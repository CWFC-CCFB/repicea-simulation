/*
 * This file is part of the repicea library.
 *
 * Copyright (C) 2009-2019 Mathieu Fortin for Rouge-Epicea
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

/**
 * This interface ensures the plot or the plot instance can provide its 
 * seasonal temperature over the interval. <p>
 * This interface refers to the publication of Manso et al. 2015 Forestry in 
 * the European context.
 * @author Mathieu Fortin - July 2019
 *
 */
public interface MeanSeasonalTemperatureCelsiusProvider {

	/**
	 * Provide the monthly mean temperature above 6&deg;C.
	 * @return the temperature (&deg;C)
	 */
	public double getMeanSeasonalTemperatureCelsius();

//	/**
//	 * Default implementation to retrieve the variable from the predictor itself.
//	 * @param predictor a ClimateSensitivePredictor instance
//	 * @return the number of days
//	 */
//	public default double getMeanSeasonalTemperatureCelsius(ClimateSensitivePredictor predictor) {
//		return getMeanSeasonalTemperatureCelsius(REpiceaClimateVariableProvider.getInformationFromPredictor(predictor, MeanSeasonalTemperatureCelsiusProvider.class));
//	}

}
