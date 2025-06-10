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

import repicea.simulation.species.REpiceaSpecies.Species;
import repicea.simulation.species.REpiceaSpecies.SpeciesLocale;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.Language;

public class REpiceaSpeciesTest {

	@Test
	public void simpleQuebecLocaleTest() {
		Language originalLanguage = REpiceaTranslator.getCurrentLanguage();
		REpiceaTranslator.setCurrentLanguage(Language.French);
		List<Species> quebecSpecies = Species.getSpeciesForThisLocale(SpeciesLocale.Quebec);
		for (Species s : quebecSpecies)
			System.out.println(s.toString());
		Assert.assertEquals("Testing the number of species in Quebec", 34, quebecSpecies.size());
		REpiceaTranslator.setCurrentLanguage(originalLanguage);
	}
	

	@Test
	public void simpleIPCCLocaleTest() {
		Language originalLanguage = REpiceaTranslator.getCurrentLanguage();
		REpiceaTranslator.setCurrentLanguage(Language.French);
		List<Species> ipccSpecies = Species.getSpeciesForThisLocale(SpeciesLocale.IPCC);
		for (Species s : ipccSpecies)
			System.out.println(s.toString());
		Assert.assertEquals("Testing the number of species in IPCC locale", 24, ipccSpecies.size());
		REpiceaTranslator.setCurrentLanguage(originalLanguage);
	}

}
