/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2026 His Majesty the King in right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package repicea.simulation.climate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import biosimclient.BioSimClient;
import biosimclient.BioSimClientException;
import biosimclient.BioSimDataSet;
import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.RCP;
import biosimclient.BioSimParameterMap;
import biosimclient.BioSimPlot;
import biosimclient.BioSimServerException;
import biosimclient.Observation;
import repicea.simulation.climate.REpiceaClimateGenerator.RepresentativeConcentrationPathway;
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimModel;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;
import repicea.simulation.covariateproviders.plotlevel.PlotIdProvider;

/**
 * A class handling checks on climate-related methods.
 * @author Mathieu Fortin - February 2026
 */
public final class REpiceaClimateManager {

	private static final List<BioSimModel> FixedNormalsModel = Arrays.asList(new BioSimModel[] {BioSimModel.Normals1961_1990,
			BioSimModel.Normals1971_2000, BioSimModel.Normals1981_2010, BioSimModel.Normals1991_2020});
	private static final HashMap<RepresentativeConcentrationPathway, RCP> RCPLookupMap = new HashMap<RepresentativeConcentrationPathway, RCP>(); 
	static {
		RCPLookupMap.put(RepresentativeConcentrationPathway.RCP4_5, RCP.RCP45);
		RCPLookupMap.put(RepresentativeConcentrationPathway.RCP8_5, RCP.RCP85);
	}
	
	private static List<Resolution> ResolutionNeedingStartDate = Arrays.asList(new Resolution[] {Resolution.Annual, Resolution.IntervalAveraged});
		
	private final Map<String, BioSimPlot> plotMap;
	private final List<BioSimPlot> plotList;
	private final Map<String, BioSimModel> annualModels;
	private final Map<String, BioSimModel> fixedNormals;
	private Resolution dominantResolution;
	private final int lastBioSIMDailyDateYr;
	private final int nbRealizations;
	private final RepresentativeConcentrationPathway rcp;
	protected int lastDateYrInDataset;
	protected final Map<BioSimModel, Map<BioSimPlot, Map<Integer, BioSimDataSet>>> annualValueMap; 
	
	private final ConcurrentHashMap<REpiceaClimateVariableInformation, 
						ConcurrentHashMap<String, 
						ConcurrentHashMap<Integer, 
						ConcurrentHashMap<Integer, 
						ConcurrentHashMap<Integer, Double>>>>> cache; // id - fromYr - toYr- realization - value

	/**
	 * Constructor 
	 * @param rcp a RepresentativeConcentrationPathway enum
	 * @param climateInfo a List of REpiceaClimateVariableInformation instances
	 * @param plots a List of BioSimPlot instances
	 * @param nbRealizations the number of realizations (must be greater than 0)
	 * @throws BioSimClientException if an error occurs while using BioSIM WebAPI 
	 * @throws BioSimServerException if an error occurs while using BioSIM WebAPI
	 */
	public REpiceaClimateManager(RepresentativeConcentrationPathway rcp,
			List<REpiceaClimateVariableInformation> climateInfo, 
			List<BioSimPlot> plots,
			int nbRealizations) throws BioSimClientException, BioSimServerException {
		if (nbRealizations < 1) {
			throw new InvalidParameterException("The nbRealizations argument must greater than 0!");
		}
		this.nbRealizations = nbRealizations;
		if (rcp != null && !RCPLookupMap.containsKey(rcp)) {
			throw new InvalidParameterException("If not null, the rcp argument should be either RCP4_5 or RCP8_5!");
		}
		this.rcp = rcp;
		annualValueMap = new HashMap<BioSimModel, Map<BioSimPlot, Map<Integer, BioSimDataSet>>>();
		cache = new ConcurrentHashMap<REpiceaClimateVariableInformation, 
				ConcurrentHashMap<String, 
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, Double>>>>>();
		lastBioSIMDailyDateYr = BioSimClient.getLastDailyDateYr();
		plotMap = new HashMap<String, BioSimPlot>();
		for (BioSimPlot p : plots) {
			String id = ((PlotIdProvider) p).getId();
			if (plotMap.containsKey(id)) {
				throw new UnsupportedOperationException("It seems the plot list contains several plots with the same id!");
			} else {
				plotMap.put(id, p);
			}
		}
		plotList = new ArrayList<BioSimPlot>();
		plotList.addAll(plots);
		
		annualModels = new LinkedHashMap<String, BioSimModel>();
		fixedNormals = new LinkedHashMap<String, BioSimModel>();
		for (REpiceaClimateVariableInformation info : climateInfo) {
			BioSimModel model = info.model;
			Map<String, BioSimModel> currentList = FixedNormalsModel.contains(model) ?
					fixedNormals :
						annualModels;
			Resolution currentResolution = FixedNormalsModel.contains(model) ?
					null :
						info.resolution;
			if (currentResolution != null) {
				if (dominantResolution == null || currentResolution.ordinal() < dominantResolution.ordinal()) {
					dominantResolution = currentResolution;
				} 
			}
			if (!currentList.containsValue(model)) {
				currentList.put(model.modelName, model);
			}
		}
	}

