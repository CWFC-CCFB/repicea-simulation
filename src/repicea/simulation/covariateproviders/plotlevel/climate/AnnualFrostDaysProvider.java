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
 * This interface ensures the plot instance can provide the number of
 * frost days annually. 
 * @author Mathieu Fortin - February 2026
 */
public interface AnnualFrostDaysProvider extends REpiceaClimateVariableProvider {

	/**
	 * Provide the annual number of frost days.
	 * @param info an REpiceaClimateVariableInformation instance defining the climate variable 
	 * @return the number of days
	 */
	public double getAnnualNbFrostDays(REpiceaClimateVariableInformation info);
	
	
	/**
	 * Default implementation to retrieve the variable from the predictor itself.
	 * @param predictor a ClimateSensitivePredictor instance
	 * @param resolution a Resolution enum
	 * @return the number of days
	 */
	public default double getAnnualNbFrostDays(ClimateSensitivePredictor predictor, Resolution resolution) {
		return getAnnualNbFrostDays(REpiceaClimateVariableProvider.getInformationFromPredictor(predictor, 
				AnnualFrostDaysProvider.class, 
				resolution));
	}

}
