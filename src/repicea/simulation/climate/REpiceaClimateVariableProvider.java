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
package repicea.simulation.climate;

import java.util.Map;

import repicea.simulation.ClimateSensitivePredictor;
import repicea.simulation.climate.REpiceaClimateVariableInformation.Resolution;

/**
 * An empty interface just to identify climate-related interfaces.
 * @author Mathieu Fortin - February 2026
 */
public interface REpiceaClimateVariableProvider {

	static REpiceaClimateVariableInformation getInformationFromPredictor(ClimateSensitivePredictor predictor, Class<? extends REpiceaClimateVariableProvider> clazz, Resolution resolution) {
		Map<Resolution, REpiceaClimateVariableInformation> infos = predictor.getClimateVariableInformationMap().get(clazz);
		if (infos == null) {
			throw new UnsupportedOperationException("It seems the predictor does not include any information regarding this climate variable: " + clazz.getSimpleName());
		} else {
			REpiceaClimateVariableInformation info = infos.get(resolution);
			if (info == null) {
				throw new UnsupportedOperationException("It seems the resolution: " + resolution.name() + " is not supported for this climate variable:" + clazz.getSimpleName());
			}			
			return info;
		}
	}
	
}
