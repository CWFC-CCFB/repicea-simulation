/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2026 His Majesty the King in Right of Canada
 * Authors: Mathieu Fortin, Canadian Forest Service
 * 			Hugues Power, Direction de la recherche forestiere du Quebec
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
package repicea.simulation.species;

import java.util.List;

import repicea.simulation.species.REpiceaSpecies.Species;
import repicea.simulation.species.REpiceaSpecies.SpeciesLocale;

/**
 * Ensure the model can provide the list of eligible species and its scope.
 * @author Mathieu Fortin - February 2026
 */
public interface REpiceaSpeciesCompliantObject {

	/**
	 * Provide the list of eligible species for this model.
	 * @return a List of Species objects
	 */
	public List<Species> getEligibleSpecies();

	/**
	 * Provide the scope of the model.
	 * @return a SpeciesLocale enum
	 */
	public SpeciesLocale getScope();
	
}
