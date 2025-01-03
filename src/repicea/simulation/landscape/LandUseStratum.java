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

import repicea.simulation.covariateproviders.plotlevel.LandUseProvider.LandUse;
import repicea.simulation.landscape.LandUseStrataManager.EstimatorType;

/**
 * The LandUseStratum class handles the inclusion probability of a particular land use.
 * @author Mathieu Fortin - January 2025
 */
class LandUseStratum {

	enum WhatFor {HTEstimator, MeanEstimator}

	final LandUseStrataManager manager;
	final LandUse landUse;
	final int nbPlots;
	final double individualPlotAreaHa;
	private double stratumAreaHa;
	double inclusionProbability;

	LandUseStratum(LandUseStrataManager manager, LandUse landUse, int nbPlots, double individualPlotAreaHa) {
		if (landUse == null) {
			throw new InvalidParameterException("The landUse argument must be non null!");
		}
		if (nbPlots < 0) {
			throw new InvalidParameterException("The nbPlots argument must be greater than or equal to 0!");
		}
		if (individualPlotAreaHa < 0) {
			throw new InvalidParameterException("The individualPlotAreaHa must be greater than or equal to 0 !");
		}
		this.manager = manager;
		this.landUse = landUse;
		this.nbPlots = nbPlots;
		this.individualPlotAreaHa = individualPlotAreaHa;
	}

	@Override
	public String toString() {
		return "Stratum " + landUse.name() + "; Nb plots = " + nbPlots + "; Area (ha) = " + stratumAreaHa;
	}

	void setStratumAreaHa(double stratumAreaHa) {
		if (stratumAreaHa < 0d) {
			throw new InvalidParameterException("The stratumAreaHa argument must be greater or equal to 0!");
		} else if (stratumAreaHa > 0d && nbPlots == 0) {
			throw new InvalidParameterException("The stratumAreaHa cannot be greater than 0 if there are not plots!");
		}
		this.stratumAreaHa = stratumAreaHa;
		manager.estimatorType = null; // we reset this member so that the manager will have to be validated again
	}
	
	EstimatorType getEstimatorTypeCompatilibity() {
		if (nbPlots >= 2) {
			if (stratumAreaHa > 0) {
				inclusionProbability = nbPlots * individualPlotAreaHa / stratumAreaHa;
				return EstimatorType.HorvitzThompson;
			} else {
				return EstimatorType.Mean;
			}
		} else {
			throw new InvalidParameterException("The sample size in this land use " + this.landUse.name() + " is smaller than 2!");
		}
	}
}


