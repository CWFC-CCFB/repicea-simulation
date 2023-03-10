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

/**
 * This interface ensures the tree object can provide the diameter of any cross section along
 * its bole, typically through a stem taper model.
 * @author Mathieu Fortin - December 2012
 */
public interface CrossSectionDiameterProvider {

	
	/**
	 * This method returns the diameter (cm) for a given cross section along the bole.
	 * @param heightM the height of the cross section
	 * @param overBark a boolean to indicate whether the radius should be calculated over or under bark
	 * @return the diameter in cm (double)
	 */
	public double getCrossSectionDiameterCm(double heightM, boolean overBark);

}
