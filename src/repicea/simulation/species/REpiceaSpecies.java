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
 * 
 * The proportion of bark volume was taken from 
 * Miles, P.D. and W.B. Smith. 2009. Specific gravity and other properties of wood
 * and bark for 156 tree species found in North America. USDA Forest Service,
 * Northern Research Station. Research Note NRS-38.
 * 
 * @author Mathieu Fortin - June 2025
 */
public interface REpiceaSpecies extends TextableEnum, SpeciesTypeProvider, BarkProportionProvider, BasicWoodDensityProvider {

	public static enum SpeciesLocale implements TextableEnum {
		IPCC("IPCC", "GIEC", null),
		Quebec("Quebec", "Qu\u00E9bec", IPCC),
		France("France", "France", IPCC);

		final SpeciesLocale nextLevel;
		
		SpeciesLocale(String englishText, String frenchText, SpeciesLocale nextLevel) {
			this.nextLevel = nextLevel;
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String toString() {
			return REpiceaTranslator.getString(this);
		}

		/**
		 * Return a larger scale species locale.
		 * @return a SpeciesLocale enum or null if this locale has no next level.
		 */
		public SpeciesLocale getNextLevel() {
			return nextLevel;
		}
	}
	
	
	static class LocaleEntry {
		
		final double basicWoodDensity;
		final double barkProportionOfWoodVolume;
		final SpeciesLocale locale;
		
		LocaleEntry(SpeciesLocale locale, double basicWoodDensity, double barkProportionOfWoodVolume) {
			this.locale = locale;
			this.basicWoodDensity = basicWoodDensity;
			this.barkProportionOfWoodVolume = barkProportionOfWoodVolume;
		}
	}
	
	/**
	 * An enum variable for the difference species.
	 */
	public static enum Species implements REpiceaSpecies {
		Abies_spp(SpeciesType.ConiferousSpecies, "Fir", "Sapin", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.40, 0.118)),
		Abies_alba(SpeciesType.ConiferousSpecies, "Silver fir", "Sapin pectin\u00E9", 
				new LocaleEntry(SpeciesLocale.France, 0.421, 0.11)),
		Abies_balsamea(SpeciesType.ConiferousSpecies, "Balsam fir", "Sapin baumier", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.335, 0.12)),