	private List<BioSimParameterMap> getParameterMap() {
		List<BioSimParameterMap> outputList = new ArrayList<BioSimParameterMap>();
		for (BioSimModel m : annualModels.values()) {
			BioSimParameterMap parmMap = new BioSimParameterMap();
			if (m.parameters != null && !m.parameters.isEmpty()) {
				String[] parms = m.parameters.split("\\*");
				for (String parm : parms) {
					String[] keyValue = parm.split(":");
					if (keyValue.length > 1) {
						parmMap.addParameter(keyValue[0], keyValue[1]);
					} else {
						parmMap.addParameter(keyValue[0], ""); 
					}
				}
			}
			outputList.add(parmMap);
		}
		return outputList;
	}
	
	private static BioSimDataSet createClone(BioSimDataSet dataSet) {
		BioSimDataSet clone = new BioSimDataSet(dataSet.getFieldNames());
		for (Observation o : dataSet.getObservations()) {
			clone.addObservation(o.toArray());
		}
		clone.indexFieldType();
		return clone;
	}
	
	private static void addAllObservations(BioSimDataSet dataSet, BioSimDataSet toBeMerged) {
		for (Observation o : toBeMerged.getObservations()) {
			dataSet.addObservation(o.toArray());
		}
		dataSet.indexFieldType();
	}
	
	private static BioSimDataSet getSubDataSet(BioSimDataSet dataSet, int realization) {
		BioSimDataSet subDataSet = new BioSimDataSet(dataSet.getFieldNames());
		int fieldIndex = dataSet.getFieldNames().indexOf("Rep");
		for (Observation o : dataSet.getObservations()) {
			Object[] objectArray = o.toArray();
			int realizationId = ((Number) objectArray[fieldIndex]).intValue();
			if (realizationId == realization) {
				subDataSet.addObservation(objectArray);
			} else if (realizationId > realization) {
				break;
			}
		}
		return subDataSet;
	}


	private static int reajustFromYrDependingOnResolution(Resolution res, int from, int to) {
		if (res.ordinal() < Resolution.IntervalAveraged.ordinal()) {
			from = to - res.nbYrBeforeToTarget + 1;
		}
		return from;
	}
	
