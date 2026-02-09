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

import repicea.simulation.climate.REpiceaClimate.ClimateVariableTemporalResolution;

/**
 * This interface ensures the plot instance can provide its growing
 * degree days (e.g. &gt; 5&deg;C).  
 * @author Mathieu Fortin - February 2026
 */
public interface AnnualGrowingDegreeDaysCelsiusProvider {

	/**
	 * Provide the growing degree-days.
	 * @param resolution the resolution of the climate variable 
	 * @return the growing degree-days (&deg;C)
	 */
	public double getGrowingDegreeDaysCelsius(ClimateVariableTemporalResolution resolution);

}
