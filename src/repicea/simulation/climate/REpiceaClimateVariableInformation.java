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
import java.util.HashMap;
import java.util.Map;

import repicea.simulation.covariateproviders.plotlevel.climate.AnnualFrostDaysProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.AnnualFrostFreeDaysProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.AnnualGrowingDegreeDaysCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.HighestAnnualTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.LowestAnnualTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualClimateMoistureIndexCmProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualSoilMoistureIndexPercentProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanMaximumAnnualTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanMaximumJulyTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanMinimumAnnualTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanMinimumJanuaryTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanTemperatureFromJuneToAugustCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanVapourPressureDeficitDaylightFromJuneToAugustHPaProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanVapourPressureDeficitFromJuneToAugustHPaProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.TotalAnnualPrecipitationMmProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.TotalAnnualRadiationMjM2Provider;
import repicea.simulation.covariateproviders.plotlevel.climate.TotalPrecipitationFromJuneToAugustMmProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.TotalPrecipitationFromMarchToMayMmProvider;

/**
 * A class that provide the information to produce a valid request through the BioSIMClient.
 * @author Mathieu Fortin - February 2026
 */
public class REpiceaClimateVariableInformation {

	/**
	 * An enum to identify the climate generator.
	 */
	public static enum WeatherGenerator {
		BioSIM,
	}

	/**
	 * An enum that stands for the temporal resolution of the climate variable.
	 */
	public static enum Resolution {
		Normals50Year(50),
		Normals30Year(30),
		IntervalAveragedStarting20YrsBeforeFinalMeasurement(20), 
		IntervalAveraged(0),
		Annual(0);
		final int nbYrBeforeToTarget;
		Resolution(int offset){
			this.nbYrBeforeToTarget = offset; 
		}
	}

	public static enum EvaluationDate {
		/**
		 * Typically for static models.
		 */
		Now,
		/**
		 * Typically for dynamic models. We evaluate the climate variable until the end of intervals.
		 */
		EndOfInterval;
	}
	
	/**
	 * An enum that refers to the model used in BioSIM
	 */
	public static enum BioSimModel {
		Climatic_Annual("Climatic_Annual", null),
		Climatic_Monthly("Climatic_Monthly", null),
		Climate_Mosture_Index_Annual("Climate_Mosture_Index_Annual", null),
		Growing_DegreeDay_Annual("DegreeDay_Annual", "LowerThreshold:5"),
		Soil_Moisture_Index_Annual("Soil_Moisture_Index_Annual", null),
		VaporPressureDeficit_Monthly("VaporPressureDeficit_Monthly", null),
		Normals1961_1990("Normals1961_1990", null),
		Normals1971_2000("Normals1971_2000", null),
		Normals1981_2010("Normals1981_2010", null),
		Normals1991_2020("Normals1991_2020", null);
		
		final String parameters;
		final String modelName;
		BioSimModel(String modelName, String parameters) {
			this.modelName = modelName;
			this.parameters = parameters;
		}
	}
	
	/**
	 * An enum for fast-tracking the production of REpiceaClimateVariableInformation.
	 */
	public static enum BioSimClimateVariable {
		GrowingDegreeDays(AnnualGrowingDegreeDaysCelsiusProvider.class, BioSimModel.Growing_DegreeDay_Annual, "DD"),
		
		FrostDays(AnnualFrostDaysProvider.class, BioSimModel.Climatic_Annual, "FrostDay"),
		FrostFreeDays(AnnualFrostFreeDaysProvider.class, BioSimModel.Climatic_Annual, "FrostFreeDay"),
		HighestAnnualTemperature(HighestAnnualTemperatureCelsiusProvider.class, BioSimModel.Climatic_Annual, "HitghestTmax"),
		LowestAnnualTemperature(LowestAnnualTemperatureCelsiusProvider.class, BioSimModel.Climatic_Annual, "LowestTmin"),
		MeanAnnualTemperature(MeanAnnualTemperatureCelsiusProvider.class, BioSimModel.Climatic_Annual, "MeanTair"),
		MeanMaximumAnnualTemperature(MeanMaximumAnnualTemperatureCelsiusProvider.class, BioSimModel.Climatic_Annual, "MeanTmax"),
		MeanMinimumAnnualTemperature(MeanMinimumAnnualTemperatureCelsiusProvider.class, BioSimModel.Climatic_Annual, "MeanTmin"),
		TotalAnnualPrecipitation(TotalAnnualPrecipitationMmProvider.class, BioSimModel.Climatic_Annual, "TotalPrcp"),
		TotalAnnualRadiation(TotalAnnualRadiationMjM2Provider.class, BioSimModel.Climatic_Annual, "TotalRadiation"),
		
