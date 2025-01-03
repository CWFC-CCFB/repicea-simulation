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

import java.awt.Container;
import java.awt.Window;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import repicea.gui.REpiceaShowableUIWithParent;
import repicea.serial.Memorizable;
import repicea.serial.MemorizerPackage;
import repicea.simulation.covariateproviders.plotlevel.LandUseProvider.LandUse;

/**
 * A class to handle the areas and sample size in the different 
 * land use categories.
 */
public final class LandUseStrataManager implements REpiceaShowableUIWithParent, Memorizable {

	public static enum EstimatorType {HorvitzThompson, Mean}

	final Map<LandUse, LandUseStratum> landUseStrata;
	EstimatorType estimatorType = null;
	transient LandUseStrataManagerDialog guiInterface;
	boolean isCancelled;

	/**
	 * Constructor.
	 * @param plots a Collection of LandUseStrataManagerCompatiblePlot instances
	 */
	public LandUseStrataManager(Collection<LandUseStrataManagerCompatiblePlot> plots) {
		Map<LandUse, Integer> landUseFreqMap = new HashMap<LandUse, Integer>();
		Map<LandUse, Double> landUseIndividualPlotAreaMap = new HashMap<LandUse, Double>();
		for (LandUseStrataManagerCompatiblePlot plot : plots) {
			LandUse lu = plot.getLandUse();
			if (landUseFreqMap.containsKey(lu)) {
				landUseFreqMap.put(lu, landUseFreqMap.get(lu) + 1);
				if (Math.abs(plot.getAreaHa() - landUseIndividualPlotAreaMap.get(lu)) > 1E-8) {
					throw new InvalidParameterException("It seems the plots have different areas!");
				}
			} else {
				landUseFreqMap.put(lu, 1);
				landUseIndividualPlotAreaMap.put(lu, plot.getAreaHa());
			}
		}

		landUseStrata = new LinkedHashMap<LandUse, LandUseStratum>();
		for (LandUse lu : landUseFreqMap.keySet()) {
			int nbPlots = landUseFreqMap.containsKey(lu) ? landUseFreqMap.get(lu) : 0;
			double individualPlotAreaHa = landUseIndividualPlotAreaMap.containsKey(lu) ? landUseIndividualPlotAreaMap.get(lu) : 0d;
			landUseStrata.put(lu, new LandUseStratum(this, lu, nbPlots, individualPlotAreaHa));
		}
	}

	
	/**
	 * Provide the number of plots for a particular land use.
	 * @param lu a LandUse enum
	 * @return an integer
	 */
	public int getNbPlotsForThisLandUse(LandUse lu) {
		return landUseStrata.containsKey(lu) ? landUseStrata.get(lu).nbPlots : 0;
	}

	/**
	 * Reset the status of the cancelled member to false.
	 */
	public void resetCancelled() {
		this.isCancelled = false;
	}
	
	/**
	 * Check if the instance has been cancelled through the dialog.
	 * @return a boolean
	 */
	public boolean isCancelled() {return isCancelled;}
	
	/**
	 * Inform on the possibility of using Horvitz-Thompson (HT) estimators.<p>
	 * HT estimators can be used only if the stratum areas are provided in 
	 * each stratum with plots. If there is only one stratum with plots, then the estimator of 
	 * the mean can be used if no area was provided for this stratum. 
	 * @return an EstimatorType enum
	 * @throws UnsupportedOperationException if there are more than one stratum and 
	 * the inclusion probability cannot be calculated for at least one of them.
	 */
	public EstimatorType getEstimatorType() throws UnsupportedOperationException {
		if (estimatorType == null) {
			if (landUseStrata.size() == 1) {
				estimatorType = landUseStrata.values().iterator().next().getEstimatorTypeCompatilibity();
			} else {
				for (LandUse lu : landUseStrata.keySet()) {
					LandUseStratum lus = landUseStrata.get(lu);
					if (lus.getEstimatorTypeCompatilibity() != EstimatorType.HorvitzThompson) {
						throw new UnsupportedOperationException("There are several strata and the inclusion probability cannot be calculated for at least one of them!");
					}
				}
				estimatorType = EstimatorType.HorvitzThompson;
			}
		}
		return estimatorType;
	}
	
	/**
	 * Provide the inclusion probability for this land use.
	 * @param lu a LandUse enum
	 * @return the inclusion probability
	 */
	public double getInclusionProbabilityForThisLandUse(LandUse lu) {
		if (getEstimatorType() == EstimatorType.HorvitzThompson) {
			return landUseStrata.get(lu).inclusionProbability;
		} else {
			throw new UnsupportedOperationException("The inclusion probability cannot be calculated!");
		}
	}

	/**
	 * Set the area for a particular land use.
	 * @param lu a LandUse enum
	 * @param stratumAreaHa the area (ha) for this land use.
	 */
	public void setStratumAreaHaForThisLandUse(LandUse lu, double stratumAreaHa) {
		if (landUseStrata.containsKey(lu)) {
			landUseStrata.get(lu).setStratumAreaHa(stratumAreaHa);
		} else {
			throw new InvalidParameterException("There are no plots for this land use: " + lu.name());
		}
	}

	@Override
	public LandUseStrataManagerDialog getUI(Container parent) {
		if (guiInterface == null) {
			guiInterface = new LandUseStrataManagerDialog(this, (Window) parent);
		}
		return guiInterface;
	}

	@Override
	public boolean isVisible() {
		return getUI(null).isVisible();
	}

	@Override
	public void showUI(Window parent) {
		getUI(parent).setVisible(true);
	}

	@Override
	public MemorizerPackage getMemorizerPackage() {
		MemorizerPackage mp = new MemorizerPackage();
		mp.add((Serializable) landUseStrata);
		return mp;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void unpackMemorizerPackage(MemorizerPackage wasMemorized) {
		landUseStrata.clear();
		landUseStrata.putAll((Map) wasMemorized.get(0));
	}


}
