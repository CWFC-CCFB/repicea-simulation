/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2021-24 His Majesty the King in Right of Canada
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
package repicea.simulation.covariateproviders;

import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

public class MethodProviderEnum {
	
	
	public static enum VariableForEstimation implements TextableEnum{
		/**
		 * Stem density.
		 */
		N("Stem density", "Densit\u00E9 d'arbres"),
		/**
		 * Basal area.
		 */
		G("Basal area", "Surface terri\u00E8re"),
		/**
		 * Volume.
		 */
		V("Volume", "Volume"),
		/**
		 * Aboveground biomass.
		 */
		B("Aboveground biomass", "Biomasse a\\u00E9rienne"),
		/**
		 * Dominant height.
		 */
		HDOM("Dominant height", "Hauteur dominante");

		VariableForEstimation(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public String toString() {
			return REpiceaTranslator.getString(this);
		}

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
	}
}
