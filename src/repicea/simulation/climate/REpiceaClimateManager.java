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
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimClimateVariable;
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimModel;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;
import repicea.simulation.covariateproviders.plotlevel.PlotIdProvider;

/**
 * A class handling the production of climate variables.<p>
 * This class is thread safe. The unique public method is synchronized.
 * 
 * @author Mathieu Fortin - February 2026
 */
public final class REpiceaClimateManager {

	
	private static final String YEAR_DATE_FIELDNAME = "Year";
	private static final String MONTH_DATE_FIELDNAME = "Month";

	@SuppressWarnings("serial")
	static class UniqueBioSimPlot implements BioSimPlot {

		
		final double elevationM;
		final double latitudeDeg;
		final double longitudeDeg;

		UniqueBioSimPlot(double[] roundedCoordinates) {
			this.latitudeDeg = roundedCoordinates[0];
			if (Double.isNaN(latitudeDeg)) {
				throw new InvalidParameterException("The latitude cannot be a NaN!");
			}
			this.longitudeDeg = roundedCoordinates[1];
			if (Double.isNaN(longitudeDeg)) {
				throw new InvalidParameterException("The latitude cannot be a NaN!");
			}
			this.elevationM = roundedCoordinates[2];
		}
		
		@Override
		public double getElevationM() {return elevationM;}

		@Override
		public double getLatitudeDeg() {return latitudeDeg;}

		@Override
		public double getLongitudeDeg() {return longitudeDeg;}
		
