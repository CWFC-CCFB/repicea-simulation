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

import repicea.simulation.climate.REpiceaClimateManager.ClimateVariableTemporalResolution;

/**
 * This interface ensures the stand instance can provide its total precipitation 
 * from June to August.
 * @author Mathieu Fortin - February 2026
 */
public interface TotalPrecipitationFromJuneToAugustMmProvider {

	/**
	 * Provide the total precipitation from March to May in mm.
	 * @param resolution the resolution of the climate variable 
	 * @return a double
	 */
	public double getTotalPrecipitationFromJuneToAugustMm(ClimateVariableTemporalResolution resolution);

}
