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
package repicea.simulation.covariateproviders.treelevel;

/**
 * This interface ensures the tree instance can provide the basal
 * area of the trees with smaller diameter than its own dbh.
 * @author Mathieu Fortin - May 2026
 */
public interface BasalAreaSmallerThanSubjectM2HaProvider {

	/**
	 * This method returns the basal area of all the trees with dbh smaller than this tree instance.<p>
	 * A typical implementation is plot basal area (m2/ha) minus basal area of larger trees (m2/ha) 
	 * minus tree basal area (m2/ha).
	 * @return basal area in m2/ha
	 */
	public double getBasalAreaSmallerThanSubjectM2Ha();

}
