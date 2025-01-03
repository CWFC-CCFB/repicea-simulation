/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2009-2017 Mathieu Fortin for Rouge-Epicea
 * Copyright (C) 2024-2025 His Majesty the King in right of Canada
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

import java.util.HashMap;
import java.util.Map;

import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * The LandUseProvider interface ensures that the plot instance knows its land use.
 * @author Mathieu Fortin - July 2018, January 2025
 */
public interface LandUseProvider {

	public static enum LandUse implements TextableEnum {
		/**
		 * Land for wood production without constraints.
		 */
		WoodProduction(true, "Wood Production", "Production ligneuse"),
		/**
		 * Unproductive land (e.g., bare land, unforested peatland).
		 */
		Unproductive(false, "Unproductive", "Improductif"),
		/**
		 * Conservation areas. 
		 */
		Conservation(false, "Conservation", "Conservation"),
		/**
		 * Land for wood production with constraints (e.g., species habitat).
		 */
		SensitiveWoodProduction(true, "Wood production with constraints", "Production ligneuse avec contraintes");
		
		static Map<String, LandUse> EligibleStringMap;
		
		final boolean isHarvestingAllowed;
		
		LandUse(boolean isHarvestingAllowed, String englishText, String frenchText) {
			this.isHarvestingAllowed = isHarvestingAllowed;
			setText(englishText, frenchText);
		}
		
		/**
		 * Provide the information whether harvesting is allowing in this
		 * land use.
		 * @return a boolean
		 */
		public boolean isHarvestingAllowed() {return isHarvestingAllowed;}

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String toString() {
			return REpiceaTranslator.getString(this);
		}

		private static Map<String, LandUse> getEligibleStringMap() {
			if (EligibleStringMap == null) {
				EligibleStringMap = new HashMap<String, LandUse>();
				for (LandUse lu : LandUse.values()) {
					EligibleStringMap.put(lu.name(), lu);
				}
			}
			return EligibleStringMap;
		}

		/**
		 * Check whether this string matches a LandUse enum name.
		 * @param str the String to be checked
		 * @return a boolean
		 */
		public static boolean isStringEligible(String str) {
			return getEligibleStringMap().containsKey(str);
		}
		
		/**
		 * Convert a string into a LandUse enum if it is eligible.
		 * @param str the string to be converted
		 * @return a LandUse enum or null if the string is not eligible
		 * @see LandUse#isStringEligible
		 */
		public static LandUse getLandUseFromString(String str) {
			if (isStringEligible(str)) {
				return getEligibleStringMap().get(str);
			} else {
				return null;
			}
		}
		
	}
	
	/**
	 * This method returns the land use of the plot instance.
	 * @return a LandUse enum
	 */
	public LandUse getLandUse();
}
