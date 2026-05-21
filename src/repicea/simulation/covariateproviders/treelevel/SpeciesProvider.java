/*
 * This file is part of the repicea library.
 *
 * Copyright (C) 2009-2012 Mathieu Fortin for Rouge-Epicea
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
package repicea.simulation.covariateproviders.treelevel;

import repicea.simulation.species.REpiceaSpecies.Species;

/**
 * This interface ensures the tree can provide its own species name.
 * @author Mathieu Fortin - March 2013
 */
public interface SpeciesProvider {

	/**
	 * Provide the species of this tree.
	 * @return a Species enum
	 */
	public default Species getSpecies() {
		return getSpecies(null);
	}
	
	/**
	 * Provide the species of this tree. <p>
	 * The argument caller allows for different implementation depending on who is calling.
	 * @param caller the instance that needs the tree species. Can be null. In that case, it should
	 * be the default implementation.
	 * @return a Species enum
	 */
	public Species getSpecies(Object caller);

	
	
}
