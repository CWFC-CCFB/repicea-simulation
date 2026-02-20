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
import biosimclient.BioSimEnums.Period;
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
 * A class handling the production of climate variables.
 * @author Mathieu Fortin - February 2026
 */
public final class REpiceaClimateManager {

	private static final HashMap<RepresentativeConcentrationPathway, RCP> RCPLookupMap = new HashMap<RepresentativeConcentrationPathway, RCP>(); 
	static {
		RCPLookupMap.put(RepresentativeConcentrationPathway.RCP4_5, RCP.RCP45);
		RCPLookupMap.put(RepresentativeConcentrationPathway.RCP8_5, RCP.RCP85);
	}
	private static final Map<BioSimModel, Period> PeriodLookupMap = new HashMap<BioSimModel, Period>();
	static {
		PeriodLookupMap.put(BioSimModel.Normals1961_1990, Period.FromNormals1961_1990);
		PeriodLookupMap.put(BioSimModel.Normals1971_2000, Period.FromNormals1971_2000);
		PeriodLookupMap.put(BioSimModel.Normals1981_2010, Period.FromNormals1981_2010);
		PeriodLookupMap.put(BioSimModel.Normals1991_2020, Period.FromNormals1991_2020);
	}
	
	private static List<Resolution> ResolutionNeedingStartDate = Arrays.asList(new Resolution[] {Resolution.Annual, Resolution.IntervalAveraged});
		
	private final Map<String, BioSimPlot> plotMap; // to map the plot from its id
	private final List<BioSimPlot> plotList; // the list of original plot so that we don't need to provide BioSimPlot instance over and over again
	final Map<String, BioSimModel> annualModels;
	final Map<String, BioSimModel> fixedNormalModels;
	private Resolution dominantResolution;
	private final int lastBioSIMDailyDateYr; // the last daily date on BioSIM WebAPI
	private final int nbRealizations; // the number of realizations in the growth simulation
	private final RepresentativeConcentrationPathway rcp;
	protected int lastDateYrInDataset; // the last date yr in the annualValueMap
	protected final Map<BioSimModel, Map<BioSimPlot, Map<Integer, BioSimDataSet>>> annualValueMap; 
	protected final Map<BioSimModel, Map<BioSimPlot, BioSimDataSet>> fixedNormals;
	private boolean staticNormalsProduced = false; // a boolean to make sure the fixed normals are retrieved only once
	private final ClimateModel climModel = ClimateModel.RCM4; // the climate model
	
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
		fixedNormals = new HashMap<BioSimModel, Map<BioSimPlot, BioSimDataSet>>();
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
		fixedNormalModels = new LinkedHashMap<String, BioSimModel>();
		for (REpiceaClimateVariableInformation info : climateInfo) {
			BioSimModel model = info.model;
			Map<String, BioSimModel> currentList = isFixedNormalsModel(model) ?
					fixedNormalModels :
						annualModels;
			Resolution currentResolution = isFixedNormalsModel(model) ?
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

	private boolean isFixedNormalsModel(BioSimModel model) {
		return PeriodLookupMap.containsKey(model);
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
			LinkedHashMap<String, Object> result = BioSimClient.generateWeather(fromYr, 
					toYr, 
					plotList, 
					RCPLookupMap.get(rcp), 
					climModel, 
					modelList, 
					isBeyondLastDailyDateYr ? nbRealizations : 1, 
					parmsMap);
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
									dataSet.clone();
						if (!innerInnerMap.containsKey(i)) {
							innerInnerMap.put(i, dataSetForThisRealization);
						} else {
							innerInnerMap.get(i).addAllObservations(dataSetForThisRealization);
						}
						
					}
				}
			}
			lastDateYrInDataset = toYr;
			if (!staticNormalsProduced) {
				for (BioSimModel model : fixedNormalModels.values()) {
					LinkedHashMap<BioSimPlot, BioSimDataSet> normalResult = BioSimClient.getAnnualNormals(PeriodLookupMap.get(model), 
							plotList, 
							RCPLookupMap.get(rcp),
							climModel);
					for (BioSimDataSet ds : normalResult.values()) {
						computeMeanTairInNormals(ds);
					}
					fixedNormals.put(model, normalResult); 
				}
				staticNormalsProduced = true;
			}
		}
	}
	
	private void computeMeanTairInNormals(BioSimDataSet dataset) {
		if (!dataset.getFieldNames().contains("T") && dataset.getFieldNames().contains("TN") && dataset.getFieldNames().contains("TX")) {
			int indexTn = dataset.getFieldNames().indexOf("TN");
			int indexTx = dataset.getFieldNames().indexOf("TX");
			List<Object> TN = dataset.getFieldValues(indexTn);
			List<Object> TX = dataset.getFieldValues(indexTx);
			Object[] T = new Object[TX.size()];
			for (int i = 0; i < T.length; i++) {
				T[i] = ((Double) TX.get(i) + (Double) TN.get(i)) * .5;
			}
			dataset.addField("T", T);
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
		BioSimPlot p = plotMap.get(plotId);
		if (isFixedNormalsModel(info.model)) {
			BioSimDataSet ds = fixedNormals.get(info.model).get(p);
			int fieldIndex = ds.getFieldNames().indexOf(info.fieldName);
			if (fieldIndex == -1) {
				throw new UnsupportedOperationException("The field " + info.fieldName + " cannot be found in the BioSimDataSet instance!");
			}
			List<Object> values = ds.getFieldValues(fieldIndex);
			if (values.size() != 1) {
				throw new UnsupportedOperationException("There should be only one value in this field " + info.fieldName + "!");
			}
			return (Double) values.get(0);
		} else {
			fromYr = reajustFromYrDependingOnResolution(info.resolution, fromYr, toYr);
			Double cachedValue = getCachedValue(info, plotId, fromYr, toYr, realization);
			if (cachedValue == null) {
//				BioSimPlot p = plotMap.get(plotId);
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