		Acer_spp(SpeciesType.BroadleavedSpecies, "Maple", "Erable", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.52, 0.109),
				new LocaleEntry(SpeciesLocale.France, 0.582, 0.15)),
		Acer_rubrum(SpeciesType.BroadleavedSpecies, "Red maple", "Erable rouge", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.516, 0.086)),
		Acer_saccharum(SpeciesType.BroadleavedSpecies, "Sugar maple", "Erable \u00E0 sucre", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.597, 0.156)),
		Acer_saccharinum(SpeciesType.BroadleavedSpecies, "Silver maple", "Erable argent\u00E9", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.461, 0.086)),

		Alnus_spp(SpeciesType.BroadleavedSpecies, "Alder", "Aulne", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.45, 0.115),
				new LocaleEntry(SpeciesLocale.France, 0.457, 0.15)),
		
		Betula_spp(SpeciesType.BroadleavedSpecies, "Birch", "Bouleau", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.51, 0.110),
				new LocaleEntry(SpeciesLocale.France, 0.541, 0.145)),
		Betula_alleghaniensis(SpeciesType.BroadleavedSpecies, "Yellow birch", "Bouleau jaune", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.559, 0.098)),
		Betula_papyrifera(SpeciesType.BroadleavedSpecies, "Paper birch", "Bouleau \u00E0 papier", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.506, 0.126)),
		Betula_populifolia(SpeciesType.BroadleavedSpecies, "Gray birch", "Bouleau gris", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.506, 0.126)),  // same specs as betula papyrifera

		Carpinus_betulus(SpeciesType.BroadleavedSpecies, "Hornbeam", "Charme", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.63, 0.086), // this one is from IPCC guidelines 2003
				new LocaleEntry(SpeciesLocale.France, 0.620, 0.05)),

		Carya_cordiformis(SpeciesType.BroadleavedSpecies, "Bitternut hickory", "Caryer cordiforme", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.628, 0.16)),
		Carya_ovata(SpeciesType.BroadleavedSpecies, "Shagbark hickory", "Caryer ovale", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.654, 0.16)),

		Castanea_sativa(SpeciesType.BroadleavedSpecies, "Chestnut", "Chataignier", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.48, 0.150),	// this one is from IPCC guidelines 2003
				new LocaleEntry(SpeciesLocale.France, 0.505, 0.13)),
		
		Fagus_sylvatica(SpeciesType.BroadleavedSpecies, 
				"European beech", "H\u00EAtre europ\u00E9en", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.58, 0.060),
				new LocaleEntry(SpeciesLocale.France, 0.604, 0.055)),
		Fagus_grandifolia(SpeciesType.BroadleavedSpecies, "American beech", "H\u00EAtre \u00E0 grande feuilles", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.59, 0.06)),

		Fraxinus_spp(SpeciesType.BroadleavedSpecies, "Ash", "Fr\u00EAne", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.57, 0.160),
				new LocaleEntry(SpeciesLocale.France, 0.596, 0.115)),
		Fraxinus_americana(SpeciesType.BroadleavedSpecies, "White ash", "Fr\u00EAne d'Am\u00E9rique", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.57, 0.16)),
		Fraxinus_pensylvanica(SpeciesType.BroadleavedSpecies, "Red ash", "Fr\u00EAne de Pensylvanie", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.486, 0.16)),
		Fraxinus_nigra(SpeciesType.BroadleavedSpecies, "Black ash", "Fr\u00EAne noir", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.468, 0.16)),

		Juglans_spp(SpeciesType.BroadleavedSpecies, "Wallnut", "Noyer", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.53, 0.150)),	// this one is from IPCC guidelines 2003
		Juglans_cinerea(SpeciesType.BroadleavedSpecies, "Butternut", "Noyer cendr\u00E9", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.368, 0.15)),
		Juglans_nigra(SpeciesType.BroadleavedSpecies, "Black walnut", "Noyer noir", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.546, 0.15)),

		Larix_decidua(SpeciesType.ConiferousSpecies, "European larch", "M\u00E9l\u00E8ze d'Europe", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.46, 0.140),
				new LocaleEntry(SpeciesLocale.France, 0.497, 0.145)),
		Larix_laricina(SpeciesType.ConiferousSpecies, "Tamarack larch", "M\u00E9l\u00E8ze laricin", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.485, 0.14)),

		Ostrya_virginiana(SpeciesType.BroadleavedSpecies, "Ironwood", "Ostryer de Virginie", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.652, 0.15)),

		Picea_abies(SpeciesType.ConiferousSpecies, "Norway spruce", "Epinette de Norv\u00E0ge", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.40, 0.126),
				new LocaleEntry(SpeciesLocale.France, 0.394, 0.11)),
		Picea_glauca(SpeciesType.ConiferousSpecies, "White spruce", "Epinette blanche", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.354, 0.13)),
		Picea_mariana(SpeciesType.ConiferousSpecies, "Black spruce", "Epinette noire", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.406, 0.13)),
		Pinus_pinaster(SpeciesType.ConiferousSpecies, "Maritime pine", "Pin maritime", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.44, 0.160),
				new LocaleEntry(SpeciesLocale.France, 0.446, 0.25)),
		Picea_rubens(SpeciesType.ConiferousSpecies, "Red spruce", "Epinette rouge", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.38, 0.13)),
		Picea_sitchensis(SpeciesType.ConiferousSpecies, "Sitka spruce", "Epic\u00E9a de Sitka", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.40, 0.126),
				new LocaleEntry(SpeciesLocale.France, 0.401, 0.11)),

		Pinus_banksiana(SpeciesType.ConiferousSpecies, "Jack pine", "Pin gris", new 
				LocaleEntry(SpeciesLocale.Quebec, 0.421, 0.14)),
		Pinus_halepensis(SpeciesType.ConiferousSpecies, "Aleppo pine", "Pin d'Alep", 
				new LocaleEntry(SpeciesLocale.France, 0.539, 0.20)),
		Pinus_laricio(SpeciesType.ConiferousSpecies, "Laricio pine", "Pin laricio", 
				new LocaleEntry(SpeciesLocale.France, 0.499, 0.20)),
		Pinus_mugo(SpeciesType.ConiferousSpecies, "Mountain pine", "Pin mugo", 
				new LocaleEntry(SpeciesLocale.France, 0.425, 0.20)),
		Pinus_nigra(SpeciesType.ConiferousSpecies, "Black pine", "Pin noir", 
				new LocaleEntry(SpeciesLocale.France, 0.523, 0.20)),
		Pinus_radiata(SpeciesType.ConiferousSpecies, "Monterey pine", "Pin de Monterey", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.38, 0.134)),
		Pinus_resinosa(SpeciesType.ConiferousSpecies, "Red pine", "Pin rouge", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.392, 0.16)),
		Pinus_strobus(SpeciesType.ConiferousSpecies, "White pine", "Pin blanc",
				new LocaleEntry(SpeciesLocale.IPCC, 0.32, 0.160),
				new LocaleEntry(SpeciesLocale.Quebec, 0.364, 0.16)),
		Pinus_sylvestris(SpeciesType.ConiferousSpecies, 
				"Scots pine", "Pin sylvestre", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.42, 0.160),
				new LocaleEntry(SpeciesLocale.France, 0.458, 0.16)),

		Populus_spp(SpeciesType.BroadleavedSpecies, "Poplar", "Peuplier", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.35, 0.184)),
		Populus_balsamifera(SpeciesType.BroadleavedSpecies, "Black cottonwood", "Peuplier baumier", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.372, 0.163)),
		Populus_deltoides(SpeciesType.BroadleavedSpecies, "Eastern cottonwood", "Peuplier delto\u00EFde", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.352, 0.22)),
		Populus_grandidentata(SpeciesType.BroadleavedSpecies, "Large-tooth aspen", "Peuplier \u00E0 grandes dents", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.39, 0.144)),
		Populus_tremula(SpeciesType.BroadleavedSpecies, "European aspen", "Tremble", 
				new LocaleEntry(SpeciesLocale.France, 0.475, 0.15)),
		Populus_tremuloides(SpeciesType.BroadleavedSpecies, "Trembling aspen", "Peuplier faux tremble", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.374, 0.144)),

		Prunus_spp(SpeciesType.ConiferousSpecies, "Cherry tree", "Cerisier ou Merisier", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.49, 0.092),
				new LocaleEntry(SpeciesLocale.France, 0.532, 0.15)),
		Prunus_serotina(SpeciesType.BroadleavedSpecies, "Black cherry", "Cerisier tardif", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.51, 0.092)),

		Pseudotsuga_menziesii(SpeciesType.ConiferousSpecies, "Douglas fir", "Sapin Douglas", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.45, 0.173),
				new LocaleEntry(SpeciesLocale.France, 0.462, 0.145)),

		Quercus_spp(SpeciesType.BroadleavedSpecies, "Oak", "Ch\u00EAne", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.58, 0.191)),
		Quercus_alba(SpeciesType.BroadleavedSpecies, "White oak", "Ch\u00EAne blanc", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.654, 0.16)),
		Quercus_ilex(SpeciesType.BroadleavedSpecies, "Holm oak", "Ch\u00EAne vert", 
				new LocaleEntry(SpeciesLocale.France, 0.704, 0.15)),
		Quercus_macrocarpa(SpeciesType.BroadleavedSpecies, "Bur oak", "Ch\u00EAne \u00E0 gros fruits", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.599, 0.16)),
		Quercus_petraea(SpeciesType.BroadleavedSpecies, "Sessile oak", "Ch\u00EAne rouvre", 
				new LocaleEntry(SpeciesLocale.France, 0.643, 0.15)),
		Quercus_pubescens(SpeciesType.BroadleavedSpecies, "Pubescent oak", "Ch\u00EAne pubescent", 
				new LocaleEntry(SpeciesLocale.France, 0.475, 0.15)),
		Quercus_robur(SpeciesType.BroadleavedSpecies, "Pedunculate oak", "Ch\u00EAne p\u00E9doncul\u00E9", 
				new LocaleEntry(SpeciesLocale.France, 0.622, 0.15)),
		Quercus_rubra(SpeciesType.BroadleavedSpecies, "Red oak", "Ch\u00EAne rouge", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.581, 0.2)),

		Robinia_pseudoacacia(SpeciesType.BroadleavedSpecies, "Black locust", "Robinier", 
				new LocaleEntry(SpeciesLocale.France, 0.642, 0.15)),

		Salix_spp(SpeciesType.BroadleavedSpecies, "Willow", "Saule", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.45, 0.160)),
		
		Thuja_occidentalis(SpeciesType.ConiferousSpecies, "Eastern white ceder", "Thuya occidental", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.299, 0.14)),
		Thuja_plicata(SpeciesType.ConiferousSpecies, "Red cedar", "Thuya g\u00E9ant", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.31, 0.106)), // this one is from IPCC guidelines 2003

		Tilia_spp(SpeciesType.BroadleavedSpecies, 
				"Lime tree", "Tilleul", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.43, 0.105),
				new LocaleEntry(SpeciesLocale.France, 0.462, 0.15)),
		Tilia_americana(SpeciesType.BroadleavedSpecies, "Basswood", "Tilleul d'Am\u00E9rique", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.36, 0.105)),
		
		Tsuga_spp(SpeciesType.ConiferousSpecies, "Hemlock", "Pruche", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.42, 0.162)), // this one is from IPCC guidelines 2003
		Tsuga_canadensis(SpeciesType.ConiferousSpecies, "Eastern hemlock", "Pruche de l'Est", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.404, 0.17)),

		Ulmus_americana(SpeciesType.BroadleavedSpecies, "American elm", "Orme d'Am\u00E9rique", 
				new LocaleEntry(SpeciesLocale.Quebec, 0.524, 0.14)),
		
		Other_broadleaved(SpeciesType.BroadleavedSpecies, "Other broadleaved", "Autre feuillu", 
				new LocaleEntry(SpeciesLocale.France, 0.592, 0.15)),
		Other_coniferous(SpeciesType.ConiferousSpecies, "Other conifer", "Autre conif\u00E8re", 
				new LocaleEntry(SpeciesLocale.France, 0.507, 0.15))
		;

		static Map<SpeciesLocale, List<Species>> SPECIES_BY_LOCALE_MAP;

		final SpeciesType speciesType;
