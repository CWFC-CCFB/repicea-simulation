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
 */
package repicea.simulation.species;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repicea.simulation.covariateproviders.treelevel.BarkProportionProvider;
import repicea.simulation.covariateproviders.treelevel.BasicWoodDensityProvider;
import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * An interface that ensures the instance can provide 
 * <ul>
 * <li> its species type
 * <li> its bark proportion
 * <li> its basic wood density
 * <li> its name in different language through the Textable enum interface
 * </ul>
 * @author Mathieu Fortin - June 2025
 */
public interface REpiceaSpecies extends TextableEnum, SpeciesTypeProvider, BarkProportionProvider, BasicWoodDensityProvider {

	public static enum SpeciesLocale {
		IPCC,
		Quebec,
		France;
	}
	
	public static enum Species implements REpiceaSpecies {
		Abies_spp(SpeciesType.ConiferousSpecies, 0.40, 0.118, "Fir", "Sapin", SpeciesLocale.IPCC),
		Acer_spp(SpeciesType.BroadleavedSpecies, 0.52, 0.109, "Maple", "Erable", SpeciesLocale.IPCC),
		Alnus_spp(SpeciesType.BroadleavedSpecies, 0.45, 0.115, "Alder", "Aulne", SpeciesLocale.IPCC),
		Betula_spp(SpeciesType.BroadleavedSpecies, 0.51, 0.110, "Birch", "Bouleau", SpeciesLocale.IPCC),
		Carpinus_betulus(SpeciesType.BroadleavedSpecies, 0.63, 0.086, "Hornbeam", "Charme", SpeciesLocale.IPCC), // this one is from IPCC guidelines 2003
		Castanea_sativa(SpeciesType.BroadleavedSpecies, 0.48, 0.150, "Chestnut", "Chataignier", SpeciesLocale.IPCC),	// this one is from IPCC guidelines 2003
		Fagus_sylvatica(SpeciesType.BroadleavedSpecies, 0.58, 0.060, "European beech", "H\u00EAtre europ\u00E9en", SpeciesLocale.IPCC),
		Fraxinus_spp(SpeciesType.BroadleavedSpecies, 0.57, 0.160, "Ash", "Fr\u00EAne", SpeciesLocale.IPCC),
		Juglans_spp(SpeciesType.BroadleavedSpecies, 0.53, 0.150, "Wallnut", "Noyer", SpeciesLocale.IPCC),	// this one is from IPCC guidelines 2003
		Larix_decidua(SpeciesType.ConiferousSpecies, 0.46, 0.140, "Larch", "M\u00E9l\u00E8ze", SpeciesLocale.IPCC),
		Picea_abies(SpeciesType.ConiferousSpecies, 0.40, 0.126, "Norway spruce", "Epinette de Norv\u00E0ge", SpeciesLocale.IPCC),
		Picea_sitchensis(SpeciesType.ConiferousSpecies, 0.40, 0.126, "Sitka spruce", "Epinette de Sitka", SpeciesLocale.IPCC),
		Pinus_pinaster(SpeciesType.ConiferousSpecies, 0.44, 0.160, "Maritime pine", "Pin maritime", SpeciesLocale.IPCC),
		Pinus_radiata(SpeciesType.ConiferousSpecies, 0.38, 0.134, "Monterey pine", "Pin de Monterey", SpeciesLocale.IPCC),
		Pinus_strobus(SpeciesType.ConiferousSpecies, 0.32, 0.160, "White pine", "Pin blanc", SpeciesLocale.IPCC),
		Pinus_sylvestris(SpeciesType.ConiferousSpecies, 0.42, 0.160, "Scots pine", "Pin sylvestre", SpeciesLocale.IPCC),
		Populus_spp(SpeciesType.BroadleavedSpecies, 0.35, 0.184, "Poplar", "Peuplier", SpeciesLocale.IPCC),
		Prunus_spp(SpeciesType.ConiferousSpecies, 0.49, 0.092, "Cherry", "Cerisier", SpeciesLocale.IPCC),
		Pseudotsuga_menziesii(SpeciesType.ConiferousSpecies, 0.45, 0.173, "Douglas fir", "Sapin Douglas", SpeciesLocale.IPCC),
		Quercus_spp(SpeciesType.BroadleavedSpecies, 0.58, 0.191, "Oak", "Ch\u00EAne", SpeciesLocale.IPCC),
		Salix_spp(SpeciesType.BroadleavedSpecies, 0.45, 0.160, "Willow", "Saule", SpeciesLocale.IPCC),
		Thuja_plicata(SpeciesType.ConiferousSpecies, 0.31, 0.106, "Red cedar", "Thuya g\u00E9ant", SpeciesLocale.IPCC), // this one is from IPCC guidelines 2003
		Tilia_spp(SpeciesType.BroadleavedSpecies, 0.43, 0.105, "Basswood", "Tilleul", SpeciesLocale.IPCC),
		Tsuga_spp(SpeciesType.ConiferousSpecies, 0.42, 0.162, "Hemlock", "Pruche", SpeciesLocale.IPCC), // this one is from IPCC guidelines 2003

