/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2009-2021 Mathieu Fortin for Rouge Epicea.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import repicea.math.DiagonalMatrix;
import repicea.math.Matrix;
import repicea.simulation.climate.REpiceaClimateGenerator.ClimateChangeScenario;
import repicea.stats.data.DataSet;
import repicea.stats.data.Observation;

/**
 * Handle the result of a simulation performed through the ExtScript class
 * @author Mathieu Fortin - January 2021
 */
public class ScriptResult {	
	
	public static final String DateYrFieldName = "DateYr";	
	public static final String TimeSinceInitialDateYrFieldName = "timeSinceInitialDateYr";
	public static final String OutputTypeFieldName = "OutputType";
	public static final String EstimateFieldName = "Estimate";
	public static final String TotalVarianceFieldName = "TotalVariance";
	public static final String VarianceEstimatorType = "VarianceEstimatorType";	

	private final DataSet dataset;
	private final List<String> outputTypes;
	private final int nbRealizations;
	private final int nbPlots;
	private final ClimateChangeScenario climateChangeScenario;
	private final String growthModel;
	
	/**
	 * Constructor.
	 * @param nbRealizations the number of realizations (0 if deterministic or &#62; 0 if stochastic)
	 * @param nbPlots the number of plots used in the projection
	 * @param climateChangeScenario a ClimateChangeScenario enum
	 * @param growthModel the model name
	 * @param dataset a DataSet instance containing the projection for a group of plots
	 */
	public ScriptResult(int nbRealizations, 
			int nbPlots,
			ClimateChangeScenario climateChangeScenario,
			String growthModel,
			DataSet dataset) {
		this.nbRealizations = nbRealizations;
		this.nbPlots = nbPlots;
		this.dataset = dataset;
		this.climateChangeScenario = climateChangeScenario;
		this.growthModel = growthModel;
		
		List<Integer> sortingIndex = new ArrayList<Integer>();
		int outputTypeFieldNameIndex = getDataSet().getFieldNames().indexOf(OutputTypeFieldName);
		sortingIndex.add(outputTypeFieldNameIndex);
		sortingIndex.add(getDataSet().getFieldNames().indexOf(DateYrFieldName));
		getDataSet().sortObservations(sortingIndex);	// the dataset is sorted according to the output type and then the date yr
		
		outputTypes = new ArrayList<String>();
		for (Observation o : getDataSet().getObservations()) {
			String outputType =  o.toArray()[outputTypeFieldNameIndex].toString();
			if (!outputTypes.contains(outputType)) {
				outputTypes.add(outputType);
			}
		}
		Collections.sort(outputTypes);
	}

	/**
	 * Return an empty data set already formatted with the appropriate field names. <br>
	 * <br>
	 * This one is meant for stochastic model projections which include variance estimates.
	 * @return a DataSet instance
	 */
	public static DataSet createEmptyDataSet() {
		return new DataSet(Arrays.asList(new String[] {DateYrFieldName,
				TimeSinceInitialDateYrFieldName,
				OutputTypeFieldName, 
				EstimateFieldName, 
				TotalVarianceFieldName,
				VarianceEstimatorType}));
	}

	/**
	 * Return an empty data set already formatted with the appropriate field names. <br>
	 * <br>
	 * This one does not contain any variance field.
	 * @return a DataSet instance
	 */
	public static DataSet createEmptyReducedDataSet() {
		return new DataSet(Arrays.asList(new String[] {DateYrFieldName,
				TimeSinceInitialDateYrFieldName,
				OutputTypeFieldName, 
				EstimateFieldName}));
	}

	public int getNbRealizations() {return nbRealizations;}
	
//	public ClimateChangeScenario getClimateChangeScenario() {return climateChangeScenario;}
	
	public String getGrowthModel() {return growthModel;}
	
	public DataSet getDataSet() {return dataset;}
	
	public List<String> getOutputTypes() {return outputTypes;}

