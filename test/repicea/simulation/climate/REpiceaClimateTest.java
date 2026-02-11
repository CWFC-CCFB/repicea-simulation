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

import org.junit.Assert;
import org.junit.Test;

import repicea.simulation.climate.REpiceaClimateManager.AllowedResolutions;
import repicea.simulation.climate.REpiceaClimateManager.ClimateVariableTemporalResolution;
import repicea.simulation.climate.REpiceaClimateVariableMap.ClimateVariable;
import repicea.simulation.covariateproviders.plotlevel.climate.MeanAnnualTemperatureCelsiusProvider;

public class REpiceaClimateTest {

	
	class MeanTemp implements MeanAnnualTemperatureCelsiusProvider {

		@AllowedResolutions(values = {ClimateVariableTemporalResolution.Annual, ClimateVariableTemporalResolution.IntervalAveraged})
		@Override
		public double getMeanAnnualTemperatureCelsius(ClimateVariableTemporalResolution resolution) {
			REpiceaClimateManager.getInstance().checkClimateRelatedMethodResolution(this, "getMeanAnnualTemperatureCelsius", resolution);
			return 0;
		}
		
	}
	
	@Test
	public void test01SimpleClimateChangeTrend() {
		REpiceaClimateChangeTrend trend = new REpiceaClimateChangeTrend();
		REpiceaClimateVariableChangeMap map = new REpiceaClimateVariableChangeMap();
		map.put(ClimateVariable.MeanGrowingSeasonTempC, 0.016);
		trend.addSegment(1950, 2015, map);
		map = new REpiceaClimateVariableChangeMap();
		map.put(ClimateVariable.MeanGrowingSeasonTempC, 0.030);
		trend.addSegment(2015, 2100, map);
		double change = trend.getChangeFromTo(1975, 2000, ClimateVariable.MeanGrowingSeasonTempC);
		Assert.assertEquals("Testing simple change from 1975 to 2000", 25*0.016, change, 1E-8);

		change = trend.getChangeFromTo(2000, 2020, ClimateVariable.MeanGrowingSeasonTempC);
		Assert.assertEquals("Testing simple change from 2000 to 2020", 15*0.016 + 5 * 0.03, change, 1E-8);

		double averageChange = trend.getAverageChangeOverThisPeriod(1975, 1960, 1990, ClimateVariable.MeanGrowingSeasonTempC);
		Assert.assertEquals("Average change from 1960 to 1990 with respect to 1975",
				0d, 
				averageChange, 
				1E-8);

		averageChange = trend.getAverageChangeOverThisPeriod(1975, 1985, 2015, ClimateVariable.MeanGrowingSeasonTempC);
		Assert.assertEquals("Average change from 1985 to 2015 with respect to 1975",
				25 * 0.016, 
				averageChange, 
				1E-8);

		averageChange = trend.getAverageChangeOverThisPeriod(1975, 2000, 2030, ClimateVariable.MeanGrowingSeasonTempC);
		double expected = (15 * (25 * 0.016 + 40 * 0.016) * .5 + 15 * (2 * 40 * 0.016 + 15 * 0.03) * .5) / 30;
		Assert.assertEquals("Average change from 2000 to 2030 with respect to 1975",
				expected, 
				averageChange, 
				1E-8);
	}
	
	@Test
	public void test02ClimateVariableResolutionsAnnotation() {
		MeanTemp temp = new MeanTemp();
		try {
			temp.getMeanAnnualTemperatureCelsius(ClimateVariableTemporalResolution.Normals30Year);
			Assert.fail("Should have failed");
		} catch (UnsupportedOperationException e) {
			// Should end up here
		}
	}
	
	@Test
	public void test03ClimateVariableResolutionsAnnotation() {
		MeanTemp temp = new MeanTemp();
		try {
			temp.getMeanAnnualTemperatureCelsius(ClimateVariableTemporalResolution.Normals30Year);
			Assert.fail("Should have failed");
		} catch (UnsupportedOperationException e) {
			// Should end up here
		}
	}


	
	
}
