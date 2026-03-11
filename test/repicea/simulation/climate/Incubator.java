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
import org.junit.Ignore;
import org.junit.Test;

import biosimclient.BioSimClientException;
import biosimclient.BioSimPlot;
import biosimclient.BioSimServerException;
import repicea.simulation.climate.REpiceaClimateGenerator.RepresentativeConcentrationPathway;
import repicea.simulation.climate.REpiceaClimateManagerTest.Plot;
import repicea.simulation.climate.REpiceaClimateVariableInformation.BioSimModel;
import repicea.simulation.climate.REpiceaClimateVariableInformation.EvaluationDate;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;
import repicea.simulation.covariateproviders.plotlevel.PlotIdProvider;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualSoilMoistureIndexPercentProvider;

public class Incubator {

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

	@Ignore
	@Test
	public void test14ClimateGenerationHappyPathExtendedPlots4SoilMoistureIndex() throws BioSimClientException, BioSimServerException {
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
		Assert.assertEquals("Testing annualValueMap size", 2, manager.annualValueMap.size());
		Assert.assertEquals("Testing nb plots in annualValueMap", 2, manager.annualValueMap.get(BioSimModel.Soil_Moisture_Index_Annual).size());
		BioSimPlot p = manager.uniquePlotList.get(0);
		Assert.assertEquals("Testing nb realization in annualValueMap", nbRealizations, manager.annualValueMap.
				get(BioSimModel.Soil_Moisture_Index_Annual).
				get(p).size());
		Assert.assertEquals("Testing nb observations in each dataset", 20, manager.annualValueMap.
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
		
		Assert.assertEquals("Testing if the values of two plots within the same pixel are the same", 
				value0,
				337.88500000000005,
				5);

	}
	
	
}
