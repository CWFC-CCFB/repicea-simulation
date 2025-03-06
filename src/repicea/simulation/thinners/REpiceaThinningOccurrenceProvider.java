/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2025 His Majesty the King in right of Canada
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
package repicea.simulation.thinners;

/**
 * An interface to ensure a particular instance 
 * can provide the basic information on the thinning that
 * occurred.
 * @author Mathieu Fortin - March 2025
 */
public interface REpiceaThinningOccurrenceProvider {

	/**
	 * Provide the date of of occurrence.
	 * @return an integer
	 */
	public int getOccurrenceDateYr();
	
	/**
	 * Provide the definition of the treatment.
	 * @return an REpiceaTreatmentDefinition instance
	 */
	public REpiceaTreatmentDefinition getTreatmentDefinition();
}