		MeanMaximumJulyTemperature(MeanMaximumJulyTemperatureCelsiusProvider.class, BioSimModel.Climatic_Monthly, "MeanTmax", 
				new Integer[] {7}, true),	
		MeanMinimumJanuaryTemperature(MeanMinimumJanuaryTemperatureCelsiusProvider.class, BioSimModel.Climatic_Monthly, "MeanTmin", 
				new Integer[] {1}, true),
		MeanTemperatureFromJuneToAugust(MeanTemperatureFromJuneToAugustCelsiusProvider.class, BioSimModel.Climatic_Monthly, "MeanTair", 
				new Integer[] {6,7,8}, true),
		TotalPrecipitationFromJuneToAugust(TotalPrecipitationFromJuneToAugustMmProvider.class, BioSimModel.Climatic_Monthly, "TotalPrcp", 
				new Integer[] {6,7,8}, false),
		TotalPrecipitationFromMarchToMay(TotalPrecipitationFromMarchToMayMmProvider.class, BioSimModel.Climatic_Monthly, "TotalPrcp", 
				new Integer[] {3,4,5}, false),
		
		MeanVPDFromJuneToAugust(MeanVapourPressureDeficitFromJuneToAugustHPaProvider.class, BioSimModel.VaporPressureDeficit_Monthly, "VaporPressureDeficit", 
				new Integer[] {6,7,8}, true),
		MeanVPDDaylightFromJuneToAugust(MeanVapourPressureDeficitDaylightFromJuneToAugustHPaProvider.class, BioSimModel.VaporPressureDeficit_Monthly, "DaylightVPD", 
				new Integer[] {6,7,8}, true),
		
		MeanAnnualCMI(MeanAnnualClimateMoistureIndexCmProvider.class, BioSimModel.Climate_Mosture_Index_Annual, "CMI"),
		
		MeanAnnualSMI(MeanAnnualSoilMoistureIndexPercentProvider.class, BioSimModel.Soil_Moisture_Index_Annual, "SMImean");
		
		static Map<Class<? extends REpiceaClimateVariableProvider>, BioSimClimateVariable> ProviderToVariableMap;
		
		final Class<? extends REpiceaClimateVariableProvider> providerClass;
		final BioSimModel model;
		final String fieldName;
		final Integer[] months;
		final Boolean average;
		
		BioSimClimateVariable(Class<? extends REpiceaClimateVariableProvider> providerClass,
				BioSimModel model, 
				String fieldName,
				Integer[] selectedMonths,
				Boolean average) {
			this.providerClass = providerClass;
			this.model = model;
			this.fieldName = fieldName;
			this.months = selectedMonths;
			this.average = average;
		}
		
		BioSimClimateVariable(Class<? extends REpiceaClimateVariableProvider> providerClass,
				BioSimModel model, 
				String fieldName) {
			this(providerClass, model, fieldName, null, null);
		}

		
		private static synchronized Map<Class<? extends REpiceaClimateVariableProvider>, BioSimClimateVariable> getProviderToVariableMap() {
			if (ProviderToVariableMap == null) {
				ProviderToVariableMap = new HashMap<Class<? extends REpiceaClimateVariableProvider>, BioSimClimateVariable>();
				for (BioSimClimateVariable v : BioSimClimateVariable.values()) {
					ProviderToVariableMap.put(v.providerClass, v);
				}
			}
			return ProviderToVariableMap;
		}
		
		public boolean isMonthlyVariable() {return months != null;}
		
