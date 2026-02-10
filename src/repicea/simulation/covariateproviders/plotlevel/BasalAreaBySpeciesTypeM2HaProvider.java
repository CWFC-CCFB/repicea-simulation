/*
 * This file is part of the repicea library.
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
package repicea.simulation.covariateproviders.plotlevel;

import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider.SpeciesType;

/**
 * Ensure the plot instance can provide its basal area by species type (broadleaved or coniferous)
 * @author Mathieu Fortin - February 2026
 */
public interface BasalAreaBySpeciesTypeM2HaProvider {

	/**
	 * Provide the basal area of a particular species type.
	 * @param type a SpeciesType enum 
	 * @return the basal area (m2/ha)
	 * @see SpeciesType enum 
	 */
	public double getBasalAreaM2HaForThisSpeciesType(SpeciesType type);
}
