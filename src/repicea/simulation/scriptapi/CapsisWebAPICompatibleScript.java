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
import repicea.simulation.covariateproviders.samplelevel.ApplicationScaleProvider.ApplicationScale;
import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider;

/**
 * An interface that ensures the Capsis model is compatible with CapsisWebAPI.
 * 
 * @author Jean-Francois Lavoie - September 2021
 * @author Mathieu Fortin - January 2024
 */
public interface CapsisWebAPICompatibleScript {
	
	/**
	 * Provide a descriptions of the different input fields.
	 * @return a List of ImportFieldElementIDCard instances.
	 */
	public List<ImportFieldElementIDCard> getFieldDescriptions();
	
	/**
	 * Set the evolution parameters of the simulation. 
	 * @param finalDateYr an integer that is the date of the final step.
	 */
	public void setEvolutionParameters(int finalDateYr);
	
	/**
	 * Add a record to be read as input for the simulation.
	 * @param record an array of object
	 */
	public void addRecord(Object[] record);

	/**
	 * Set the indices of the fields contained in the input that match the fields for the simulation. <p>
	 * For instance, if the plot id is the first field for the simulation, then the first integer in the 
	 * array could be 2, indicating the field containing the plot id in the input is the third one. For optional
	 * fields, the index can be set to -1, indicating that this field is not part of the input. 
	 * @param indices an array of integer.
	 * @return a boolean true if the matches are consistent
	 */
	public boolean setFieldMatches(int[] indices);

	/**
	 * Run the simulation.
	 * @return a ScriptResult instance containing the results of the simulation.
	 * @throws Exception if an error occurred during the simulation
	 */
	public ScriptResult runSimulation() throws Exception;

	/**
	 * Close the project. <p>
	 * For consistency with CAPSIS.
	 */
	public void closeProject();		// vient de GScript

	/**
	 * Provide the current version of CAPSIS.
	 * @return a String
	 */
	public String getCapsisVersion();

	/**
	 * Provide a list of species code belonging to particular species types.
	 * @param type a SpeciesType enum (either ConiferousSpecies or BroadleavedSpecies)
	 * @return a List of Strings that correspond to the species code.
	 */
	public List<String> getSpeciesOfType(SpeciesTypeProvider.SpeciesType... type);

	/**
	 * Register an output request for the simulation.<p>
	 * An output request contains a Request enum that defines the variable and the status class of the trees to be included in the output
	 * request. It may also contain aggregation patterns. These patterns make it possible to consider only single species or groups of species.
	 * @param request a Request instance
	 * @param aggregationPatterns a LinkedHashMap whose keys are the species group name and the values are lists of species codes.
	 */
	public void registerOutputRequest(Request request, LinkedHashMap<String, List<String>> aggregationPatterns);

	/**
	 * Set the initial parameters of the simulation.
	 * @param initialDateYr the date (yr) of the initial step.
	 * @param isStochastic a boolean true for stochastic simulation, false for deterministic simulation.
	 * @param nbRealizations the number of realizations to be used in case of stochastic simulations.
	 * @param scale an ApplicationScale enum (either Stand or FMU)
	 * @param climateChangeOption a string that defines the climate scenario. 
	 */
	public void setInitialParameters(int initialDateYr, 
			boolean isStochastic, 
			int nbRealizations, 
			ApplicationScale scale,
			String climateChangeOption);	// this is passed as a string so that the implementing script will cast it correctly for use.

	/**
	 * Return the list of requests that can be used with this script instance.
	 * @return a List of Request enums
	 */
	public List<Request> getPossibleRequests();
	
	
	/**
	 * Provide a double between 0 and 1, which indicates the progress of the simulation.
	 * @return a double
	 */
	public double getSimulationProgress();
	
	/**
	 * Provide the scope of the model.<p>
	 * The keys refer to the attribute and the values to the possible values.
	 * @return a Map instance
	 */
	public LinkedHashMap<String, Object> getScope();
}