		Betula_papyrifera_QC(SpeciesType.BroadleavedSpecies, 0.506, 0.126, "Paper birch", "Bouleau \u00E0 papier", SpeciesLocale.Quebec),
		Betula_alleghaniensis_QC(SpeciesType.BroadleavedSpecies, 0.559, 0.098, "Yellow birch", "Bouleau jaune", SpeciesLocale.Quebec),
		Carya_cordiformis_QC(SpeciesType.BroadleavedSpecies, 0.628, 0.16, "Bitternut hickory", "Caryer cordiforme", SpeciesLocale.Quebec),
		Carya_ovata_QC(SpeciesType.BroadleavedSpecies, 0.654, 0.16, "Shagbark hickory", "Caryer ovale", SpeciesLocale.Quebec),
		Prunus_serotina_QC(SpeciesType.BroadleavedSpecies, 0.51, 0.092, "Black cherry", "Cerisier tardif", SpeciesLocale.Quebec),
		Quercus_macrocarpa_QC(SpeciesType.BroadleavedSpecies, 0.599, 0.16, "Bur oak", "Ch\u00EAne \u00E0 gros fruits", SpeciesLocale.Quebec),
		Quercus_alba_QC(SpeciesType.BroadleavedSpecies, 0.654, 0.16, "White oak", "Ch\u00EAne blanc", SpeciesLocale.Quebec),
		Quercus_rubra_QC(SpeciesType.BroadleavedSpecies, 0.581, 0.2, "Red oak", "Ch\u00EAne rouge", SpeciesLocale.Quebec),
		Picea_glauca_QC(SpeciesType.ConiferousSpecies, 0.354, 0.13, "White spruce", "Epinette blanche", SpeciesLocale.Quebec),
		Picea_rubens_QC(SpeciesType.ConiferousSpecies, 0.38, 0.13, "Red spruce", "Epinette rouge", SpeciesLocale.Quebec),
		Picea_mariana_QC(SpeciesType.ConiferousSpecies, 0.406, 0.13, "Black spruce", "Epinette noire", SpeciesLocale.Quebec),
		Acer_saccharum_QC(SpeciesType.BroadleavedSpecies, 0.597, 0.156, "Sugar maple", "Erable \u00E0 sucre", SpeciesLocale.Quebec),
		Acer_saccharinum_QC(SpeciesType.BroadleavedSpecies, 0.461, 0.086, "Silver maple", "Erable argent\u00E9", SpeciesLocale.Quebec),
		Acer_rubrum_QC(SpeciesType.BroadleavedSpecies, 0.516, 0.086, "Red maple", "Erable rouge", SpeciesLocale.Quebec),
		Fraxinus_americana_QC(SpeciesType.BroadleavedSpecies, 0.57, 0.16, "White ash", "Fr\u00EAne d'Am\u00E9rique", SpeciesLocale.Quebec),
		Fraxinus_pensylvanica_QC(SpeciesType.BroadleavedSpecies, 0.486, 0.16, "Red ash", "Fr\u00EAne de Pensylvanie", SpeciesLocale.Quebec),
		Fraxinus_nigra_QC(SpeciesType.BroadleavedSpecies, 0.468, 0.16, "Black ash", "Fr\u00EAne noir", SpeciesLocale.Quebec),
		Fagus_grandifolia_QC(SpeciesType.BroadleavedSpecies, 0.59, 0.06, "American beech", "H\u00EAtre \u00E0 grande feuilles", SpeciesLocale.Quebec),
		Larix_laricina_QC(SpeciesType.ConiferousSpecies, 0.485, 0.14, "Tamarack larch", "M\u00E9l\u00E8ze laricin", SpeciesLocale.Quebec),
		Juglans_cinerea_QC(SpeciesType.BroadleavedSpecies, 0.368, 0.15, "Butternut", "Noyer cendr\u00E9", SpeciesLocale.Quebec),
		Juglans_nigra_QC(SpeciesType.BroadleavedSpecies, 0.546, 0.15, "Black walnut", "Noyer noir", SpeciesLocale.Quebec),
		Ulmus_americana_QC(SpeciesType.BroadleavedSpecies, 0.524, 0.14, "American elm", "Orme d'Am\u00E9rique", SpeciesLocale.Quebec),
		Ostrya_virgiana_QC(SpeciesType.BroadleavedSpecies, 0.652, 0.15, "Ironwood", "Ostryer de Virginie", SpeciesLocale.Quebec),
		Populus_grandidentata_QC(SpeciesType.BroadleavedSpecies, 0.39, 0.144, "Large-tooth aspen", "Peuplier \u00E0 grandes dents", SpeciesLocale.Quebec),
		Populus_balsamifera_QC(SpeciesType.BroadleavedSpecies, 0.372, 0.163, "Black cottonwood", "Peuplier baumier", SpeciesLocale.Quebec),
		Populus_tremuloides_QC(SpeciesType.BroadleavedSpecies, 0.374, 0.144, "Trembling aspen", "Peuplier faux tremble", SpeciesLocale.Quebec),
		Populus_deltoides_QC(SpeciesType.BroadleavedSpecies, 0.352, 0.22, "Eastern cottonwood", "Peuplier delto\u00EFde", SpeciesLocale.Quebec),
		Pinus_strobus_QC(SpeciesType.ConiferousSpecies, 0.364, 0.16, "White pine", "Pin blanc", SpeciesLocale.Quebec),
		Pinus_banksiana_QC(SpeciesType.ConiferousSpecies, 0.421, 0.14, "Jack pine", "Pin gris", SpeciesLocale.Quebec),
		Pinus_resinosa_QC(SpeciesType.ConiferousSpecies, 0.392, 0.16, "Red pine", "Pin rouge", SpeciesLocale.Quebec),
		Tsuga_canadensis_QC(SpeciesType.ConiferousSpecies, 0.404, 0.17, "Eastern hemlock", "Pruche de l'Est", SpeciesLocale.Quebec),
		Abies_balsamea_QC(SpeciesType.ConiferousSpecies, 0.335, 0.12, "Balsam fir", "Sapin baumier", SpeciesLocale.Quebec),
		Thuja_occidentalis_QC(SpeciesType.ConiferousSpecies, 0.299, 0.14, "Eastern white ceder", "Thuya occidental", SpeciesLocale.Quebec),
		Tilia_americana_QC(SpeciesType.BroadleavedSpecies, 0.36, 0.105, "Basswood", "Tilleul d'Am\u00E9rique", SpeciesLocale.Quebec),
		
		
		Populus_tremula_FR(SpeciesType.BroadleavedSpecies, 0.475, 0.145, "European aspen", "Tremble", SpeciesLocale.France),
		Pinus_nigra_FR(SpeciesType. ConiferousSpecies, 0.510, 0.200, "Black pine", "Pin noir", SpeciesLocale.France),
		Other_broadleaved_FR(SpeciesType.BroadleavedSpecies, 0.601, 0.15, "Other broadleaved species", "Autre feuillu", SpeciesLocale.France),
		Other_coniferous_FR(SpeciesType.ConiferousSpecies, 0.443, 0.15, "Other coniferous species", "Autre conif\u00E8re", SpeciesLocale.France)
		;

