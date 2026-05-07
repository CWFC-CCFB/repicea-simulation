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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimPlot;
import biosimclient.BioSimPlotImpl;
import repicea.simulation.ClimateSensitivePredictor;
import repicea.simulation.climate.REpiceaClimateGenerator.RepresentativeConcentrationPathway;
import repicea.simulation.climate.REpiceaClimateManager.UniqueBioSimPlot;
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimClimateVariable;
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimModel;
import repicea.simulation.climate.REpiceaClimateVariableInformation.EvaluationDate;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;
import repicea.simulation.covariateproviders.plotlevel.PlotIdProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.AnnualGrowingDegreeDaysCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualClimateMoistureIndexCmProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualSoilMoistureIndexPercentProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualTemperatureCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanTemperatureFromJuneToAugustCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.TotalPrecipitationFromJuneToAugustMmProvider;

public class REpiceaClimateManagerTest {

	@SuppressWarnings("serial")
	static class Plot implements PlotIdProvider, 
								BioSimPlot, 
								ClimateSensitivePredictor,
								MeanAnnualTemperatureCelsiusProvider {

		private static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, Plot.class, Resolution.IntervalAveraged, EvaluationDate.EndOfInterval);
		}
		
		final double latitude;
		final double longitude;
		final double altitude;
		final String id;
		
		Plot(String id, double latitude, double longitude, double altitude) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
			this.altitude = altitude;
		}
		
		
		@Override
		public double getElevationM() {return altitude;}

		@Override
		public double getLatitudeDeg() {return latitude;}

		@Override
		public double getLongitudeDeg() {return longitude;}

		@Override
		public String getId() {return id;}


		@Override
		public double getMeanAnnualTemperatureCelsius(REpiceaClimateVariableInformation info) {return 0;}


		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}
		
	}
	
	
	@SuppressWarnings("serial")
	static class ExtendedPlot extends Plot implements AnnualGrowingDegreeDaysCelsiusProvider {

		private static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement, EvaluationDate.EndOfInterval);
		}
		
		ExtendedPlot(String id, double latitude, double longitude, double altitude) {
			super(id, latitude, longitude, altitude);
		}
		
		
		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}


		@Override
		public double getGrowingDegreeDaysCelsius(REpiceaClimateVariableInformation info) {
			return 0;
		}


		
	}

	
	@SuppressWarnings("serial")
	static class ExtendedPlot2 extends Plot implements MeanTemperatureFromJuneToAugustCelsiusProvider {

		static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot2.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement, EvaluationDate.EndOfInterval);
		}
		
		ExtendedPlot2(String id, double latitude, double longitude, double altitude) {
			super(id, latitude, longitude, altitude);
		}
		
		
		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}

		@Override
		public double getMeanTemperatureFromJuneToAugustCelsius(REpiceaClimateVariableInformation info) {
			return 0;
		}
	}

	
	@SuppressWarnings("serial")
	static class ExtendedPlot3 extends Plot implements TotalPrecipitationFromJuneToAugustMmProvider {

		static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot3.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement, EvaluationDate.EndOfInterval);
		}
		
		ExtendedPlot3(String id, double latitude, double longitude, double altitude) {
			super(id, latitude, longitude, altitude);
		}
		
		
		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}


		@Override
		public double getTotalPrecipitationFromJuneToAugustMm(REpiceaClimateVariableInformation info) {
			return 0;
		}

	}

	
	@SuppressWarnings("serial")
	static class PlotWithNormals extends ExtendedPlot  {

		private static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement, EvaluationDate.EndOfInterval);
			CLIMATE_INFO.get(MeanAnnualTemperatureCelsiusProvider.class).put(Resolution.Normals30Year,  
					new REpiceaClimateVariableInformation(Resolution.Normals30Year, BioSimModel.Normals1971_2000, "T", EvaluationDate.Now, null));
		}
		
		PlotWithNormals(String id, double latitude, double longitude, double altitude) {
			super(id, latitude, longitude, altitude);
		}
		
		
		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}


		@Override
		public double getGrowingDegreeDaysCelsius(REpiceaClimateVariableInformation info) {
			return 0;
		}
	}

	
	@Test
	public void test01ClimateGenerationHappyPathOneRealizationOverOneInterval() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new Plot("01", 46, -75, 120));
		plots.add(new Plot("02", 47, -76, 220));
		plots.add(new Plot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = Plot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 1);
		try {
			manager.produceClimateVariables(2010);
			Assert.fail("Should have thrown an exception!");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
		Thread.sleep(500);
		System.out.println("Relax this error was expected!");

		manager.lastDateYrInDataset = 2000;
		manager.produceClimateVariables(2010);
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 1, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		double value = manager.getValue(2000, 2010, 0, ((PlotIdProvider) plots.get(0)).getId(), infos.get(0));
		Assert.assertEquals("Testing interval averaged value", 5.38, value, 1E-8);
	}

	@Test
	public void test02ClimateGenerationHappyPathFiveRealizationsBeforeLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new Plot("01", 46, -75, 120));
		plots.add(new Plot("02", 47, -76, 220));
		plots.add(new Plot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = Plot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 5);
		manager.lastDateYrInDataset = 2000;
		manager.produceClimateVariables(2010);
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}

	@Test
	public void test03ClimateGenerationHappyPathFiveRealizationsAfterLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new Plot("01", 46, -75, 120));
		plots.add(new Plot("02", 47, -76, 220));
		plots.add(new Plot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = Plot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 5);
		manager.lastDateYrInDataset = 2030;
		manager.produceClimateVariables(2040);
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}

	
	@Test
	public void test04ClimateGenerationHappyPathFiveRealizationsOverlappingLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new Plot("01", 46, -75, 120));
		plots.add(new Plot("02", 47, -76, 220));
		plots.add(new Plot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = Plot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 5);
		manager.lastDateYrInDataset = 2020;
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 10, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
	}

	
	
	@Test
	public void test05ClimateGenerationHappyPathExtendedPlotsOneRealizationBeforeLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot("01", 46, -75, 120));
		plots.add(new ExtendedPlot("02", 47, -76, 220));
		plots.add(new ExtendedPlot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 1);
		manager.produceClimateVariables(2010); // should work because the resolution is not annual nor interval averaged
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 1, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
		Assert.assertEquals("Testing last date year in dataset", 2010, manager.lastDateYrInDataset);
		manager.produceClimateVariables(2020); // should work because the resolution is not annual nor interval averaged
		Assert.assertEquals("Testing nb observations in each dataset", 30, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
		Assert.assertEquals("Testing last date year in dataset", 2020, manager.lastDateYrInDataset);
	}
	
	
	@Test
	public void test06ClimateGenerationHappyPathExtendedPlotsFiveRealizationsBeforeLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot("01", 46, -75, 120));
		plots.add(new ExtendedPlot("02", 47, -76, 220));
		plots.add(new ExtendedPlot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 5);
		manager.produceClimateVariables(2010);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}


	@Test
	public void test07ClimateGenerationHappyPathExtendedPlotsFiveRealizationsAfterLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot("01", 46, -75, 120));
		plots.add(new ExtendedPlot("02", 47, -76, 220));
		plots.add(new ExtendedPlot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 5);
		manager.produceClimateVariables(2040);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}

	@Test
	public void test08ClimateGenerationHappyPathExtendedPlotsFiveRealizationsOverlappingLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot("01", 46, -75, 120));
		plots.add(new ExtendedPlot("02", 47, -76, 220));
		plots.add(new ExtendedPlot("03", 52, -80, 300));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 5);
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
	}

	
	@Test
	public void test09ClimateGenerationWithNormals() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new PlotWithNormals("01", 46, -75, 120));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = PlotWithNormals.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 1);
		manager.produceClimateVariables(2040);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 1, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());

		REpiceaClimateVariableInformation normals1971_2000Info = null;
		for (REpiceaClimateVariableInformation info : infos) {
			if (info.model == BioSimModel.Normals1971_2000) {
				normals1971_2000Info = info;
				break;
			}
		}
		if (normals1971_2000Info == null) {
			throw new UnsupportedOperationException("Could not find the 1971-2000 normals BioSimModel instance in the infos local variable!");
		}
		
		double value = manager.getValue(2030, 2040, 0, ((PlotIdProvider) plots.get(0)).getId(), normals1971_2000Info);
		Assert.assertEquals("Testing 1971-2000 mean annual temperature", 3.9608219178082194, value, 1E-8);
	}

	@Test
	public void test10ClimateGenerationHappyPathExtendedPlots20RealizationsOverlappingLastDaily() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot("01", 46, -75, 120));
		plots.add(new ExtendedPlot("02", 47, -76, 220));
		plots.add(new ExtendedPlot("03", 52, -80, 300));
		int nbRealizations = 20;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				nbRealizations);
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
	}

	@Test
	public void test11ClimateGenerationHappyPathExtendedPlotsWithResolutionRounding() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot("01", 46.10, -75, 120));
		plots.add(new ExtendedPlot("02", 46.12, -75, 125));
		plots.add(new ExtendedPlot("03", 52, -80, 300));
		int nbRealizations = 1;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				0.1,
				0.1,
				20,
				nbRealizations);
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
		
		REpiceaClimateVariableInformation climateAnnualInfo = null;
		for (REpiceaClimateVariableInformation info : infos) {
			if (info.model == BioSimModel.Climatic_Annual) {
				climateAnnualInfo = info;
				break;
			}
		}
		if (climateAnnualInfo == null) {
			throw new UnsupportedOperationException("Could not find the Climatic_Annual BioSimModel instance in the infos local variable!");
		}
		
		double value0 = manager.getValue(2015, 2030, 0, ((PlotIdProvider) plots.get(0)).getId(), climateAnnualInfo);
		double value1 = manager.getValue(2015, 2030, 0, ((PlotIdProvider) plots.get(1)).getId(), climateAnnualInfo);
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				value1,
				1E-8);
	}


	@Test
	public void test12ClimateGenerationHappyPathExtendedPlots2WithMonthlyClimateCompilationAverage() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot2("01", 46.10, -75, 120));
		plots.add(new ExtendedPlot2("02", 46.12, -75, 125));
		plots.add(new ExtendedPlot2("03", 52, -80, 300));
		int nbRealizations = 1;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot2.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				0.1,
				0.1,
				20,
				nbRealizations);
		manager.produceClimateVariables(2025);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Monthly).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Monthly).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20 * 12, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Monthly).
				get(p).get(0).getNumberOfObservations());
		
		REpiceaClimateVariableInformation climateAnnualInfo = null;
		for (REpiceaClimateVariableInformation info : infos) {
			if (info.model == BioSimModel.Climatic_Monthly) {
				climateAnnualInfo = info;
				break;
			}
		}
		if (climateAnnualInfo == null) {
			throw new UnsupportedOperationException("Could not find the Climatic_Annual BioSimModel instance in the infos local variable!");
		}
		
		double value0 = manager.getValue(2015, 2025, 0, ((PlotIdProvider) plots.get(0)).getId(), climateAnnualInfo);
		double value1 = manager.getValue(2015, 2025, 0, ((PlotIdProvider) plots.get(1)).getId(), climateAnnualInfo);
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				value1,
				1E-8);
		
		Assert.assertEquals("Testing if the reference value for first plot", 
				18.759999999999998,
				value0,
				1E-8);
	}

	@Test
	public void test13ClimateGenerationHappyPathExtendedPlots2WithMonthlyClimateCompilationSummation() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot3("01", 46.10, -75, 120));
		plots.add(new ExtendedPlot3("02", 46.12, -75, 125));
		plots.add(new ExtendedPlot3("03", 52, -80, 300));
		int nbRealizations = 1;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot3.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				0.1,
				0.1,
				20,
				nbRealizations);
		manager.produceClimateVariables(2025);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualOrMonthlyValueMap.get(BioSimModel.Climatic_Monthly).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Monthly).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20 * 12, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climatic_Monthly).
				get(p).get(0).getNumberOfObservations());
		
		REpiceaClimateVariableInformation climateAnnualInfo = null;
		for (REpiceaClimateVariableInformation info : infos) {
			if (info.model == BioSimModel.Climatic_Monthly) {
				climateAnnualInfo = info;
				break;
			}
		}
		if (climateAnnualInfo == null) {
			throw new UnsupportedOperationException("Could not find the Climatic_Annual BioSimModel instance in the infos local variable!");
		}
		
		double value0 = manager.getValue(2015, 2025, 0, ((PlotIdProvider) plots.get(0)).getId(), climateAnnualInfo);
		double value1 = manager.getValue(2015, 2025, 0, ((PlotIdProvider) plots.get(1)).getId(), climateAnnualInfo);
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				value1,
				1E-8);
		
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				346.52,
				value0,
				10);

	}

	@SuppressWarnings("serial")
	static class ExtendedPlot4 extends Plot implements MeanAnnualSoilMoistureIndexPercentProvider {

		static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot4.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement, EvaluationDate.EndOfInterval);
		}
		
		ExtendedPlot4(String id, double latitude, double longitude, double altitude) {
			super(id, latitude, longitude, altitude);
		}
		
		
		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}


		@Override
		public double getMeanAnnualSMIPercent(REpiceaClimateVariableInformation info) {
			return 0;
		}

	}

	@Test
	public void test14ClimateGenerationHappyPathExtendedPlots4SoilMoistureIndex() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot4("01", 46.10, -75, 120));
		plots.add(new ExtendedPlot4("02", 46.12, -75, 125));
		plots.add(new ExtendedPlot4("03", 52, -80, 300));
		int nbRealizations = 1;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot4.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				0.1,
				0.1,
				20,
				nbRealizations);
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualOrMonthlyValueMap.get(BioSimModel.Soil_Moisture_Index_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Soil_Moisture_Index_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Soil_Moisture_Index_Annual).
				get(p).get(0).getNumberOfObservations());
		
		REpiceaClimateVariableInformation climateAnnualInfo = null;
		for (REpiceaClimateVariableInformation info : infos) {
			if (info.model == BioSimModel.Soil_Moisture_Index_Annual) {
				climateAnnualInfo = info;
				break;
			}
		}
		if (climateAnnualInfo == null) {
			throw new UnsupportedOperationException("Could not find the Soil_Moisture_Index_Annual BioSimModel instance in the infos local variable!");
		}
		
		double value0 = manager.getValue(2015, 2030, 0, ((PlotIdProvider) plots.get(0)).getId(), climateAnnualInfo);
		double value1 = manager.getValue(2015, 2030, 0, ((PlotIdProvider) plots.get(1)).getId(), climateAnnualInfo);
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				value1,
				1E-8);
		
		Assert.assertEquals("Testing the values is always approximately the same!", 
				95.053165,
				value0,
				5);

	}

	
	@SuppressWarnings("serial")
	static class ExtendedPlot5 extends Plot implements MeanAnnualClimateMoistureIndexCmProvider {

		static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot5.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement, EvaluationDate.EndOfInterval);
		}
		
		ExtendedPlot5(String id, double latitude, double longitude, double altitude) {
			super(id, latitude, longitude, altitude);
		}
		
		
		@Override
		public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap() {
			return CLIMATE_INFO;
		}


		@Override
		public double getMeanAnnualCMICm(REpiceaClimateVariableInformation info) {
			return 0;
		}

	}

	
	@Test
	public void test15ClimateGenerationHappyPathExtendedPlots5ClimateMoistureIndex() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot5("01", 46.10, -75, 120));
		plots.add(new ExtendedPlot5("02", 46.12, -75, 125));
		plots.add(new ExtendedPlot5("03", 52, -80, 300));
		int nbRealizations = 1;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot5.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				0.1,
				0.1,
				20,
				nbRealizations);
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualOrMonthlyValueMap.get(BioSimModel.Climate_Mosture_Index_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climate_Mosture_Index_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climate_Mosture_Index_Annual).
				get(p).get(0).getNumberOfObservations());
		
		REpiceaClimateVariableInformation climateAnnualInfo = null;
		for (REpiceaClimateVariableInformation info : infos) {
			if (info.model == BioSimModel.Climate_Mosture_Index_Annual) {
				climateAnnualInfo = info;
				break;
			}
		}
		if (climateAnnualInfo == null) {
			throw new UnsupportedOperationException("Could not find the Climate_Mosture_Index_Annual BioSimModel instance in the infos local variable!");
		}
		
		double value0 = manager.getValue(2015, 2030, 0, ((PlotIdProvider) plots.get(0)).getId(), climateAnnualInfo);
		double value1 = manager.getValue(2015, 2030, 0, ((PlotIdProvider) plots.get(1)).getId(), climateAnnualInfo);
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				value1,
				1E-8);
	}

	@Test
	public void test16RetrievingAnnualValues() throws Exception {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new ExtendedPlot5("01", 46.10, -75, 120));
		plots.add(new ExtendedPlot5("02", 46.12, -75, 125));
		plots.add(new ExtendedPlot5("03", 52, -80, 300));
		int nbRealizations = 1;
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = ExtendedPlot5.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, 
				infos, 
				plots, 
				0.1,
				0.1,
				20,
				nbRealizations);
		manager.produceClimateVariables(2030);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualOrMonthlyValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualOrMonthlyValueMap.get(BioSimModel.Climate_Mosture_Index_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climate_Mosture_Index_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualOrMonthlyValueMap.
				get(BioSimModel.Climate_Mosture_Index_Annual).
				get(p).get(0).getNumberOfObservations());
		
		List<Double>[] annualOutput = manager.getAnnualValues(BioSimClimateVariable.MeanAnnualCMI, 
				"01",
				0, 
				2020, 
				2030);

		List<Double> dates = annualOutput[0];
		Assert.assertEquals("Testing size of the list", 
				dates.size(),
				11);
		Assert.assertEquals("Testing size of the list", 
				((Double) dates.get(0)).intValue(),
				2020);
		Assert.assertEquals("Testing size of the list", 
				((Double) dates.get(dates.size() - 1)).intValue(),
				2030);

		Assert.assertEquals("Testing size of the list", 
				annualOutput[1].size(),
				11);
	}

	@Test
	public void test17CheckingCoordinates() {
		BioSimPlot p = new BioSimPlotImpl(45,70, Double.NaN);
		UniqueBioSimPlot uniquePlot = REpiceaClimateManager.roundCoordinates(p, 0.5, 0.5, 20);
		Assert.assertTrue("Making sure that the elevation set at NaN remains NaN in the UniqueBioSimPlot instance",
				Double.isNaN(uniquePlot.elevationM));
	}
	
	
	

}
