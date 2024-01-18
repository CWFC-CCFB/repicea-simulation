/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2021-24 His Majesty the King in Right of Canada
 * Author: Jean-Francois Lavoie and Mathieu Fortin, Canadian Forest Service
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
package repicea.simulation.scriptapi;

import java.util.LinkedHashMap;
import java.util.List;

import repicea.io.tools.ImportFieldElement.ImportFieldElementIDCard;
import repicea.simulation.ApplicationScaleProvider.ApplicationScale;
import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider;

/**
 * An interface that ensures the Capsis model is compatible with CapsisWebAPI.
 * 
 * @author Jean-Francois Lavoie - September 2021
 * @author Mathieu Fortin - January 2024
 */
public interface ExtScriptAPI {
	
	public List<ImportFieldElementIDCard> getFieldDescriptions();
	public void setEvolutionParameters(int finalDateYr);
	public void addRecord(Object[] record);
	public boolean setFieldMatches(int[] indices);
	public ScriptResult runSimulation() throws Exception;
	public void closeProject();		// vient de GScript
	public String getCapsisVersion();
	public List<String> getSpeciesOfType(SpeciesTypeProvider.SpeciesType... type);
	public void registerOutputRequest(Request request, LinkedHashMap<String, List<String>> aggregationPatterns);
	public void setInitialParameters(int initialDateYr, 
			boolean isStochastic, 
			int nbRealizations, 
			ApplicationScale scale,
			String climateChangeOption);	// this is passed as a string so that the implementing script will cast it correctly for use.

}
