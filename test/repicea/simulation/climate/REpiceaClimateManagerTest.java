package repicea.simulation.climate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimClientException;
import biosimclient.BioSimPlot;
import biosimclient.BioSimServerException;
import repicea.simulation.ClimateSensitivePredictor;
import repicea.simulation.climate.REpiceaClimateGenerator.RepresentativeConcentrationPathway;
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimModel;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;
import repicea.simulation.covariateproviders.plotlevel.PlotIdProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.AnnualGrowingDegreeDaysCelsiusProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualTemperatureCelsiusProvider;

public class REpiceaClimateManagerTest {

	@SuppressWarnings("serial")
	static class Plot implements PlotIdProvider, 
								BioSimPlot, 
								ClimateSensitivePredictor,
								MeanAnnualTemperatureCelsiusProvider {

		private static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, Plot.class, Resolution.IntervalAveraged);
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
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement);
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
	static class PlotWithNormals extends ExtendedPlot  {

		private static final Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> CLIMATE_INFO = new HashMap<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>>();
		static {
			REpiceaClimateVariableInformation.fillClimateInfoMap(CLIMATE_INFO, ExtendedPlot.class, Resolution.IntervalAveragedStarting20YrsBeforeFinalMeasurement);
			CLIMATE_INFO.get(MeanAnnualTemperatureCelsiusProvider.class).put(Resolution.Normals30Year,  
					new REpiceaClimateVariableInformation(Resolution.Normals30Year, BioSimModel.Normals1971_2000, "T"));
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
	public void test01ClimateGenerationHappyPathOneRealizationOverOneInterval() throws BioSimClientException, BioSimServerException, InterruptedException {
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
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 1, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		double value = manager.getValue(2000, 2010, 0, ((PlotIdProvider) plots.get(0)).getId(), infos.get(0));
		Assert.assertEquals("Testing interval averaged value", 4.8909090909, value, 1E-8);
	}

	@Test
	public void test02ClimateGenerationHappyPathFiveRealizationsBeforeLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}

	@Test
	public void test03ClimateGenerationHappyPathFiveRealizationsAfterLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}

	
	@Test
	public void test04ClimateGenerationHappyPathFiveRealizationsOverlappingLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 1, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 10, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
	}

	
	
	@Test
	public void test05ClimateGenerationHappyPathExtendedPlotsOneRealizationBeforeLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 1, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
		Assert.assertEquals("Testing last date year in dataset", 2010, manager.lastDateYrInDataset);
		manager.produceClimateVariables(2020); // should work because the resolution is not annual nor interval averaged
		Assert.assertEquals("Testing nb observations in each dataset", 30, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
		Assert.assertEquals("Testing last date year in dataset", 2020, manager.lastDateYrInDataset);
	}
	
	
	@Test
	public void test06ClimateGenerationHappyPathExtendedPlotsFiveRealizationsBeforeLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}


	@Test
	public void test07ClimateGenerationHappyPathExtendedPlotsFiveRealizationsAfterLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
	}

	@Test
	public void test08ClimateGenerationHappyPathExtendedPlotsFiveRealizationsOverlappingLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 5, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
	}

	
	@Test
	public void test09ClimateGenerationWithNormals() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new PlotWithNormals("01", 46, -75, 120));
		Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> oMap = PlotWithNormals.CLIMATE_INFO;
		List<REpiceaClimateVariableInformation> infos = new ArrayList<REpiceaClimateVariableInformation>();
		for (Map<Resolution, REpiceaClimateVariableInformation> innerMap : oMap.values()) {
			infos.addAll(innerMap.values());
		}
		REpiceaClimateManager manager = new REpiceaClimateManager(RepresentativeConcentrationPathway.RCP4_5, infos, plots, 1);
		manager.produceClimateVariables(2040);
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", 1, manager.annualValueMap.
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
	public void test10ClimateGenerationHappyPathExtendedPlots20RealizationsOverlappingLastDaily() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", plots.size(), manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).get(0).getNumberOfObservations());
	}

	
	@Test
	public void test11ClimateGenerationHappyPathExtendedPlotsWithResolutionRounding() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualValueMap.get(BioSimModel.Climatic_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualValueMap.
				get(BioSimModel.Climatic_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualValueMap.
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
		
		double value0 = manager.getValue(2030, 2040, 0, ((PlotIdProvider) plots.get(0)).getId(), climateAnnualInfo);
		double value1 = manager.getValue(2030, 2040, 0, ((PlotIdProvider) plots.get(1)).getId(), climateAnnualInfo);
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				value1,
				1E-8);
	}

	
	
	
}
