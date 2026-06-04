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
import java.util.concurrent.ConcurrentHashMap;

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
	
	/**
	 * Provide the surrogate map for the predictor or tool.<p>
	 * The surrogate map can be cleared and filled with specific
	 * surrogates.
	 * @return a ConcurrentHashMap instance
	 */
	public ConcurrentHashMap<Species, Species> getSurrogateMap();

	/**
	 * Reset the surrogate map to its default value.
	 */
	public void setSurrogateMapToDefaultValue();
	
	
	/**
	 * Convert the Species instance into an eligible species if possible.<p>
	 * If the species is among the eligible species it is simply returned. If
	 * the species isn't, then we use its surrogate and see if it is eligible or
	 * in the surrogate map already. If it isn't, we then try with the surrogate of
	 * the surrogate and so on.
	 * @param sp a Species enum 
	 * @return a Species enum
	 * @throws UnsupportedOperationException if the species is not eligible and there are no
	 * valid surrogates.
	 */
	public default Species convertToEligibleSpecies(Species sp) {
		if (getEligibleSpecies().contains(sp)) {
			return sp;
		} else if (getSurrogateMap().containsKey(sp)) {
			return getSurrogateMap().get(sp);
		} else {
			Species tmpSp = REpiceaSpecies.getSurrogate(sp);
			while (tmpSp != null) {
				if (getEligibleSpecies().contains(tmpSp)) {
					getSurrogateMap().put(sp, tmpSp);
					return tmpSp;
				} else if (getSurrogateMap().containsKey(tmpSp)) {
					Species outputSp = getSurrogateMap().get(tmpSp);
					getSurrogateMap().put(sp, outputSp);
					return outputSp;
				}
				tmpSp = REpiceaSpecies.getSurrogate(tmpSp);
			}
			throw new UnsupportedOperationException("Species " + sp.getLatinName() + " and its surrogates are not eligible for this class " + getClass().getSimpleName());
		}
	}
}
