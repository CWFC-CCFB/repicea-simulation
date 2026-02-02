/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2021-2025 His Majesty the King in Right of Canada
 * Copyright (C) 2025 Ministere des Ressources naturelles et des Forets du Quebec
 * Authors: Mathieu Fortin, Canadian Forest Service
 * 			Hugues Power, Direction de la recherche forestiere du Quebec
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
 */package repicea.simulation.species;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider.SpeciesType;
import repicea.simulation.species.REpiceaSpecies.Species;
import repicea.simulation.species.REpiceaSpecies.SpeciesLocale;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.Language;

public class REpiceaSpeciesTest {

	@Test
	public void test01QuebecLocale() {
		Language originalLanguage = REpiceaTranslator.getCurrentLanguage();
		REpiceaTranslator.setCurrentLanguage(Language.French);
		List<Species> quebecSpecies = Species.getSpeciesForThisLocale(SpeciesLocale.Quebec);
		System.out.println("==== Quebec Species ====");
		for (Species s : quebecSpecies)
			System.out.println(s.toString() + " - " + s.getLatinName());
		Assert.assertEquals("Testing the number of species in Quebec", 36, quebecSpecies.size());
		REpiceaTranslator.setCurrentLanguage(originalLanguage);
	}
	

	@Test
	public void test02IPCCLocale() {
		Language originalLanguage = REpiceaTranslator.getCurrentLanguage();
		REpiceaTranslator.setCurrentLanguage(Language.French);
		List<Species> ipccSpecies = Species.getSpeciesForThisLocale(SpeciesLocale.IPCC);
		System.out.println("==== IPCC Species ====");
		for (Species s : ipccSpecies)
			System.out.println(s.toString() + " - " + s.getLatinName());
		Assert.assertEquals("Testing the number of species in IPCC locale", 24, ipccSpecies.size());
		REpiceaTranslator.setCurrentLanguage(originalLanguage);
	}

	@Test
	public void test03FranceLocale() {
		Language originalLanguage = REpiceaTranslator.getCurrentLanguage();
		REpiceaTranslator.setCurrentLanguage(Language.French);
		List<Species> franceSpecies = Species.getSpeciesForThisLocale(SpeciesLocale.France);
		System.out.println("==== French Species ====");
		for (Species s : franceSpecies)
			System.out.println(s.toString() + " - " + s.getLatinName());
		Assert.assertEquals("Testing the number of species in France", 28, franceSpecies.size());
		REpiceaTranslator.setCurrentLanguage(originalLanguage);
	}
	
	@Test
	public void test04NorthAmericaLocale() {
		Language originalLanguage = REpiceaTranslator.getCurrentLanguage();
		REpiceaTranslator.setCurrentLanguage(Language.French);
		List<Species> northAmericaSpecies = Species.getSpeciesForThisLocale(SpeciesLocale.NorthAmerica);
		System.out.println("==== North American Species ====");
		for (Species s : northAmericaSpecies)
			System.out.println(s.toString() + " - " + s.getLatinName());
		Assert.assertEquals("Testing the number of species in North America", 43, northAmericaSpecies.size());
		REpiceaTranslator.setCurrentLanguage(originalLanguage);
	}

	@SuppressWarnings("unused")
	@Test
	public void test05TestSpeed() {
		long initTime = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			double barkProportion = REpiceaSpecies.Species.Acer_saccharum.getBarkProportionOfWoodVolume(SpeciesLocale.Quebec);
		}
		long finalTime = System.currentTimeMillis() - initTime;
		System.out.println("Time to get bark proportion when NOT AVAILABLE in locale: " + finalTime);

		initTime = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			double barkProportion = REpiceaSpecies.Species.Acer_saccharum.getBarkProportionOfWoodVolume(SpeciesLocale.NorthAmerica);
		}
		finalTime = System.currentTimeMillis() - initTime;
		System.out.println("Time to get bark proportion when AVAILABLE in locale: " + finalTime);

	}

	
	public static void main(String[] args) {
		int nbBroadleaved = 0;
		int nbConiferous = 0;
		double meanBarkProportionBroadleaved = 0;
		double meanBarkProportionConiferous = 0;
		double meanBasicDensityBroadleaved = 0;
		double meanBasicDensityConiferous = 0;
		SpeciesLocale locale = SpeciesLocale.Quebec;

		int nbSpecies = 0;
		for (Species sp : Species.getSpeciesForThisLocale(locale)) {
			if (sp.getSpeciesType() == SpeciesType.BroadleavedSpecies) {
				nbBroadleaved++;
				meanBarkProportionBroadleaved += sp.getBarkProportionOfWoodVolume(locale);
				meanBasicDensityBroadleaved += sp.getBasicWoodDensity(locale);
			} else if (sp.getSpeciesType() == SpeciesType.ConiferousSpecies){
				nbConiferous++;
				meanBarkProportionConiferous += sp.getBarkProportionOfWoodVolume(locale);
				meanBasicDensityConiferous += sp.getBasicWoodDensity(locale);
			}
			nbSpecies++;
		}
		
		if (nbBroadleaved + nbConiferous != nbSpecies) {
			throw new UnsupportedOperationException("Some species have been left out!");
		}
		
		System.out.println("Mean basic density coniferous = " + meanBasicDensityConiferous/nbConiferous);
		System.out.println("Mean bark proportion coniferous = " + meanBarkProportionConiferous/nbConiferous);
		System.out.println("Mean basic density broadleaved = " + meanBasicDensityBroadleaved/nbBroadleaved);
		System.out.println("Mean bark proportion broadleaved = " + meanBarkProportionBroadleaved/nbBroadleaved);

	}

}
