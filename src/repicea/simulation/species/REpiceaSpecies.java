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

import repicea.serial.SerializerChangeMonitor;
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
 * For Quebec SpeciesLocale, the proportion of bark volume was taken from 
 * Miles, P.D. and W.B. Smith. 2009. Specific gravity and other properties of wood
 * and bark for 156 tree species found in North America. USDA Forest Service,
 * Northern Research Station. Research Note NRS-38.
 * 
 * @author Mathieu Fortin - June 2025
 */
public interface REpiceaSpecies extends TextableEnum, SpeciesTypeProvider, BarkProportionProvider, BasicWoodDensityProvider {

	static class Initializer {
		static {
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Abies_alba_FR", "Abies_alba");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Acer_spp_FR", "Acer_spp");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Alnus_spp_FR", "Alnus_spp");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Betula_spp_FR", "Betula_spp");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Carpinus_betulus_FR","Carpinus_betulus");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Castanea_sativa_FR","Castanea_sativa");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Fagus_sylvatica_FR","Fagus_sylvatica");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Fraxinus_spp_FR","Fraxinus_spp");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Larix_decidua_FR","Larix_decidua");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Picea_abies_FR","Picea_abies");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_pinaster_FR","Pinus_pinaster");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Picea_sitchensis_FR","Picea_sitchensis");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_halepensis_FR","Pinus_halepensis");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_laricio_FR","Pinus_laricio");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_mugo_FR","Pinus_mugo");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_nigra_FR","Pinus_nigra");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_sylvestris_FR","Pinus_sylvestris");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Populus_tremula_FR","Populus_tremula");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Prunus_spp_FR","Prunus_spp");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pseudotsuga_menziesii_FR","Pseudotsuga_menziesii");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_ilex_FR","Quercus_ilex");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_petraea_FR","Quercus_petraea");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_pubescens_FR","Quercus_pubescens");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_robur_FR","Quercus_robur");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Robinia_pseudoacacia_FR","Robinia_pseudoacacia");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Tilia_spp_FR","Tilia_spp");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Other_broadleaved_FR","Other_broadleaved");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Other_coniferous_FR","Other_coniferous");
			
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Abies_balsamea_QC","Abies_balsamea");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Acer_rubrum_QC","Acer_rubrum");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Acer_saccharum_QC","Acer_saccharum"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Acer_saccharinum_QC","Acer_saccharinum");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Betula_alleghaniensis_QC","Betula_alleghaniensis");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Betula_papyrifera_QC","Betula_papyrifera");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Carya_cordiformis_QC","Carya_cordiformis"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Carya_ovata_QC","Carya_ovata");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Fagus_grandifolia_QC","Fagus_grandifolia");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Fraxinus_americana_QC","Fraxinus_americana");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Fraxinus_pensylvanica_QC","Fraxinus_pensylvanica");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Fraxinus_nigra_QC","Fraxinus_nigra"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Juglans_cinerea_QC","Juglans_cinerea");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Juglans_nigra_QC","Juglans_nigra");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Larix_laricina_QC","Larix_laricina");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Ostrya_virgiana_QC","Ostrya_virginiana");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Picea_glauca_QC","Picea_glauca");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Picea_mariana_QC","Picea_mariana"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Picea_rubens_QC","Picea_rubens");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_banksiana_QC","Pinus_banksiana");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_resinosa_QC","Pinus_resinosa");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Pinus_strobus_QC","Pinus_strobus");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Populus_balsamifera_QC","Populus_balsamifera");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Populus_deltoides_QC","Populus_deltoides");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Populus_grandidentata_QC","Populus_grandidentata");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Populus_tremuloides_QC","Populus_tremuloides");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Prunus_serotina_QC","Prunus_serotina"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_alba_QC","Quercus_alba"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_macrocarpa_QC","Quercus_macrocarpa");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Quercus_rubra_QC","Quercus_rubra"); 
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Thuja_occidentalis_QC","Thuja_occidentalis");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Tilia_americana_QC","Tilia_americana");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Tsuga_canadensis_QC","Tsuga_canadensis");
			SerializerChangeMonitor.registerEnumNameChange("repicea.simulation.species.REpiceaSpecies$Species", "Ulmus_americana_QC","Ulmus_americana"); 
		}
	}

	public static enum SpeciesLocale implements TextableEnum {
		/**
		 * IPCC locale.<p>
		 * 
		 * Data come from the 2006 IPCC Guidelines for National Greenhouse Gas Inventories.
		 * Some data come from the 2003 version of the guide.
		 */
		IPCC("IPCC", "GIEC", null),
		/**
		 * North America locale.<p>
		 * 
		 * Data come from Miles, P.D. and W.B. Smith. 2009. 
		 * Specific gravity and other properties of wood
		 * and bark for 156 tree species found in North America. 
		 * USDA Forest Service, Northern Research Station. Research Note NRS-38.
		 */
		NorthAmerica("North America", "Amerique du Nord", IPCC),
		/**
		 * Quebec locale.<p>
		 * 
		 * Basic density data were provided by Hugues Power. There is
		 * no bark proportion and consequently, any call to the 
		 * {@link Species#getBarkProportionOfWoodVolume(SpeciesLocale)} 
		 * method will rely on the next locale, i.e. NorthAmerica. <p>
		 * 
		 * The other coniferous and other broadleaved groups were calculated
		 * as the mean of the species in each species type.
		 */
		Quebec("Quebec", "Qu\u00E9bec", NorthAmerica),
		/**
		 * France locale.<p>
		 * Data provided by French national inventory (contact is
		 * Henri Cuny).
		 */
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
		
		final Double basicWoodDensity;
		final Double barkProportionOfWoodVolume;
		final SpeciesLocale locale;
		
		LocaleEntry(SpeciesLocale locale, Double basicWoodDensity, Double barkProportionOfWoodVolume) {
			this.locale = locale;
			this.basicWoodDensity = basicWoodDensity;
			this.barkProportionOfWoodVolume = barkProportionOfWoodVolume;
		}
	}
	
	/**
	 * An enum variable for the difference species.
	 */
	public enum Species implements REpiceaSpecies {
		Abies_spp(SpeciesType.ConiferousSpecies, "Fir", "Sapin", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.40, 0.118)),
		
		Abies_alba(SpeciesType.ConiferousSpecies, "Silver fir", "Sapin pectin\u00E9", 
				new LocaleEntry(SpeciesLocale.France, 0.421, 0.11)),
		
		Abies_balsamea(SpeciesType.ConiferousSpecies, "Balsam fir", "Sapin baumier", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.33, 0.12),
				new LocaleEntry(SpeciesLocale.Quebec, 0.335, null)),

		Abies_lasiocarpa(SpeciesType.ConiferousSpecies, "Subalpine fir", "Sapin subalpin", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.310, 0.108)),

		Acer_spp(SpeciesType.BroadleavedSpecies, "Maple", "Erable", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.52, 0.109),
				new LocaleEntry(SpeciesLocale.France, 0.582, 0.15)),

		Acer_rubrum(SpeciesType.BroadleavedSpecies, "Red maple", "Erable rouge",
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.490, 0.086),
				new LocaleEntry(SpeciesLocale.Quebec, 0.516, null)),
		
		Acer_saccharum(SpeciesType.BroadleavedSpecies, "Sugar maple", "Erable \u00E0 sucre", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.560, 0.156),
				new LocaleEntry(SpeciesLocale.Quebec, 0.597, null)),
		
		Acer_saccharinum(SpeciesType.BroadleavedSpecies, "Silver maple", "Erable argent\u00E9", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.440, 0.086),
				new LocaleEntry(SpeciesLocale.Quebec, 0.461, null)),

		Alnus_spp(SpeciesType.BroadleavedSpecies, "Alder", "Aulne", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.45, 0.115),
				new LocaleEntry(SpeciesLocale.France, 0.457, 0.15)),
		
		Betula_spp(SpeciesType.BroadleavedSpecies, "Birch", "Bouleau", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.51, 0.110),
				new LocaleEntry(SpeciesLocale.France, 0.541, 0.145)),
		
		Betula_alleghaniensis(SpeciesType.BroadleavedSpecies, "Yellow birch", "Bouleau jaune", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.550, 0.098),
				new LocaleEntry(SpeciesLocale.Quebec, 0.559, null)),
		
		Betula_papyrifera(SpeciesType.BroadleavedSpecies, "Paper birch", "Bouleau \u00E0 papier", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.480, 0.126),
				new LocaleEntry(SpeciesLocale.Quebec, 0.506, null)),
		
		Betula_populifolia(SpeciesType.BroadleavedSpecies, "Gray birch", "Bouleau gris", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.450, 0.126)),  

		Carpinus_betulus(SpeciesType.BroadleavedSpecies, "Hornbeam", "Charme", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.63, 0.086), // this one is from IPCC guidelines 2003
				new LocaleEntry(SpeciesLocale.France, 0.620, 0.05)),

		Carya_cordiformis(SpeciesType.BroadleavedSpecies, "Bitternut hickory", "Caryer cordiforme", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.600, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.628, null)),
		
		Carya_ovata(SpeciesType.BroadleavedSpecies, "Shagbark hickory", "Caryer ovale", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.640, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.654, null)),

		Castanea_sativa(SpeciesType.BroadleavedSpecies, "Chestnut", "Chataignier", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.48, 0.150),	// this one is from IPCC guidelines 2003
				new LocaleEntry(SpeciesLocale.France, 0.505, 0.13)),
		
		Fagus_sylvatica(SpeciesType.BroadleavedSpecies, 
				"European beech", "H\u00EAtre europ\u00E9en", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.58, 0.060),
				new LocaleEntry(SpeciesLocale.France, 0.604, 0.055)),
		
		Fagus_grandifolia(SpeciesType.BroadleavedSpecies, "American beech", "H\u00EAtre \u00E0 grande feuilles", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.560, 0.06),
				new LocaleEntry(SpeciesLocale.Quebec, 0.590, null)),

		Fraxinus_spp(SpeciesType.BroadleavedSpecies, "Ash", "Fr\u00EAne", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.57, 0.160),
				new LocaleEntry(SpeciesLocale.France, 0.596, 0.115)),
		
		Fraxinus_americana(SpeciesType.BroadleavedSpecies, "White ash", "Fr\u00EAne d'Am\u00E9rique", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.550, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.570, null)),
		
		Fraxinus_pensylvanica(SpeciesType.BroadleavedSpecies, "Red ash", "Fr\u00EAne de Pensylvanie", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.530, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.486, null)),
		
		Fraxinus_nigra(SpeciesType.BroadleavedSpecies, "Black ash", "Fr\u00EAne noir", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.450, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.468, null)),

		Juglans_spp(SpeciesType.BroadleavedSpecies, "Wallnut", "Noyer", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.53, 0.150)),	// this one is from IPCC guidelines 2003
		
		Juglans_cinerea(SpeciesType.BroadleavedSpecies, "Butternut", "Noyer cendr\u00E9", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.360, 0.15),
				new LocaleEntry(SpeciesLocale.Quebec, 0.368, null)),
		
		Juglans_nigra(SpeciesType.BroadleavedSpecies, "Black walnut", "Noyer noir", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.510, 0.15),
				new LocaleEntry(SpeciesLocale.Quebec, 0.546, null)),

		Juniperus_virginiana(SpeciesType.ConiferousSpecies, "Eastern red cedar", "Gen\u00E9vrier de Virginie",
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.440, 0.12)),
		
		Larix_decidua(SpeciesType.ConiferousSpecies, "European larch", "M\u00E9l\u00E8ze d'Europe", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.46, 0.140),
				new LocaleEntry(SpeciesLocale.France, 0.497, 0.145)),
		
		Larix_laricina(SpeciesType.ConiferousSpecies, "Tamarack larch", "M\u00E9l\u00E8ze laricin", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.490, 0.14),
				new LocaleEntry(SpeciesLocale.Quebec, 0.485, null)),

		Ostrya_virginiana(SpeciesType.BroadleavedSpecies, "Ironwood", "Ostryer de Virginie", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.630, 0.15),
				new LocaleEntry(SpeciesLocale.Quebec, 0.652, null)),

		Picea_abies(SpeciesType.ConiferousSpecies, "Norway spruce", "Epinette de Norv\u00E0ge", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.40, 0.126),
				new LocaleEntry(SpeciesLocale.France, 0.394, 0.11)),
		
		Picea_glauca(SpeciesType.ConiferousSpecies, "White spruce", "Epinette blanche", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.370, 0.13),
				new LocaleEntry(SpeciesLocale.Quebec, 0.354, null)),
		
		Picea_mariana(SpeciesType.ConiferousSpecies, "Black spruce", "Epinette noire", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.380, 0.13),
				new LocaleEntry(SpeciesLocale.Quebec, 0.406, null)),
		
		Picea_rubens(SpeciesType.ConiferousSpecies, "Red spruce", "Epinette rouge", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.370, 0.13),
				new LocaleEntry(SpeciesLocale.Quebec, 0.38, null)),
		
		Picea_sitchensis(SpeciesType.ConiferousSpecies, "Sitka spruce", "Epic\u00E9a de Sitka", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.40, 0.126),
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.330, 0.125),
				new LocaleEntry(SpeciesLocale.France, 0.401, 0.11)),

		Pinus_banksiana(SpeciesType.ConiferousSpecies, "Jack pine", "Pin gris", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.400, 0.14),
				new	LocaleEntry(SpeciesLocale.Quebec, 0.421, null)),

		Pinus_contorta(SpeciesType.ConiferousSpecies, "Lodgepole pine", "Pin tordu",
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.380, 0.089)),
		
		Pinus_halepensis(SpeciesType.ConiferousSpecies, "Aleppo pine", "Pin d'Alep", 
				new LocaleEntry(SpeciesLocale.France, 0.539, 0.20)),
		
		Pinus_laricio(SpeciesType.ConiferousSpecies, "Laricio pine", "Pin laricio", 
				new LocaleEntry(SpeciesLocale.France, 0.499, 0.20)),
		
		Pinus_mugo(SpeciesType.ConiferousSpecies, "Mountain pine", "Pin mugo", 
				new LocaleEntry(SpeciesLocale.France, 0.425, 0.20)),
		
		Pinus_nigra(SpeciesType.ConiferousSpecies, "Black pine", "Pin noir", 
				new LocaleEntry(SpeciesLocale.France, 0.523, 0.20)),
		
		Pinus_pinaster(SpeciesType.ConiferousSpecies, "Maritime pine", "Pin maritime", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.44, 0.160),
				new LocaleEntry(SpeciesLocale.France, 0.446, 0.25)),
		
		Pinus_radiata(SpeciesType.ConiferousSpecies, "Monterey pine", "Pin de Monterey", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.380, 0.134),
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.400, 0.134)),
		
		Pinus_resinosa(SpeciesType.ConiferousSpecies, "Red pine", "Pin rouge", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.410, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.392, null)),
		
		Pinus_strobus(SpeciesType.ConiferousSpecies, "White pine", "Pin blanc",
				new LocaleEntry(SpeciesLocale.IPCC, 0.32, 0.160),
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.340, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.364, null)),
		
		Pinus_sylvestris(SpeciesType.ConiferousSpecies, 
				"Scots pine", "Pin sylvestre", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.42, 0.160),
				new LocaleEntry(SpeciesLocale.France, 0.458, 0.16)),

		Populus_spp(SpeciesType.BroadleavedSpecies, "Poplar", "Peuplier", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.35, 0.184)),
		
		Populus_balsamifera(SpeciesType.BroadleavedSpecies, "Balsam poplar", "Peuplier baumier", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.310, 0.163),
				new LocaleEntry(SpeciesLocale.Quebec, 0.372, null)),
		
		Populus_deltoides(SpeciesType.BroadleavedSpecies, "Eastern cottonwood", "Peuplier delto\u00EFde", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.370, 0.22),
				new LocaleEntry(SpeciesLocale.Quebec, 0.352, null)),
		
		Populus_grandidentata(SpeciesType.BroadleavedSpecies, "Large-tooth aspen", "Peuplier \u00E0 grandes dents", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.360, 0.144),
				new LocaleEntry(SpeciesLocale.Quebec, 0.390, null)),
		
		Populus_tremula(SpeciesType.BroadleavedSpecies, "European aspen", "Tremble", 
				new LocaleEntry(SpeciesLocale.France, 0.475, 0.15)),
		
		Populus_tremuloides(SpeciesType.BroadleavedSpecies, "Trembling aspen", "Peuplier faux tremble", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.350, 0.144),
				new LocaleEntry(SpeciesLocale.Quebec, 0.374, null)),

		Prunus_spp(SpeciesType.BroadleavedSpecies, "Cherry tree", "Cerisier ou Merisier", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.49, 0.092),
				new LocaleEntry(SpeciesLocale.France, 0.532, 0.15)),
		
		Prunus_serotina(SpeciesType.BroadleavedSpecies, "Black cherry", "Cerisier tardif", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.470, 0.092),
				new LocaleEntry(SpeciesLocale.Quebec, 0.51, null)),

		Pseudotsuga_menziesii(SpeciesType.ConiferousSpecies, "Douglas fir", "Sapin Douglas", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.45, 0.173),
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.450, 0.173),
				new LocaleEntry(SpeciesLocale.France, 0.462, 0.145)),

		Quercus_spp(SpeciesType.BroadleavedSpecies, "Oak", "Ch\u00EAne", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.58, 0.191)),
		
		Quercus_alba(SpeciesType.BroadleavedSpecies, "White oak", "Ch\u00EAne blanc", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.600, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.654, null)),
		
		Quercus_ilex(SpeciesType.BroadleavedSpecies, "Holm oak", "Ch\u00EAne vert", 
				new LocaleEntry(SpeciesLocale.France, 0.704, 0.15)),

		Quercus_macrocarpa(SpeciesType.BroadleavedSpecies, "Bur oak", "Ch\u00EAne \u00E0 gros fruits", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.580, 0.16),
				new LocaleEntry(SpeciesLocale.Quebec, 0.599, null)),

		Quercus_petraea(SpeciesType.BroadleavedSpecies, "Sessile oak", "Ch\u00EAne rouvre", 
				new LocaleEntry(SpeciesLocale.France, 0.643, 0.15)),
		
		Quercus_pubescens(SpeciesType.BroadleavedSpecies, "Pubescent oak", "Ch\u00EAne pubescent", 
				new LocaleEntry(SpeciesLocale.France, 0.475, 0.15)),
		
		Quercus_robur(SpeciesType.BroadleavedSpecies, "Pedunculate oak", "Ch\u00EAne p\u00E9doncul\u00E9", 
				new LocaleEntry(SpeciesLocale.France, 0.622, 0.15)),
		
		Quercus_rubra(SpeciesType.BroadleavedSpecies, "Red oak", "Ch\u00EAne rouge", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.560, 0.20),
				new LocaleEntry(SpeciesLocale.Quebec, 0.581, null)),

		Robinia_pseudoacacia(SpeciesType.BroadleavedSpecies, "Black locust", "Robinier", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.660, 0.15),
				new LocaleEntry(SpeciesLocale.France, 0.642, 0.15)),

		Salix_spp(SpeciesType.BroadleavedSpecies, "Willow", "Saule", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.45, 0.160)),
		
		Thuja_occidentalis(SpeciesType.ConiferousSpecies, "Eastern white ceder", "Thuya occidental", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.290, 0.14),
				new LocaleEntry(SpeciesLocale.Quebec, 0.299, null)),
		
		Thuja_plicata(SpeciesType.ConiferousSpecies, "Red cedar", "Thuya g\u00E9ant", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.310, 0.106), // this one is from IPCC guidelines 2003
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.310, 0.106)),

		Tilia_spp(SpeciesType.BroadleavedSpecies, 
				"Lime tree", "Tilleul", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.43, 0.105),
				new LocaleEntry(SpeciesLocale.France, 0.462, 0.15)),
		
		Tilia_americana(SpeciesType.BroadleavedSpecies, "American basswood", "Tilleul d'Am\u00E9rique", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.320, 0.105),
				new LocaleEntry(SpeciesLocale.Quebec, 0.360, null)),
		
		Tsuga_spp(SpeciesType.ConiferousSpecies, "Hemlock", "Pruche", 
				new LocaleEntry(SpeciesLocale.IPCC, 0.42, 0.162)), // this one is from IPCC guidelines 2003
		
		Tsuga_canadensis(SpeciesType.ConiferousSpecies, "Eastern hemlock", "Pruche de l'Est", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.380, 0.17),
				new LocaleEntry(SpeciesLocale.Quebec, 0.404, null)),

		Ulmus_americana(SpeciesType.BroadleavedSpecies, "American elm", "Orme d'Am\u00E9rique", 
				new LocaleEntry(SpeciesLocale.NorthAmerica, 0.460, 0.14),
				new LocaleEntry(SpeciesLocale.Quebec, 0.524, null)),
		
		Other_broadleaved(SpeciesType.BroadleavedSpecies, "Other broadleaved", "Autre feuillu", 
				new LocaleEntry(SpeciesLocale.France, 0.592, 0.15),
				new LocaleEntry(SpeciesLocale.Quebec, 0.513, 0.141)),
		
		Other_coniferous(SpeciesType.ConiferousSpecies, "Other conifer", "Autre conif\u00E8re", 
				new LocaleEntry(SpeciesLocale.France, 0.507, 0.15),
				new LocaleEntry(SpeciesLocale.Quebec, 0.384, 0.142))
		;

		final static Initializer SingletonInitializer = new Initializer();

		static Map<SpeciesLocale, List<Species>> SPECIES_BY_LOCALE_MAP;

		final SpeciesType speciesType;