//		final double basicWoodDensity;
//		final double barkProportionOfWoodVolume;
//		final List<SpeciesLocale> localeList;
		
		private final Map<SpeciesLocale, LocaleEntry> localeMap;
		
		Species(SpeciesType speciesType, 
				String englishName,
				String frenchName,
				LocaleEntry... localeEntries) {
			this.speciesType = speciesType;
			this.localeMap = new HashMap<SpeciesLocale, LocaleEntry>();
			for (LocaleEntry le : localeEntries) {
				localeMap.put(le.locale, le);
			}
//			this.basicWoodDensity = basicWoodDensity;
//			this.barkProportionOfWoodVolume = barkProportionOfWoodVolume;
			setText(englishName, frenchName);
//			localeList = Arrays.asList(locales);
		};

		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String getLatinName() {
			String latinName = name().indexOf("_") == name().lastIndexOf("_") ?
					name() :
						name().substring(0, name().lastIndexOf("_"));
			return latinName.replace("_", " ");
		}
		
		@Override
		public String toString() {
			return REpiceaTranslator.getString(this) + " " + localeMap.keySet().toString();
		}

		@Override
		public double getBasicWoodDensity(SpeciesLocale locale) {
			return Species.findLocaleEntryInMap(locale, localeMap).basicWoodDensity;
		}

		
		private static LocaleEntry findLocaleEntryInMap(SpeciesLocale locale, Map<SpeciesLocale, LocaleEntry> oMap) {
			SpeciesLocale currentLocale = locale;
			while (currentLocale != null) {
				if (oMap.containsKey(locale)) {
					return oMap.get(locale);
				} else {
					currentLocale = locale.nextLevel;
				}
			}
			throw new UnsupportedOperationException("Cannot find any available basic wood density for this locale: " + locale.name());
		}

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
					for (SpeciesLocale l : s.localeMap.keySet()) {
						SPECIES_BY_LOCALE_MAP.get(l).add(s);
					}
				}
			}
			return SPECIES_BY_LOCALE_MAP.get(thisLocale);
		}
		
		@Override
		public double getBarkProportionOfWoodVolume(SpeciesLocale locale) {
			return Species.findLocaleEntryInMap(locale, localeMap).barkProportionOfWoodVolume;
		}		
		
		@Override
		public SpeciesType getSpeciesType() {return speciesType;}
		
	}
	
	/**
	 * Provide the Latin name of the species.
	 * @return a String
	 */
	public String getLatinName();
	
}
