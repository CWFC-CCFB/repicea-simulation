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
package repicea.simulation;

import java.util.Map;

import repicea.simulation.climatemanagement.REpiceaClimateVariableInformation;
import repicea.simulation.climatemanagement.REpiceaClimateVariableProvider;
import repicea.simulation.climatemanagement.REpiceaClimateVariableInformation.Resolution;

/**
 * An interface that applies to predictors that are climate sensitive.
 * @author Mathieu Fortin - February 2026
 */
public interface ClimateSensitivePredictor {

	/**
	 * Provide the information on the climate variable so that they can be 
	 * retrieved using a weather generator.
	 * @return a Map of REpiceaClimateVariableProvider derived classes as key and REpiceaClimateVariableInformation as value
	 */
	public Map<Class<? extends REpiceaClimateVariableProvider>, Map<Resolution, REpiceaClimateVariableInformation>> getClimateVariableInformationMap(); 
	
}