	/**
	 * Check if the variance field is contained in the DataSet instance.
	 * @return true if the variance is in or false otherwise
	 */
	public boolean isVarianceAvailable() {
		return getDataSet().getFieldNames().indexOf(TotalVarianceFieldName) > -1;
	}
	
	/**
	 * Sort the dataset and create the variance-covariance matrix of the error term.
	 * <br>
	 * The current implementation assumes the variance-covariance matrix is a diagonal matrix.
	 * If the DataSet instance does not have a variance field, it returns null.
	 * @param outputType a string defining the output type we are interested in (e.g. volume_alive_allspecies)
	 * @return a Matrix
	 */
	public Matrix computeVarCovErrorTerm(String outputType) {
		if (isVarianceAvailable()) {
			int outputFieldTypeIndex = getDataSet().getFieldNames().indexOf(OutputTypeFieldName);
			List<Observation> selectedObservations = new ArrayList<Observation>();
			for (Observation o : getDataSet().getObservations()) {
				if (o.toArray()[outputFieldTypeIndex].equals(outputType)) {
					selectedObservations.add(o);
				}
			}
			int nbObs = selectedObservations.size();
			int varianceIndex = getDataSet().getFieldNames().indexOf(TotalVarianceFieldName);
			DiagonalMatrix mat = new DiagonalMatrix(nbObs);
			for (int i = 0; i < nbObs; i++) {
				mat.setValueAt(i, i, (Double) selectedObservations.get(i).toArray()[varianceIndex]);
			}
			return mat;
		} else {
			return  null;
		}
	}

	
	
	private static void appendDifferenceMessage(StringBuilder sb, String message) {
		if (sb.length() > 0) {
			sb.append(System.lineSeparator());
		}
		sb.append(message);
	}
	
	/**
	 * Check the differences between this instance and another ScriptResult instance.<p>
	 * 
	 * @param other another ScriptResult instance
	 * @return a String containing the difference. If the string is empty, then there is no difference.
	 */
	public String checkForDifferences(ScriptResult other) {
		StringBuilder sb = new StringBuilder();
		if (!dataset.getFieldNames().equals(other.dataset.getFieldNames())) {
			appendDifferenceMessage(sb, "Dataset fieldnames are different: " + System.lineSeparator() + 
					"This = " + dataset.getFieldNames() + System.lineSeparator() + 
					"New  = " + other.dataset.getFieldNames());
		}
		if (!dataset.getFieldTypes().equals(other.dataset.getFieldTypes())) {
			appendDifferenceMessage(sb, "Dataset field types are different: " + System.lineSeparator() + 
					"This = " + dataset.getFieldTypes() + System.lineSeparator() + 
					"New  = " + other.dataset.getFieldTypes());
		}
		if (!outputTypes.equals(other.outputTypes)) {
			appendDifferenceMessage(sb, "Output types are different: " + System.lineSeparator() + 
					"This = " + outputTypes + System.lineSeparator() + 
					"New  = " + other.outputTypes);
		}
		if (nbRealizations != other.nbRealizations) {
			appendDifferenceMessage(sb, "Numbers of realizations are different: " + 
					"This = " + nbRealizations + "; New = " + other.nbRealizations);
		}
		if (!climateChangeScenario.equals(other.climateChangeScenario)) {
			appendDifferenceMessage(sb, "Climate change scenarios are different: " + 
					"This = " + climateChangeScenario + "; New = " + other.climateChangeScenario);
		}
		if (!growthModel.equals(other.growthModel)) {
			appendDifferenceMessage(sb, "Growth models are different: " + 
					"This = " + growthModel + "; New = " + other.growthModel);
		}
		return sb.toString();
	}

	/**
	 * Return the climate change scenario.
	 * @return a String
	 */
	public String getClimateChangeScenario() {
		if (climateChangeScenario instanceof Enum) {
			return ((Enum<?>) climateChangeScenario).name();
		} else {
			return climateChangeScenario.toString();
		}
	}
	
	/**
	 * Provide the number of plots in this simulation script.
	 * @return an integer
	 */
	public int getNbPlots() {
		return nbPlots;
	}
	
}
