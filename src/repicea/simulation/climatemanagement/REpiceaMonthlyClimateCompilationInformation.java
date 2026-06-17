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
package repicea.simulation.climatemanagement;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

/**
 * A class to store the information related to how the monthly
 * climate variables are compiled annually.<p>
 * Typically, precipitation is summed up whereas temperature
 * is averaged over some months of the year.
 * @author Mathieu Fortin - March 2026
 */
public class REpiceaMonthlyClimateCompilationInformation {

	final List<Integer> selectedMonths;
	final boolean isAverage;

	/**
	 * A specific class with the information on the compilation of
	 * climate variables observed on a monthly basis.
	 * @param months an array of Integer instances
	 * @param average true if the result should be averaged on the months or false if it is simply summed up.
	 */
	public REpiceaMonthlyClimateCompilationInformation(Integer[] months, boolean average) {
		if (months == null || months.length == 0) {
			throw new InvalidParameterException("The month argument should be a non empty array of integers!");
		}
		selectedMonths = Arrays.asList(months);
		this.isAverage = average;
	}

}
