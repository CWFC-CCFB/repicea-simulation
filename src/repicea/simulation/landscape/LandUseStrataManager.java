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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import repicea.gui.REpiceaShowableUIWithParent;
import repicea.serial.Memorizable;
import repicea.serial.MemorizerPackage;
import repicea.simulation.covariateproviders.plotlevel.LandUseProvider.LandUse;
import repicea.stats.sampling.FinitePopulationEstimate;
import repicea.stats.sampling.PointEstimate;
import repicea.stats.sampling.PopulationMeanEstimate;
import repicea.stats.sampling.StratifiedPopulationEstimate;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * A class to handle the areas and sample size in the different 
 * land use categories.
 */
public final class LandUseStrataManager implements REpiceaShowableUIWithParent, Memorizable {

	@SuppressWarnings("serial")
	public static class LandUseStratumException extends RuntimeException {
		
		LandUseStratumException(String message) {
			super(message);
		}
		
	}
	
	enum MessageID implements TextableEnum {
		DifferentPlotAreasError("It seems the plots have different areas!", "Les placettes semblent avoir des surfaces diff\u00E9rentes!"),
		UnableToCalculateInclusionProbabilityForAtLeastOneStratum("There are several strata and the inclusion probability cannot be calculated for at least one of them!",
				"Il y a plusieurs strates et la probabilit\u00E9 d'inclusion ne peut \u00EAtre calcul\u00E9e pour au moins une d'entre elles!"),
		UnableToCalculateInclusionProbabilityAtAll("The inclusion probability cannot be calculated!", "La probabilit\u00E9 d'inclusion ne peut \u00EAtre calcul\u00E9e"),
		AreaSetForStratumWithoutPlotError("There are no plots for this land use: ", "Cette strate n'a pas de placettes!"),
		StratumAreaMustBeEqualOrGreaterThanZero("The stratumAreaHa argument must be greater or equal to 0!", "Le param\u00E8tre stratumAreaHa doit \u00EAtre \u00E9gal ou plus grand que z\u00E9ro!"),
		StratumAreaCantBeGreaterThanZeroIfNoPlots("The stratumAreaHa cannot be greater than 0 if there are not plots!", "Le param\u00E8tre stratumAreaHa ne peut \u00EAtre plus grand que z\u00E9ro s'il n'y a pas de placettes!"),
		SampleSizeOfThisLandUse("The sample size in this land use ", "La taille d'\u00E9chantillon de cette affectation "),
		IsSmallerThanTwo(" is smaller than 2!", " est plus petite que 2!"),
		AlreadyThisPlotId("It seems there are duplicate plot ids!", "Certaines placettes ont le m\u00EAme identifiant!");

		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
	}
	
	public static enum EstimatorType {
		/**
		 * Mean for infinite population.
		 */
		InfinitePopulation,
		/**
		 * Mean and total for finite population.
		 */
		FinitePopulation,
		/**
		 * Mean and total for stratified population.
		 */
		StratifiedPopulation}

	final Map<LandUse, LandUseStratum> landUseStrata;
	EstimatorType estimatorType = null;
	transient LandUseStrataManagerDialog guiInterface;
	boolean isCancelled;
	final Map<String, LandUseStratum> plotIdToLandUseStrataMap;

