/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2025 His Majesty the King in right of Canada
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
package repicea.simulation.landscape;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import repicea.simulation.covariateproviders.plotlevel.LandUseProvider.LandUse;
import repicea.simulation.landscape.LandUseStrataManager.LandUseStratumException;
import repicea.stats.sampling.StratifiedPopulationEstimate;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LandUseStratumManagerTest {

	private static class LandUseStratumManagerCompatiblePlotImpl implements LandUseStrataManagerCompatiblePlot {

		final double areaHa;
		final LandUse landUse;
		final String id;

		private LandUseStratumManagerCompatiblePlotImpl(String id, double areaHa, LandUse landUse) {
			this.areaHa = areaHa;
			this.landUse = landUse;
			this.id = id;
		}

		@Override
		public double getAreaHa() {return areaHa;}

		@Override
		public LandUse getLandUse() {return landUse;}

		@Override
		public String getId() {return id;}
	}
	
	@Test
	public void test01HappyPathWithSingleStratum() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.setStratumAreaHaForThisLandUse(LandUse.WoodProduction, 100d);
		lusm.validateDesign();
	}

	@Test
	public void test02FailingNotEnoughPlotsWithSingleStratum() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		try {
			lusm.validateDesign();	
			Assert.fail("Should have thrown an LandUseStratumException!");
		} catch(LandUseStratumException e) {
			System.err.println(e.getMessage());
			System.out.println("Got an exception! Relax that was expected!");
		}
	}

	@Test
	public void test03HappyPathWithSingleStratumAndArea() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.setStratumAreaHaForThisLandUse(LandUse.WoodProduction, 100);
		Assert.assertEquals("Checking inclusion probability", 
				2 * 0.04 / 100, 
				lusm.getInclusionProbabilityForThisLandUse(LandUse.WoodProduction), 
				1E-8);
	}

	@Test
	public void test04FailingWithMultipleStrataOneOfThemHasTooFewPlots() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.04, LandUse.SensitiveWoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.setStratumAreaHaForThisLandUse(LandUse.WoodProduction, 100);
		lusm.setStratumAreaHaForThisLandUse(LandUse.SensitiveWoodProduction, 200);
		try {
			lusm.validateDesign();	
			Assert.fail("Should have thrown an InvalidParameterException!");
		} catch(LandUseStratumException e) {
			System.err.println(e.getMessage());
			System.out.println("Got an exception! Relax that was expected!");
		}
	}
		
	@Test
	public void test05FailingWithHeterogeneousMultipleStrata() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.04, LandUse.SensitiveWoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.setStratumAreaHaForThisLandUse(LandUse.WoodProduction, 100);
		try {
			lusm.validateDesign();	
			Assert.fail("Should have thrown an InvalidParameterException!");
		} catch(LandUseStratumException e) {
			System.err.println(e.getMessage());
			System.out.println("Got an exception! Relax that was expected!");
		}
	}

	@Test
	public void test06FailingWithHeterogeneousMultipleStrata2() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.04, LandUse.SensitiveWoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		try {
			lusm.validateDesign();	
			Assert.fail("Should have thrown an InvalidParameterException!");
		} catch(LandUseStratumException e) {
			System.err.println(e.getMessage());
			System.out.println("Got an exception! Relax that was expected!");
		}
	}

	@Test
	public void test07FailingWhileProvidingAreaForStratumWithNoPlots() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.04, LandUse.SensitiveWoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		try {
			lusm.setStratumAreaHaForThisLandUse(LandUse.Conservation, 10d);
			Assert.fail("Should have thrown an InvalidParameterException!");
		} catch(InvalidParameterException e) {
			System.err.println(e.getMessage());
			System.out.println("Got an exception! Relax that was expected!");
		}
	}

	@Test
	public void test08HappyPathWithMultipleStrata() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.08, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 5, 0.08, LandUse.SensitiveWoodProduction));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.setStratumAreaHaForThisLandUse(LandUse.WoodProduction, 100d);
		lusm.setStratumAreaHaForThisLandUse(LandUse.SensitiveWoodProduction, 200d);
		Assert.assertEquals("Checking inclusion probability", 
				3 * 0.04 / 100, 
				lusm.getInclusionProbabilityForThisLandUse(LandUse.WoodProduction), 
				1E-8);
		Assert.assertEquals("Checking inclusion probability for plot 1", 
				3 * 0.04 / 100, 
				lusm.getInclusionProbabilityForThisPlot("" + 1),
				1E-8);
		Assert.assertEquals("Checking inclusion probability", 
				2 * 0.08 / 200, 
				lusm.getInclusionProbabilityForThisLandUse(LandUse.SensitiveWoodProduction), 
				1E-8);
		Assert.assertEquals("Checking inclusion probability for plot 4", 
				2 * 0.08 / 200, 
				lusm.getInclusionProbabilityForThisPlot("" + 4), 
				1E-8);
	}

	
	@Test
	public void test09HappyPathSubdomainEstimator() {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.08, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 5, 0.08, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 6, 0.08, LandUse.Conservation));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 7, 0.08, LandUse.Conservation));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.setStratumAreaHaForThisLandUse(LandUse.WoodProduction, 100d);
		lusm.setStratumAreaHaForThisLandUse(LandUse.SensitiveWoodProduction, 250d);
		lusm.setStratumAreaHaForThisLandUse(LandUse.Conservation, 300d);
		StratifiedPopulationEstimate pe = lusm.getPointEstimateForSubDomains(Arrays.asList(new LandUse[] {LandUse.WoodProduction, LandUse.SensitiveWoodProduction}));
		Assert.assertTrue("Testing if the PointEstimate instance is a StratifiedPopulationTotalEstimate", pe instanceof StratifiedPopulationEstimate);
		Assert.assertEquals("Testing population size of subdomain estimator", 
				100d/0.04 + 250d/0.08,
				pe.getPopulationSize(), 
				1E-8);

		pe = lusm.getPointEstimateForSubDomains(Arrays.asList(new LandUse[] {LandUse.WoodProduction}));
		Assert.assertEquals("Testing population size of subdomain estimator", 
				100d/0.04,
				pe.getPopulationSize(), 
				1E-8);

		try {
			lusm.getPointEstimateForSubDomains(Arrays.asList(new LandUse[] {LandUse.Unproductive}));
			Assert.fail("Should have thrown an InvalidParameterException");
		} catch (InvalidParameterException e) {
			System.err.println(e.getMessage());
			System.out.println("This error was expected!");
		}
	}

	
	public static void main(String[] arg) {
		List<LandUseStrataManagerCompatiblePlot> plots = new ArrayList<LandUseStrataManagerCompatiblePlot>();
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 1, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 2, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 3, 0.04, LandUse.WoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 4, 0.08, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 5, 0.08, LandUse.SensitiveWoodProduction));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 6, 0.08, LandUse.Inaccessible));
		plots.add(new LandUseStratumManagerCompatiblePlotImpl("" + 7, 0.08, LandUse.Inaccessible));
		LandUseStrataManager lusm = new LandUseStrataManager(plots);
		lusm.showUI(null);
	//	int u = 0;
		lusm.showUI(null);
		System.exit(0);
	}
}