	/**
	 * Produce and store the climate variables. 
	 * @param toYr the year date at which the climate variables must be produced
	 * @throws BioSimClientException if an error occurs while using BioSIM WebAPI 
	 * @throws BioSimServerException if an error occurs while using BioSIM WebAPI
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void produceClimateVariables(final int toYr) throws BioSimClientException, BioSimServerException {
		if (toYr <= lastDateYrInDataset) {	// we are up to date
			return;
		}
		if (lastDateYrInDataset == 0 && ResolutionNeedingStartDate.contains(dominantResolution)) { // Means we haven't started the simulation and we are using allometric relationship. In that case, we do not use an interval-average or annual resolution.
			throw new UnsupportedOperationException("The dominant resolution " + dominantResolution.name() + " requires an upper range date!");
		}
		int fromYr = reajustFromYrDependingOnResolution(dominantResolution, lastDateYrInDataset + 1, toYr);
		if (fromYr < lastDateYrInDataset + 1) {	// we dont need to go that far if the upper range is already more recent
			fromYr = lastDateYrInDataset + 1;
		}
		if (fromYr < lastBioSIMDailyDateYr && toYr > lastBioSIMDailyDateYr) {
			lastDateYrInDataset = fromYr - 1;
			produceClimateVariables(lastBioSIMDailyDateYr);
			produceClimateVariables(toYr);
		} else {
			boolean isBeyondLastDailyDateYr = fromYr > lastBioSIMDailyDateYr; 
			List<String> modelList = new ArrayList<String>(annualModels.keySet());
			List<BioSimParameterMap> parmsMap = getParameterMap();
			LinkedHashMap<String, Object> result = BioSimClient.generateWeather(fromYr, toYr, plotList, RCPLookupMap.get(rcp), ClimateModel.RCM4, modelList, isBeyondLastDailyDateYr ? nbRealizations : 1, parmsMap);
//			System.out.println("BioSIM request took " + BioSimClient.getLastServerRequestDuration() + " sec.");
			for (String modelName : result.keySet()) {
				BioSimModel model = annualModels.get(modelName);
				if (!annualValueMap.containsKey(model)) {
					annualValueMap.put(model, new HashMap<BioSimPlot, Map<Integer, BioSimDataSet>>());
				}
				Map<BioSimPlot, Map<Integer, BioSimDataSet>> innerMap = annualValueMap.get(model);
				LinkedHashMap<BioSimPlot, BioSimDataSet> innerResultMap = (LinkedHashMap) result.get(modelName);
				for (BioSimPlot p : innerResultMap.keySet()) {
					if (!innerMap.containsKey(p)) {
						innerMap.put(p, new HashMap<Integer, BioSimDataSet>());
					} 
					Map<Integer, BioSimDataSet> innerInnerMap = innerMap.get(p);
					BioSimDataSet dataSet = innerResultMap.get(p);
					for (int i = 0; i < nbRealizations; i++) {
						BioSimDataSet dataSetForThisRealization = isBeyondLastDailyDateYr ? 
								getSubDataSet(dataSet, i) :
									createClone(dataSet);
						if (!innerInnerMap.containsKey(i)) {
							innerInnerMap.put(i, dataSetForThisRealization);
						} else {
							addAllObservations(innerInnerMap.get(i), dataSetForThisRealization);
						}
						
					}
				}
			}
			lastDateYrInDataset = toYr;
			// TODO MF20260217 faire les normals ici
		}
	}
	
	
	private Double getCachedValue(REpiceaClimateVariableInformation info, String plotId, int fromYr, int toYr, int realization) {
		if (cache.containsKey(info)) {
			ConcurrentHashMap<String, 
			ConcurrentHashMap<Integer, 
			ConcurrentHashMap<Integer, 
			ConcurrentHashMap<Integer, Double>>>> innerMap1 = cache.get(info);
			if (innerMap1.contains(plotId)) {
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, Double>>> innerMap2 = innerMap1.get(plotId);
				if (innerMap2.contains(fromYr)) {
					ConcurrentHashMap<Integer, 
					ConcurrentHashMap<Integer, Double>> innerMap3 = innerMap2.get(fromYr);
					if (innerMap3.contains(toYr)) {
						ConcurrentHashMap<Integer, Double> innerMap4 = innerMap3.get(toYr);
						if (innerMap4.contains(realization)) {
							return innerMap4.get(realization);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Return a value for a particular climate variable.<p>
	 * The variable and its resolution are defined through the info argument.
	 * @param fromYr the start date (yr)
	 * @param toYr the end date (yr)
	 * @param realization the realization id
	 * @param plotId the plot id
	 * @param info an REpiceaClimateVariableInformation instance 
	 * @return the value of the climate variable (double)
	 */
	public double getValue(int fromYr, 
			int toYr, 
			int realization, 
			String plotId,
			REpiceaClimateVariableInformation info) {
		fromYr = reajustFromYrDependingOnResolution(info.resolution, fromYr, toYr);
		Double cachedValue = getCachedValue(info, plotId, fromYr, toYr, realization);
		if (cachedValue == null) {
			BioSimPlot p = plotMap.get(plotId);
			BioSimDataSet dataSet = annualValueMap.get(info.model).get(p).get(realization);
			int indexField = dataSet.getFieldNames().indexOf(info.fieldName);
			int indexYear = dataSet.getFieldNames().indexOf("Year");
			double total = 0d;
			for (Observation o : dataSet.getObservations()) {
				Object[] objectArray = o.toArray();
				int currentDateYr = ((Number) objectArray[indexYear]).intValue();
				if (currentDateYr >= fromYr && currentDateYr <= toYr) {
					total += ((Number) objectArray[indexField]).doubleValue();
				} else if (currentDateYr > toYr) {
					break;
				}
			}
			double mean = total / (toYr - fromYr + 1);
			storeValueInCache(info, plotId, fromYr, toYr, realization, mean);
			return mean;
		} else {
			return cachedValue;
		}
		
	}

	private void storeValueInCache(REpiceaClimateVariableInformation info, 
			String plotId, 
			int fromYr, 
			int toYr,
			int realization, 
			double mean) {
		if (!cache.containsKey(info)) {
			cache.put(info, new ConcurrentHashMap<String, 
								ConcurrentHashMap<Integer, 
								ConcurrentHashMap<Integer, 
								ConcurrentHashMap<Integer, Double>>>>());
		}
		ConcurrentHashMap<String, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Double>>>> innerMap1 = cache.get(info);
		if (!innerMap1.containsKey(plotId)) {
			innerMap1.put(plotId, new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Double>>>());
		}
		ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Double>>> innerMap2 = innerMap1.get(plotId);
		if (!innerMap2.containsKey(fromYr)) {
			innerMap2.put(fromYr, new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Double>>());
		}
		ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Double>> innerMap3 = innerMap2.get(fromYr);
		if (!innerMap3.containsKey(toYr)) {
			innerMap3.put(toYr, new ConcurrentHashMap<Integer, Double>());
		}
		ConcurrentHashMap<Integer, Double> innerMap4 = innerMap3.get(toYr);
		innerMap4.put(realization, mean);
	}

	
}