		public static REpiceaClimateVariableInformation createVariableInfo(Class<? extends REpiceaClimateVariableProvider> providerClass, Resolution resolution, EvaluationDate point) {
			return new REpiceaClimateVariableInformation(resolution, getProviderToVariableMap().get(providerClass), point);
		}
		
	}

	public final WeatherGenerator generator;
	public final Resolution resolution;
	public final BioSimModel model;
	public final String fieldName;
	public final EvaluationDate point;
	final REpiceaMonthlyClimateCompilationInformation monthCompilation;

	/**
	 * Indicate whether the climate values are expected to be on a monthly basis.<p>
	 * If not, then it is assumed that the climate values are on an annual basis.
	 * 
	 * @return a boolean (true: monthly, false: annual)
	 */
	public boolean isMonthly() {return monthCompilation != null;}
	
	/**
	 * Constructor.
	 * @param resolution a Resolution enum
	 * @param variable a BioSimVariable enum (the variable name in the model output)
	 * @param point an EvaluationPoint enum
	 */
	private REpiceaClimateVariableInformation(Resolution resolution, BioSimClimateVariable variable, EvaluationDate point) {
		this(resolution, variable.model, variable.fieldName, point, variable.months != null ? 
				new REpiceaMonthlyClimateCompilationInformation(variable.months, variable.average) : 
					null);
	}
	
	/**
	 * General constructor.
	 * @param resolution a Resolution enum
	 * @param model a BioSimModel enum
	 * @param fieldName a string 
	 * @param point an EvaluationPoint enum
	 * @param monthlyComp a MonthlyCompilationInformation instance
	 */
	public REpiceaClimateVariableInformation(Resolution resolution, BioSimModel model, String fieldName, EvaluationDate point, REpiceaMonthlyClimateCompilationInformation monthlyComp) {
		this.generator = WeatherGenerator.BioSIM;
		if (resolution == null) {
			throw new InvalidParameterException("The resolution argument cannot be null!");
		}
		this.resolution = resolution;
		if (model == null) {
			throw new InvalidParameterException("The model argument cannot be null!");
		}
		this.model = model;
		if (fieldName == null || fieldName.isEmpty()) {
			throw new InvalidParameterException("The fieldName argument must be a non empty string!");
		}
		this.fieldName = fieldName;
		if (point == null) {
			throw new InvalidParameterException("The point argument cannot be null!");
		}
		this.point = point;
		this.monthCompilation = monthlyComp;
	}

	/**
	 * Constructor for annual variable, i.e. without monthly compilation.
	 * @param resolution a Resolution enum
	 * @param model a BioSimModel enum
	 * @param fieldName a string 
	 * @param point an EvaluationPoint enum
	 */
	public REpiceaClimateVariableInformation(Resolution resolution, BioSimModel model, String fieldName, EvaluationDate point) {
		this(resolution, model, fieldName, point, null);
	}

	
	/**
	 * Fill a static map with the REpiceaClimateVariableInformation instances for a specific resolution.<p>
	 * This is a fast track method to get all the REpiceaClimateVariableInformation instances at the same resolution
	 * at once.
	 * @param oMap the static map
	 * @param plotClazz the plot class implementing some REpiceaClimateVariableProvider interfaces
	 * @param resolution a Resolution enum
	 * @param point an EvaluationPoint enum
	 */
	public static void fillClimateInfoMap(Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap,
			Class<?> plotClazz,
			Resolution resolution,
			EvaluationDate point) {
		Class<?> clazz = plotClazz;
		while (clazz != null && clazz != Object.class) {
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interfaze : interfaces) {
				if (REpiceaClimateVariableProvider.class.isAssignableFrom(interfaze)) {
					@SuppressWarnings("unchecked")
					Class<? extends REpiceaClimateVariableProvider> climateInterfaze = (Class<? extends REpiceaClimateVariableProvider>) interfaze;
					if (!oMap.containsKey(climateInterfaze)) {
						oMap.put(climateInterfaze, new HashMap<Resolution, REpiceaClimateVariableInformation>());
					}
					oMap.get(climateInterfaze).put(resolution, BioSimClimateVariable.createVariableInfo(climateInterfaze, resolution, point));
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

}
