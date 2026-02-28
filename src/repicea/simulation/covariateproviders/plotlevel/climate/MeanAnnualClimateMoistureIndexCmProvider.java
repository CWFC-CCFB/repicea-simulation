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
package repicea.simulation.covariateproviders.plotlevel.climate;

import repicea.simulation.ClimateSensitivePredictor;
import repicea.simulation.climate.REpiceaClimateVariableInformation;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;
import repicea.simulation.climate.REpiceaClimateVariableProvider;

/**
 * This interface ensures the plot instance can provide its mean 
 * annual climate moisture index. 
 * @author Mathieu Fortin - February 2026
 */
public interface MeanAnnualClimateMoistureIndexCmProvider extends REpiceaClimateVariableProvider {

	/**
	 * Provide the mean annual climate moisture index (CMI).
	 * @param info an REpiceaClimateVariableInformation instance defining the climate variable 
	 * @return the CMI (cm)
	 */
	public double getMeanAnnualCMICm(REpiceaClimateVariableInformation info);
	
	/**
	 * Provide the mean annual climate moisture index (CMI).
	 * @param predictor a ClimateSensitivePredictor instance
	 * @param resolution a Resolution enum
	 * @return the CMI (cm)
	 */
	public default double getMeanAnnualCMICm(ClimateSensitivePredictor predictor, Resolution resolution) {
		return getMeanAnnualCMICm(REpiceaClimateVariableProvider.getInformationFromPredictor(predictor, 
				MeanAnnualClimateMoistureIndexCmProvider.class,
				resolution));
	}

}
