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
package repicea.simulation.io;

import repicea.io.tools.LevelProviderEnum;

public class FieldUtilities {

	public enum Level {
		stratumLevel,
		plotLevel, 
		treeLevel 
	}

	public enum FieldID implements LevelProviderEnum {
		STRATUM(Level.stratumLevel),
		
		PLOT(Level.plotLevel),
		PLOT_AREA(Level.plotLevel),
		PLOT_MEASUREMENTID(Level.plotLevel),
		CRUISE_LINE(Level.plotLevel),
		
		LATITUDE(Level.plotLevel),
		LONGITUDE(Level.plotLevel),
		ALTITUDE(Level.plotLevel),
		
		ECOREGION(Level.plotLevel),
		TYPEECO(Level.plotLevel),
		DRAINAGE_CLASS(Level.plotLevel),
		SLOPE_CLASS(Level.plotLevel),
		LAND_USE(Level.plotLevel),
		
		ORIGIN(Level.plotLevel),
		DISTURBANCE(Level.plotLevel),
		
		PLOTWEIGHT(Level.plotLevel),
		
		PRECTOT(Level.plotLevel),
		MEANTEMP(Level.plotLevel),
		DEGJR(Level.plotLevel),
		PRECUTIL(Level.plotLevel),
		PRECSAIS(Level.plotLevel),
		JRXGEL(Level.plotLevel),
		JRXGELC(Level.plotLevel),
		JRCROIS(Level.plotLevel),
		DPV(Level.plotLevel),
		ARIDITE(Level.plotLevel),
		NEIGEP(Level.plotLevel),
		NEIGET(Level.plotLevel),

		SPECIES(Level.treeLevel),
		TREEID(Level.treeLevel),
		TREESTATUS(Level.treeLevel),
		TREEFREQ(Level.treeLevel),
		TREEDHPCM(Level.treeLevel),
		TREEHEIGHT(Level.treeLevel),
		TREEVOLUME(Level.treeLevel),
		TREEQUALITY(Level.treeLevel),
		
		AGE3M(Level.plotLevel),
		AGE4M(Level.plotLevel),
		AGE7M(Level.plotLevel),
		AGE12M(Level.plotLevel),
		AGEHD(Level.plotLevel),
		DOMINANT_HEIGHT(Level.plotLevel);
		
		private Level level;
		
		FieldID(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getFieldLevel() {
			return level;
		}
	}

}