		static Map<SpeciesLocale, List<Species>> SPECIES_BY_LOCALE_MAP;

		final SpeciesType speciesType;
		final double basicWoodDensity;
		final double barkProportionOfWoodVolume;
		final List<SpeciesLocale> localeList;
		
		Species(SpeciesType speciesType, 
				double basicWoodDensity, 
				double barkProportionOfWoodVolume, 
				String englishName,
				String frenchName,
				SpeciesLocale... locales) {
			this.speciesType = speciesType;
			this.basicWoodDensity = basicWoodDensity;
			this.barkProportionOfWoodVolume = barkProportionOfWoodVolume;
			setText(englishName, frenchName);
			localeList = Arrays.asList(locales);
		};

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		public String getLatinName() {
			String latinName = name().indexOf("_") == name().lastIndexOf("_") ?
					name() :
						name().substring(0, name().lastIndexOf("_"));
			return latinName.replace("_", " ");
		}
		
		@Override
		public String toString() {return REpiceaTranslator.getString(this);}

		@Override
		public double getBasicWoodDensity() {return basicWoodDensity;}


		/**
		 * Provide the list of species for a particular species locale
		 * @param thisLocale a SpeciesLocale enum
		 * @return a list of Species enum
		 */
		public static List<Species> getSpeciesForThisLocale(SpeciesLocale thisLocale) {
			if (SPECIES_BY_LOCALE_MAP == null) {
				SPECIES_BY_LOCALE_MAP = new HashMap<SpeciesLocale, List<Species>>();
				for (SpeciesLocale locale : SpeciesLocale.values()) {
					SPECIES_BY_LOCALE_MAP.put(locale, new ArrayList<Species>());	
				}
				for (Species s : Species.values()) {
					for (SpeciesLocale l : s.localeList) {
						SPECIES_BY_LOCALE_MAP.get(l).add(s);
					}
				}
			}
			return SPECIES_BY_LOCALE_MAP.get(thisLocale);
		}
		
		@Override
		public double getBarkProportionOfWoodVolume() {return barkProportionOfWoodVolume;}		
		
		@Override
		public SpeciesType getSpeciesType() {return speciesType;}
		
		
	}
	
}