		String getUniqueId() {
			return latitudeDeg + "_" + longitudeDeg + "_" + elevationM; 
		}
	}
	
	
	private static final HashMap<RepresentativeConcentrationPathway, RCP> RCPLookupMap = new HashMap<RepresentativeConcentrationPathway, RCP>(); 
	static {
		RCPLookupMap.put(RepresentativeConcentrationPathway.NO_CHANGE, RCP.CONSTANT_CLIMATE);
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
		
	private final Map<String, BioSimPlot> plotMap; // to map the original plot id to the unique BioSimPlot instances
	final List<BioSimPlot> uniquePlotList; // the list of unique plot so that we don't need to provide BioSimPlot instance over and over again
	final Map<String, BioSimModel> annualOrMonthlyModels;
	final Map<String, BioSimModel> fixedNormalModels;
	private Resolution dominantResolution;
	private final int lastBioSIMCompleteObservedDailyDateYr; // the last daily date on BioSIM WebAPI
	private final int nbRealizations; // the number of realizations in the growth simulation
	private final RepresentativeConcentrationPathway rcp;
	protected int lastDateYrInDataset; // the last date yr in the annualValueMap
	protected final Map<BioSimModel, Map<BioSimPlot, Map<Integer, BioSimDataSet>>> annualOrMonthlyValueMap; 
	protected final Map<BioSimModel, Map<BioSimPlot, BioSimDataSet>> fixedNormals;
	private boolean staticNormalsProduced = false; // a boolean to make sure the fixed normals are retrieved only once
	private final ClimateModel climModel = ClimateModel.RCM4; // the climate model
	
	private final double latitudeResolution;
	private final double longitudeResolution;
	private final double elevationResolution;
	
	private final ConcurrentHashMap<REpiceaClimateVariableInformation, 
						ConcurrentHashMap<String, 
						ConcurrentHashMap<Integer, 
						ConcurrentHashMap<Integer, 
						ConcurrentHashMap<Integer, Double>>>>> cache; // id - fromYr - toYr- realization - value

	
	static UniqueBioSimPlot roundCoordinates(BioSimPlot p, double latitudeResolution, double longitudeResolution, double elevationResolution) {
		double[] roundedCoordinates = new double[3];
		roundedCoordinates[0] = latitudeResolution > 0d ?
				Math.round(p.getLatitudeDeg() / latitudeResolution) * latitudeResolution :
					p.getLatitudeDeg(); 
		roundedCoordinates[1] = longitudeResolution > 0d ? 
				Math.round(p.getLongitudeDeg() / longitudeResolution) * longitudeResolution :
					p.getLongitudeDeg();
		double plotElevationM = p.getElevationM();
		if (Double.isNaN(plotElevationM)) {
			roundedCoordinates[2] = Double.NaN;	// make sure we keep the Double.NaN.
		} else {
			roundedCoordinates[2] = elevationResolution > 0d ?
	 				Math.round(p.getElevationM() / elevationResolution) * elevationResolution :
						p.getElevationM();
		}
		return new UniqueBioSimPlot(roundedCoordinates);
	}
	
	
	/**
	 * Constructor.<p>
	 * If the resolutions are set to 0, then the true coordinates are used. Otherwise,
	 * the coordinates are rounded at the resolution level and used afterwards. Original 
	 * plot ids are mapped to the rounded coordinates. 
	 * @param rcp a RepresentativeConcentrationPathway enum
	 * @param climateInfo a List of REpiceaClimateVariableInformation instances
	 * @param plots a List of BioSimPlot instances
	 * @param latitudeResolution the latitude resolution (within range [0,1])
	 * @param longitudeResolution the longitude resolution (within range [0,1])
	 * @param elevationResolution the elevation resolution (within range [0,100])
	 * @param nbRealizations the number of realizations (must be greater than 0)
	 * @throws BioSimClientException if an error occurs while using BioSIM WebAPI 
	 * @throws BioSimServerException if an error occurs while using BioSIM WebAPI
	 */
	public REpiceaClimateManager(RepresentativeConcentrationPathway rcp,
			List<REpiceaClimateVariableInformation> climateInfo, 
			List<BioSimPlot> plots,
			double latitudeResolution,
			double longitudeResolution,
			double elevationResolution,
			int nbRealizations) throws BioSimClientException, BioSimServerException {
		if (latitudeResolution < 0 || latitudeResolution > 1) {
			throw new InvalidParameterException("The latitudeResolution argument should be with the range [0,1]!");
		}
		this.latitudeResolution = latitudeResolution; 
		if (longitudeResolution < 0 || longitudeResolution > 1) {
			throw new InvalidParameterException("The longitudeResolution argument should be with the range [0,1]!");
		}
		this.longitudeResolution = longitudeResolution; 
		if (elevationResolution < 0 || elevationResolution > 100) {
			throw new InvalidParameterException("The latitudeResolution argument should be with the range [0,100]!");
		}
		this.elevationResolution = elevationResolution;
		if (nbRealizations < 1) {
			throw new InvalidParameterException("The nbRealizations argument must greater than 0!");
		}
		this.nbRealizations = nbRealizations;
		if (rcp != null && !RCPLookupMap.containsKey(rcp)) {
			throw new InvalidParameterException("If not null, the rcp argument should be either RCP4_5 or RCP8_5!");
		}
		this.rcp = rcp;
		annualOrMonthlyValueMap = new HashMap<BioSimModel, Map<BioSimPlot, Map<Integer, BioSimDataSet>>>();
		fixedNormals = new HashMap<BioSimModel, Map<BioSimPlot, BioSimDataSet>>();
		cache = new ConcurrentHashMap<REpiceaClimateVariableInformation, 
				ConcurrentHashMap<String, 
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, Double>>>>>();
		lastBioSIMCompleteObservedDailyDateYr = BioSimClient.getLastDailyDateYr() - 1;
		plotMap = new HashMap<String, BioSimPlot>();
		Map<String, BioSimPlot> uniquePlotMap = new HashMap<String, BioSimPlot>();
		for (BioSimPlot p : plots) {
			String id = ((PlotIdProvider) p).getId();
			if (plotMap.containsKey(id)) {
				throw new UnsupportedOperationException("It seems the plot list contains several plots with the same id!");
			} else {
				UniqueBioSimPlot uPlot = roundCoordinates(p, 
						this.latitudeResolution, 
						this.longitudeResolution, 
						this.elevationResolution);
				String uniqueId = uPlot.getUniqueId();
				if (!uniquePlotMap.containsKey(uniqueId)) {
					uniquePlotMap.put(uniqueId, uPlot);
				}
				plotMap.put(id, uniquePlotMap.get(uniqueId));
			}
		}
		uniquePlotList = new ArrayList<BioSimPlot>();
		uniquePlotList.addAll(uniquePlotMap.values());
		
		annualOrMonthlyModels = new LinkedHashMap<String, BioSimModel>();
		fixedNormalModels = new LinkedHashMap<String, BioSimModel>();
		for (REpiceaClimateVariableInformation info : climateInfo) {
			BioSimModel model = info.model;
			Map<String, BioSimModel> currentList = isFixedNormalsModel(model) ?
					fixedNormalModels :
						annualOrMonthlyModels;
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

	/**
	 * Constructor.<p>
	 * Constructor with original plot coordinates, that is without rounding.
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
		this(rcp, climateInfo, plots, 0, 0, 0, nbRealizations);
	}
	
	
	private boolean isFixedNormalsModel(BioSimModel model) {
		return PeriodLookupMap.containsKey(model);
	}
	
	private List<BioSimParameterMap> getParameterMap() {
		List<BioSimParameterMap> outputList = new ArrayList<BioSimParameterMap>();
		for (BioSimModel m : annualOrMonthlyModels.values()) {
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
			from = to - res.nbYrBeforeToTarget;
		}
		return from;
	}
	
	
	/**
	 * Produce and store the climate variables. <p>
	 * The start date is set in function of the resolution.
	 * @param toYr the year date at which the climate variables must be produced
	 * @throws BioSimClientException if an error occurs while using BioSIM WebAPI 
	 * @throws BioSimServerException if an error occurs while using BioSIM WebAPI
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void produceClimateVariables(final int toYr) throws Exception {
		if (toYr <= lastDateYrInDataset) {	// we are up to date
			return;
		}
		if (lastDateYrInDataset == 0 && ResolutionNeedingStartDate.contains(dominantResolution)) { // Means we haven't started the simulation and we are using allometric relationship. In that case, we do not use an interval-average or annual resolution.
			throw new UnsupportedOperationException("The dominant resolution " + dominantResolution.name() + " requires an upper range date!");
		}
		int fromYr = reajustFromYrDependingOnResolution(dominantResolution, lastDateYrInDataset, toYr);
		if (fromYr < lastDateYrInDataset) {	// we dont need to go that far if the upper range is already more recent
			fromYr = lastDateYrInDataset;
		}
		if (fromYr < lastBioSIMCompleteObservedDailyDateYr && toYr > lastBioSIMCompleteObservedDailyDateYr) {
			lastDateYrInDataset = fromYr;
			produceClimateVariables(lastBioSIMCompleteObservedDailyDateYr);
			produceClimateVariables(toYr);
		} else {
			boolean isBeyondLastDailyDateYr = fromYr >= lastBioSIMCompleteObservedDailyDateYr; 
			List<String> modelList = new ArrayList<String>(annualOrMonthlyModels.keySet());
			List<BioSimParameterMap> parmsMap = getParameterMap();
			int nbAttempts = 0;
			LinkedHashMap<String, Object> result = null;
			boolean isResultValid;
			do {
				result = BioSimClient.generateWeather(
						fromYr + 1,
						toYr, 
						uniquePlotList, 
						RCPLookupMap.get(rcp), 
						climModel, 
						modelList, 
						isBeyondLastDailyDateYr ? nbRealizations : 1, 
								parmsMap);
				nbAttempts++;
				isResultValid = true;
				for (Object o : result.values()) {
					if (!(o instanceof LinkedHashMap)) {
						isResultValid = false;
						break;
					}
				}
				if (!isResultValid && nbAttempts < 3) {
					System.out.println("Something went wrong with climate generation!" + System.lineSeparator() + 
							"This might be due to the internet connection. Will try once more...");
				}
			} while (!isResultValid && nbAttempts < 3);
//			System.out.println("BioSIM request took " + BioSimClient.getLastServerRequestDuration() + " sec.");
			for (String modelName : result.keySet()) {
				BioSimModel model = annualOrMonthlyModels.get(modelName);
				if (!annualOrMonthlyValueMap.containsKey(model)) {
					annualOrMonthlyValueMap.put(model, new HashMap<BioSimPlot, Map<Integer, BioSimDataSet>>());
				}
				Map<BioSimPlot, Map<Integer, BioSimDataSet>> innerMap = annualOrMonthlyValueMap.get(model);
				Object resultForThisModel = result.get(modelName);
				if (resultForThisModel instanceof Exception) {
					throw (Exception) resultForThisModel;
				}
				LinkedHashMap<BioSimPlot, BioSimDataSet> innerResultMap = (LinkedHashMap) resultForThisModel;
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
							uniquePlotList, 
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
			if (innerMap1.containsKey(plotId)) {
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, 
				ConcurrentHashMap<Integer, Double>>> innerMap2 = innerMap1.get(plotId);
				if (innerMap2.containsKey(fromYr)) {
					ConcurrentHashMap<Integer, 
					ConcurrentHashMap<Integer, Double>> innerMap3 = innerMap2.get(fromYr);
					if (innerMap3.containsKey(toYr)) {
						ConcurrentHashMap<Integer, Double> innerMap4 = innerMap3.get(toYr);
						if (innerMap4.containsKey(realization)) {
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
	 * 
	 * The method calls produce the climate variables on the fly. The variable and its resolution are defined 
	 * through the info argument.<p> 
	 * 
	 * IMPORTANT: The fromYr argument is not inclusive. If fromYr = 2010 and toYr = 2020, the annual variable
	 * will be calculated over the period 2011 to 2020.
	 * 
	 * @param fromYr the start date (yr, exclusive) 
	 * @param toYr the end date (yr, inclusive)
	 * @param realization the realization id
	 * @param plotId the plot id
	 * @param info an REpiceaClimateVariableInformation instance 
	 * @return the value of the climate variable (double)
	 * @throws Exception if an error occurs on the client or server side while using BioSIM WebAPI 
	 */
	public synchronized double getValue(int fromYr, 
			int toYr, 
			int realization, 
			String plotId,
			REpiceaClimateVariableInformation info) throws Exception {
		produceClimateVariables(toYr);
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
				BioSimDataSet dataSet = annualOrMonthlyValueMap.get(info.model).get(p).get(realization);
				int indexField = dataSet.getFieldNames().indexOf(info.fieldName);
				int yearFieldIndex = dataSet.getFieldNames().indexOf(YEAR_DATE_FIELDNAME);
				List<Object> yearIndex = dataSet.getFieldValues(yearFieldIndex);
				double total = 0d;
				if (!info.isMonthly()) {
					for (int yr = fromYr + 1; yr <= toYr; yr++) {
						int indexObs = yearIndex.indexOf(yr);
						if (indexObs == -1) {
							throw new UnsupportedOperationException("The date " + yr + " is not in the BioSimDataSet instance!");
						}
						Object[] objectArray = dataSet.getObservations().get(indexObs).toArray();
//						int currentDateYr = ((Number) objectArray[indexYear]).intValue();
						total += ((Number) objectArray[indexField]).doubleValue();
					}
				} else {
					int monthFieldIndex = dataSet.getFieldNames().indexOf(MONTH_DATE_FIELDNAME);
					List<Object> monthIndex = dataSet.getFieldValues(monthFieldIndex);
					List<Integer> selectedMonths = info.monthCompilation.selectedMonths;
					for (int yr = fromYr + 1; yr <= toYr; yr++) {
						int startIndex = yearIndex.indexOf(yr);
						int stopIndex = yearIndex.lastIndexOf(yr);
						if (startIndex == -1) {
							throw new UnsupportedOperationException("The date " + yr + " is not in the BioSimDataSet instance!");
						}
						double annualValue = 0;
						int nbValues = 0;
						for (int pointer = startIndex; pointer <= stopIndex; pointer++) {
							if (selectedMonths.contains(monthIndex.get(pointer))) {
								Object[] objectArray = dataSet.getObservations().get(pointer).toArray();
								annualValue += ((Number) objectArray[indexField]).doubleValue();
								nbValues++;
							}
						}
						if (nbValues != selectedMonths.size()) {
							throw new UnsupportedOperationException("It seems that some monthly values are missing!");
						}
						if (info.monthCompilation.isAverage && selectedMonths.size() > 1) { // average is required only there are more than one monthly value.
							annualValue /= nbValues; 
						}
						total += annualValue;
					}					
				}
				double mean = total / (toYr - fromYr);
				storeValueInCache(info, plotId, fromYr, toYr, realization, mean);
				return mean;
			} else {
				return cachedValue;
			}
		}
	}
	
	/**
	 * Provide lists of dates and annual values over a time period.<p>
	 * This method is typically called after the simulation to retrieve some climate variables.
	 * @param variable a BioSimClimateVariable enum
	 * @param plotId the id of the plot
	 * @param realization the realization id
	 * @param startDate the start date (yr) inclusive
	 * @param endDate the end date (yr) inclusive
	 * @return two lists of Double instances, the first of which contains the dates whereas the second contains the annual climate variable
	 */
	@SuppressWarnings({ "unchecked" })
	public List<Double>[] getAnnualValues(BioSimClimateVariable variable, String plotId, int realization, int startDate, int endDate) {
		if (variable.isMonthlyVariable()) {
			throw new UnsupportedOperationException("Monthly variables are not supported!");
		}
		if (!annualOrMonthlyValueMap.containsKey(variable.model)) {
			throw new InvalidParameterException("This climate variable have not been produced yet!");
		}
		Map<BioSimPlot, Map<Integer, BioSimDataSet>> innerMap = annualOrMonthlyValueMap.get(variable.model);
		BioSimPlot p = plotMap.get(plotId);
		if (p == null) {
			throw new InvalidParameterException("This plot " + plotId + " is not being considered in the climate manager!");
		}
		Map<Integer, BioSimDataSet> innerInnerMap = innerMap.get(p);
		if (!innerInnerMap.containsKey(realization)) {
			throw new InvalidParameterException("This realization " + realization + " is not being considered in the climate manager!");
		}
		BioSimDataSet ds = innerInnerMap.get(realization);
		List<Object> dates = ds.getFieldValues(ds.getFieldNames().indexOf(YEAR_DATE_FIELDNAME));
		List<Object> values = ds.getFieldValues(ds.getFieldNames().indexOf(variable.fieldName));
		int startIndex = dates.indexOf(startDate);
		int endIndex = dates.lastIndexOf(endDate);
		if (startIndex == -1) {
			throw new InvalidParameterException("The start date is not found in the BioSimDataSet instance");
		}
		if (endIndex == -1) {
			throw new InvalidParameterException("The end date is not found in the BioSimDataSet instance");
		}
		List<Double>[] output = new List[2];
		output[0] = new ArrayList<Double>();
		output[1] = new ArrayList<Double>();
		for (int index = startIndex; index <= endIndex; index++) {
			output[0].add(((Number) dates.get(index)).doubleValue());
			output[1].add(((Number) values.get(index)).doubleValue());
		}
		return output;
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