	/**
	 * Constructor.
	 * @param plots a Collection of LandUseStrataManagerCompatiblePlot instances
	 */
	public LandUseStrataManager(Collection<LandUseStrataManagerCompatiblePlot> plots) {
		Map<LandUse, Integer> landUseFreqMap = new HashMap<LandUse, Integer>();
		Map<LandUse, Double> landUseIndividualPlotAreaMap = new HashMap<LandUse, Double>();
		Map<String, LandUse> plotIds = new HashMap<String, LandUse>();
		for (LandUseStrataManagerCompatiblePlot plot : plots) {
			LandUse lu = plot.getLandUse();
			if (landUseFreqMap.containsKey(lu)) {
				landUseFreqMap.put(lu, landUseFreqMap.get(lu) + 1);
				if (Math.abs(plot.getAreaHa() - landUseIndividualPlotAreaMap.get(lu)) > 1E-8) {
					throw new LandUseStratumException(MessageID.DifferentPlotAreasError.toString());
				}
			} else {
				landUseFreqMap.put(lu, 1);
				landUseIndividualPlotAreaMap.put(lu, plot.getAreaHa());
			}
			String plotId = plot.getId();
			if (!plotIds.containsKey(plotId)) {
				plotIds.put(plotId, plot.getLandUse());
			} else {
				throw new LandUseStratumException(MessageID.AlreadyThisPlotId.toString());
			}
		}

		landUseStrata = new LinkedHashMap<LandUse, LandUseStratum>();
		for (LandUse lu : landUseFreqMap.keySet()) {
			int nbPlots = landUseFreqMap.containsKey(lu) ? landUseFreqMap.get(lu) : 0;
			double individualPlotAreaHa = landUseIndividualPlotAreaMap.containsKey(lu) ? landUseIndividualPlotAreaMap.get(lu) : 0d;
			landUseStrata.put(lu, new LandUseStratum(this, lu, nbPlots, individualPlotAreaHa));
		}
		
		plotIdToLandUseStrataMap = new HashMap<String, LandUseStratum>();
		for (String plotId : plotIds.keySet()) {
			LandUse lu = plotIds.get(plotId);
			plotIdToLandUseStrataMap.put(plotId, landUseStrata.get(lu));
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
					if (lus.getEstimatorTypeCompatilibity() == EstimatorType.InfinitePopulation) {
						throw new LandUseStratumException(MessageID.UnableToCalculateInclusionProbabilityForAtLeastOneStratum.toString());
					}
				}
				estimatorType = EstimatorType.StratifiedPopulation;
			}
		}
		return estimatorType;
	}

	/**
	 * Provide a point estimator that matches the context.<p>
	 * If there is a single stratum, then a PopulationMeanEstimate instance is returned.
	 * In cases of multiple strata, a StratifiedPopulationTotalEstimate is returned.
	 * @return a PointEstimate instance
	 */
	public PointEstimate getPointEstimate() {
		EstimatorType type = getEstimatorType();
		LandUseStratum s;
		switch(type) {
		case InfinitePopulation:
			return new PopulationMeanEstimate();
		case FinitePopulation:
			s = landUseStrata.values().iterator().next();
			return new FinitePopulationEstimate(s.stratumAreaHa / s.individualPlotAreaHa);
		case StratifiedPopulation:
			List<String> stratumNames = new ArrayList<String>();
			List<Double> strataPopulationSizes = new ArrayList<Double>();
			for (LandUse stratum : landUseStrata.keySet()) {
				stratumNames.add(stratum.name());
				s = landUseStrata.get(stratum);
				strataPopulationSizes.add(s.stratumAreaHa / s.individualPlotAreaHa);
			}
			return new StratifiedPopulationEstimate(stratumNames, strataPopulationSizes);
		default:
			throw new UnsupportedOperationException("This estimator type has not been implemeted yet: " + type.name());
		}
	}
	
	
	/**
	 * Provide a point estimator for a subdomain.<p>
	 * The subdomain can include one or more strata.
	 * If there is a single stratum, then a PopulationMeanEstimate instance is returned.
	 * In cases of multiple strata, a StratifiedPopulationTotalEstimate is returned.
	 * @param strata an Array of LandUse enums
	 * @return a PointEstimate instance
	 */
	public PointEstimate getPointEstimateForSubDomains(LandUse[] strata) {
		if (strata == null || strata.length == 0) {
			throw new InvalidParameterException("The strata parameter must have at least one string!");
		}
		List<LandUse> landUses = new ArrayList<LandUse>();
		for (LandUse lu : strata) {
			if (!landUses.contains(lu)) {
				if (!landUseStrata.containsKey(lu)) {
					throw new InvalidParameterException("The strata map does not contain a stratum for this land use: " + lu.name());
				}
				landUses.add(lu);
			}
		}
		getEstimatorType(); // just to make sure the manager is valid
		if (landUses.size() == 1) {
			LandUseStratum s = landUseStrata.get(landUses.get(0));
			return s.stratumAreaHa == 0d ?
					new PopulationMeanEstimate() :
						new FinitePopulationEstimate(s.stratumAreaHa / s.individualPlotAreaHa);
		} else {
			List<String> stratumNames = new ArrayList<String>();
			List<Double> strataPopulationSizes = new ArrayList<Double>();
			for (LandUse stratum : landUses) {
				stratumNames.add(stratum.name());
				LandUseStratum s = landUseStrata.get(stratum);
				strataPopulationSizes.add(s.stratumAreaHa / s.individualPlotAreaHa);
			}
			return new StratifiedPopulationEstimate(stratumNames, strataPopulationSizes);
		} 
	}

	/**
	 * Provide the inclusion probability for this land use.
	 * @param lu a LandUse enum
	 * @return the inclusion probability
	 */
	public double getInclusionProbabilityForThisLandUse(LandUse lu) {
		if (getEstimatorType() != EstimatorType.InfinitePopulation) {
			return landUseStrata.get(lu).inclusionProbability;
		} else {
			throw new LandUseStratumException(MessageID.UnableToCalculateInclusionProbabilityAtAll.toString());
		}
	}

	/**
	 * Provide the inclusion probability for a particular plot.
	 * @param plotId the unique id of a plot
	 * @return the inclusion probability
	 */
	public double getInclusionProbabilityForThisPlot(String plotId) {
		if (getEstimatorType() != EstimatorType.InfinitePopulation) {
			return plotIdToLandUseStrataMap.get(plotId).inclusionProbability;
		} else {
			throw new LandUseStratumException(MessageID.UnableToCalculateInclusionProbabilityAtAll.toString());
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
			throw new LandUseStratumException(MessageID.AreaSetForStratumWithoutPlotError.toString() + lu.name());
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
		mp.add((Serializable) plotIdToLandUseStrataMap);
		return mp;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void unpackMemorizerPackage(MemorizerPackage wasMemorized) {
		landUseStrata.clear();
		landUseStrata.putAll((Map) wasMemorized.get(0));
		plotIdToLandUseStrataMap.clear();
		plotIdToLandUseStrataMap.putAll((Map) wasMemorized.get(1));
	}


}
