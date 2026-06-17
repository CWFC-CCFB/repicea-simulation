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
import repicea.simulation.climatemanagement.REpiceaClimateVariableInformation;
import repicea.simulation.climatemanagement.REpiceaClimateVariableProvider;
import repicea.simulation.climatemanagement.REpiceaClimateVariableInformation.Resolution;

/**
 * This interface ensures the plot instance can provide its mean 
 * vapour pressure deficit (VPD) for daylight period from June to
 * August. 
 * @author Mathieu Fortin - February 2026
 */
public interface MeanVapourPressureDeficitDaylightFromJuneToAugustHPaProvider extends REpiceaClimateVariableProvider {

	/**
	 * Provide the mean daylight VPD from June to August.
	 * @param info an REpiceaClimateVariableInformation instance defining the climate variable 
	 * @return the daylight VPD (hPa)
	 */
	public double getMeanVPDDaylightFromJuneToAugustHPa(REpiceaClimateVariableInformation info);
	
	/**
	 * Default implementation to retrieve the variable from the predictor itself.
	 * @param predictor a ClimateSensitivePredictor instance
	 * @param resolution a Resolution enum
	 * @return the daylight VPD (hPa)
	 */
	public default double getMeanVPDDaylightFromJuneToAugustHPa(ClimateSensitivePredictor predictor, Resolution resolution) {
		return getMeanVPDDaylightFromJuneToAugustHPa(REpiceaClimateVariableProvider.getInformationFromPredictor(predictor, 
				MeanVapourPressureDeficitDaylightFromJuneToAugustHPaProvider.class,
				resolution));
	}

}