//		final double basicWoodDensity;
//		final double barkProportionOfWoodVolume;
//		final List<SpeciesLocale> localeList;
		
		private final Map<SpeciesLocale, Double> localeBarkProportion;
		private final Map<SpeciesLocale, Double> localeBasicWoodDensity;
		private final List<SpeciesLocale> localeList;
		
		Species(SpeciesType speciesType, 
				String englishName,
				String frenchName,
				LocaleEntry... localeEntries) {
			this.speciesType = speciesType;
			localeBarkProportion = new HashMap<SpeciesLocale, Double>();
			localeBasicWoodDensity = new HashMap<SpeciesLocale, Double>();
			localeList = new ArrayList<SpeciesLocale>();
			for (LocaleEntry le : localeEntries) {
				if (le.barkProportionOfWoodVolume != null) {
					localeBarkProportion.put(le.locale, le.barkProportionOfWoodVolume);
				}
				if (le.basicWoodDensity != null) {
					localeBasicWoodDensity.put(le.locale, le.basicWoodDensity);
				}
				if (le.barkProportionOfWoodVolume != null || le.basicWoodDensity != null) {
					if (!localeList.contains(le.locale)) {
						localeList.add(le.locale);
					}
				}
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
			return REpiceaTranslator.getString(this) + " " + localeList.toString();
		}

		@Override
		public double getBasicWoodDensity(SpeciesLocale locale) {
			return Species.findLocaleEntryInMap(locale, localeBasicWoodDensity);
		}

		
		private static double findLocaleEntryInMap(SpeciesLocale locale, Map<SpeciesLocale, Double> oMap) {
			SpeciesLocale currentLocale = locale;
			while (currentLocale != null) {
				if (oMap.containsKey(currentLocale)) {
					return oMap.get(currentLocale);
				} else {
					currentLocale = currentLocale.nextLevel;
				}
			}
			throw new UnsupportedOperationException("Cannot find any entry with this locale: " + locale.name());
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
					for (SpeciesLocale l : s.localeList) {
						SPECIES_BY_LOCALE_MAP.get(l).add(s);
					}
				}
			}
			return SPECIES_BY_LOCALE_MAP.get(thisLocale);
		}
		
		@Override
		public double getBarkProportionOfWoodVolume(SpeciesLocale locale) {
			return Species.findLocaleEntryInMap(locale, localeBarkProportion);
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
