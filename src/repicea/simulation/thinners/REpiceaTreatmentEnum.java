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

import repicea.util.FullNameEnum;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * An interface that applies to enum variables defining silvicultural
 * treatments.
 * @author Mathieu Fortin - March 2025
 */
public interface REpiceaTreatmentEnum extends  TextableEnum, FullNameEnum {

	/**
	 * This method is already included in the Enum class.
	 * @return a String
	 */
	public String name();
	
	/**
	 * Indicate whether this treatment is a final one.
	 * @return a boolean
	 */
	public boolean isFinalCut();
	
}
