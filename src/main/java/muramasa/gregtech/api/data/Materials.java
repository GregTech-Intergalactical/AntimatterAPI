package muramasa.gregtech.api.data;

import muramasa.gregtech.api.materials.Material;

import java.util.LinkedHashMap;

import static muramasa.gregtech.api.enums.Element.*;
import static muramasa.gregtech.api.enums.GenerationFlag.*;
import static muramasa.gregtech.api.materials.MaterialSet.*;

public class Materials {

    public static LinkedHashMap<String, Material> generatedMap = new LinkedHashMap<>();
    public static Material[] generated = new Material[1000]; //TODO remove Material IDs

    public static Material Aluminium = new Material(0, "Aluminium", 0x80c8f0, DULL, Al).asMetal(933, 1700).addOre().addTools(10.0F, 128, 2).add(RING, FOIL, SGEAR, GEAR, FRAME);
    public static Material Beryllium = new Material(1, "Beryllium", 0x64b464, METALLIC, Be).asMetal(1560).addTools(14.0F, 64, 2).addOre();
    public static Material Bismuth = new Material(2, "Bismuth", 0x64a0a0, METALLIC, Bi).asMetal(544).addTools(6.0F, 64, 1);
    public static Material Carbon = new Material(3, "Carbon", 0x141414, DULL, C).asSolid().addTools(1.0F, 64, 2);
    public static Material Chrome = new Material(4, "Chrome", 0xffe6e6, SHINY, Cr).asMetal(2180, 1700).addOre().addTools(11.0F, 256, 3).add(SCREW, RING, PLATE, ROTOR);
    public static Material Cobalt = new Material(5, "Cobalt", 0x5050fa, METALLIC, Co).asMetal(1768).addTools(8.0F, 512, 3);
    public static Material Gold = new Material(6, "Gold", 0xffff1e, SHINY, Au).asMetal(1337).addOre().addTools(12.0F, 64, 2).add(FOIL, ROD, WIREF, GEAR, BLOCK);
    public static Material Iridium = new Material(7, "Iridium", 0xf0f0f5, DULL, Ir).asMetal(2719, 2719).addOre().add(FRAME).addTools(6.0F, 2560, 3);
    public static Material Iron = new Material(8, "Iron", 0xc8c8c8, METALLIC, Fe).asMetal(1811).asPlasma().addTools(6.0F, 256, 2).add(RING, GEAR, FRAME, BLOCK);
    public static Material Lanthanum = new Material(9, "Lanthanum", 0xffffff, METALLIC, La).asSolid(1193, 1193);
    public static Material Lead = new Material(10, "Lead", 0x8c648c, DULL, Pb).asMetal(600).addOre().addTools(8.0F, 64, 1).add(PLATE, FOIL, ROD, WIREF, DPLATE);
    public static Material Manganese = new Material(11, "Manganese", 0xfafafa, DULL, Mn).asMetal(1519).addOre().addTools(7.0F, 512, 2).add(FOIL);
    public static Material Molybdenum = new Material(12, "Molybdenum", 0xb4b4dc, SHINY, Mo).asMetal(2896).addTools(7.0F, 512, 2).addOre();
    public static Material Neodymium = new Material(13, "Neodymium", 0x646464, METALLIC, Nd).asMetal(1297, 1297).addTools(7.0F, 512, 2).addOre();
    public static Material Neutronium = new Material(14, "Neutronium", 0xfafafa, DULL, Nt).asMetal(10000, 10000).addTools(24.0F, 655360, 6).add(SCREW, RING, GEAR, SGEAR, FRAME);
    public static Material Nickel = new Material(15, "Nickel", 0xc8c8fa, METALLIC, Ni).asMetal(1728).asPlasma().addOre().addTools(6.0F, 64, 2);
    public static Material Osmium = new Material(16, "Osmium", 0x3232ff, METALLIC, Os).asMetal(3306, 3306).addOre().addTools(16.0F, 1280, 4).add(SCREW, RING, PLATE, FOIL, ROD, WIREF);
    public static Material Palladium = new Material(17, "Palladium", 0x808080, SHINY, Pd).asMetal(1828, 1828).addTools(8.0F, 512, 2).addOre();
    public static Material Platinum = new Material(18, "Platinum", 0xffffc8, SHINY, Pt).asMetal(2041).addOre().addTools(12.0F, 64, 2).add(PLATE, FOIL, ROD, WIREF);
    public static Material Plutonium = new Material(19, "Plutonium 239", 0xf03232, METALLIC, Pu).asMetal(912).addTools(6.0F, 512, 3).addOre();
    public static Material Plutonium241 = new Material(20, "Plutonium 241", 0xfa4646, SHINY, Pu241).asMetal(912).addTools(6.0F, 512, 3);
    public static Material Silver = new Material(21, "Silver", 0xdcdcff, SHINY, Ag).asMetal(1234).addTools(10.0F, 64, 2).addOre();
    public static Material Thorium = new Material(22, "Thorium", 0x001e00, SHINY, Th).asMetal(2115).addTools(6.0F, 512, 2).addOre();
    public static Material Titanium = new Material(23, "Titanium", 0xdca0f0, METALLIC).asMetal();
    public static Material Tungsten = new Material(24, "Tungsten", 0x323232, METALLIC, W).asMetal(3695, 3000).addTools(7.0F, 2560, 3).add(FOIL);
    public static Material Uranium = new Material(25, "Uranium 238", 0x32f032, METALLIC, U).asMetal(1405).addTools(6.0F, 512, 3).addOre();
    public static Material Uranium235 = new Material(26, "Uranium 235", 0x46fa46, METALLIC, U235).asMetal(1405).addTools(6.0F, 512, 3).addOre();
    public static Material Graphite = new Material(27, "Graphite", 0x808080, DULL).asDust().addOre().addTools(5.0F, 32, 2);
    public static Material Americium = new Material(28, "Americium", 0xc8c8c8, METALLIC, Am).asMetal(1149, 0).add(PLATE, ROD);
    public static Material Antimony = new Material(29, "Antimony", 0xdcdcf0, SHINY, Sb).asMetal(1449, 0);
    public static Material Argon = new Material(30, "Argon", 0xff00f0, NONE, Ar).asGas();
    public static Material Arsenic = new Material(31, "Arsenic", 0xffffff, DULL, As).asSolid();
    public static Material Barium = new Material(32, "Barium", 0xffffff, METALLIC, Ba).asDust(1000).add(FOIL);
    public static Material Boron = new Material(33, "Boron", 0xfafafa, DULL, B).asDust(2349);
    public static Material Caesium = new Material(34, "Caesium", 0xffffff, METALLIC, Cs).asMetal(2349);
    public static Material Calcium = new Material(35, "Calcium", 0xfff5f5, METALLIC, Ca).asDust(1115);
    public static Material Cadmium = new Material(36, "Cadmium", 0x32323c, SHINY, Cd).asDust(594);
    public static Material Cerium = new Material(37, "Cerium", 0xffffff, METALLIC, Ce).asSolid(1068, 1068);
    public static Material Chlorine = new Material(38, "Chlorine", 0xffffff, NONE, Cr).asGas();
    public static Material Copper = new Material(39, "Copper", 0xff6400, SHINY, Cu).asMetal(1357).addOre().add(PLATE, DPLATE, ROD, FOIL, WIREF, GEAR);
    public static Material Deuterium = new Material(40, "Deuterium", 0xffff00, NONE, D).asFluid();
    public static Material Dysprosium = new Material(41, "Dysprosium", 0xffffff, METALLIC, D).asMetal(1680, 1680);
    public static Material Europium = new Material(42, "Europium", 0xffffff, METALLIC, Eu).asMetal(1099, 1099).add(PLATE, ROD);
    public static Material Fluorine = new Material(43, "Fluorine", 0xffffff, NONE, F).asFluid();
    public static Material Gallium = new Material(44, "Gallium", 0xdcdcff, SHINY, Ga).asMetal(302).add(PLATE);
    public static Material Hydrogen = new Material(45, "Hydrogen", 0x0000ff, NONE, H).asGas();
    public static Material Helium = new Material(46, "Helium", 0xffff00, NONE, He).asPlasma();
    public static Material Helium3 = new Material(47, "Helium-3", 0xffffff, NONE, He_3).asGas();
    public static Material Indium = new Material(48, "Indium", 0x400080, METALLIC, In).asSolid(429);
    public static Material Lithium = new Material(49, "Lithium", 0xe1dcff, DULL, Li).asSolid(454).addOre();
    public static Material Lutetium = new Material(50, "Lutetium", 0xffffff, DULL, Lu).asMetal(1925, 1925);
    public static Material Magnesium = new Material(51, "Magnesium", 0xffc8c8, METALLIC, Mg).asMetal(923);
    public static Material Mercury = new Material(52, "Mercury", 0xffdcdc, SHINY, Hg).asFluid();
    public static Material Niobium = new Material(53, "Niobium", 0xbeb4c8, METALLIC, Nb).asMetal(2750, 2750);
    public static Material Nitrogen = new Material(54, "Nitrogen", 0x0096c8, NONE, N).asPlasma();
    public static Material Oxygen = new Material(55, "Oxygen", 0x0064c8, NONE, O).asPlasma();
    public static Material Phosphor = new Material(56, "Phosphor", 0xffff00, DULL, P).asDust(317);
    public static Material Potassium = new Material(57, "Potassium", 0xfafafa, METALLIC, K).asSolid(336);
    public static Material Radon = new Material(58, "Radon", 0xff00ff, NONE, Rn).asGas();
    public static Material Silicon = new Material(59, "Silicon", 0x3c3c50, METALLIC, Si).asMetal(1687, 1687).add(PLATE, FOIL, BLOCK);
    public static Material Sodium = new Material(60, "Sodium", 0x000096, METALLIC, Na).asDust(370);
    public static Material Sulfur = new Material(61, "Sulfur", 0xc8c800, DULL, S).asDust(388).addOre().asPlasma();
    public static Material Tantalum = new Material(62, "Tantalum", 0xffffff, METALLIC, Ta).asSolid(3290);
    public static Material Tin = new Material(63, "Tin", 0xdcdcdc, DULL, Sn).asMetal(505, 505).addOre().add(PLATE, ROD, BOLT, SCREW, RING, GEAR, FOIL, WIREF, FRAME);
    public static Material Tritium = new Material(64, "Tritium", 0xff0000, METALLIC, T).asFluid();
    public static Material Vanadium = new Material(65, "Vanadium", 0x323232, METALLIC, V).asMetal(2183, 2183);
    public static Material Yttrium = new Material(66, "Yttrium", 0xdcfadc, METALLIC, Y).asMetal(1799, 1799);
    public static Material Zinc = new Material(67, "Zinc", 0xfaf0f0, METALLIC, Zn).asMetal(692).addOre().add(PLATE, FOIL); //TODO ORE flag added due to bee recipes

    /** Gases **/
    public static Material WoodGas = new Material(660, "Wood Gas", 0xdecd87, NONE).asGas(24);
    public static Material Methane = new Material(715, "Methane", 0xffffff, NONE).asGas(104).add(Carbon, 1, Hydrogen, 4);
    public static Material CarbonDioxide = new Material(497, "Carbon Dioxide", 0xa9d0f5, NONE).asGas().add(Carbon, 1, Oxygen, 2);
    public static Material NobleGases = new Material(496, "Noble Gases", 0xc9e3fc, NONE).asGas()/*.setTemp(79, 0)*/.add(CarbonDioxide, 21, Helium, 9, Methane, 3, Deuterium, 1);
    public static Material Air = new Material(494, "Air", 0xc9e3fc, NONE).asGas().add(Nitrogen, 40, Oxygen, 11, Argon, 1, NobleGases, 1);
    public static Material NitrogenDioxide = new Material(717, "Nitrogen Dioxide", 0x64afff, NONE).asGas().add(Nitrogen, 1, Oxygen, 2);
    public static Material NaturalGas = new Material(733, "Natural Gas", 0xffffff, NONE).asGas(15);
    public static Material SulfuricGas = new Material(734, "Sulfuric Gas", 0xffffff, NONE).asGas(20);
    public static Material ReNONEryGas = new Material(735, "ReNONEry Gas", 0xffffff, NONE).asGas(128);
    public static Material LPG = new Material(742, "LPG", 0xffff00, NONE).asGas(256);
    public static Material Ethane = new Material(642, "Ethane", 0xc8c8ff, NONE).asGas(168).add(Carbon, 2, Hydrogen, 6);
    public static Material Propane = new Material(643, "Propane", 0xfae250, NONE).asGas(232).add(Carbon, 2, Hydrogen, 6);
    public static Material Butane = new Material(644, "Butane", 0xb6371e, NONE).asGas(296).add(Carbon, 4, Hydrogen, 10);
    public static Material Butene = new Material(645, "Butene", 0xcf5005, NONE).asGas(256).add(Carbon, 4, Hydrogen, 8);
    public static Material Butadiene = new Material(646, "Butadiene", 0xe86900, NONE).asGas(206).add(Carbon, 4, Hydrogen, 6);
    public static Material VinylChloride = new Material(650, "Vinyl Chloride", 0xfff0f0, NONE).asGas().add(Carbon, 2, Hydrogen, 3, Chlorine, 1);
    public static Material SulfurDioxide = new Material(651, "Sulfur Dioxide", 0xc8c819, NONE).asGas().add(Sulfur, 1, Oxygen, 2);
    public static Material SulfurTrioxide = new Material(652, "Sulfur Trioxide", 0xa0a014, NONE).asGas()/*.setTemp(344, 1)*/.add(Sulfur, 1, Oxygen, 3);
    public static Material Dimethylamine = new Material(656, "Dimethylamine", 0x554469, NONE).asGas().add(Carbon, 2, Hydrogen, 7, Nitrogen, 1);
    public static Material DinitrogenTetroxide = new Material(657, "Dinitrogen Tetroxide", 0x004184, NONE).asGas().add(Nitrogen, 2, Oxygen, 4);
    public static Material NitricOxide = new Material(658, "Nitric Oxide", 0x7dc8f0, NONE).asGas().add(Nitrogen, 1, Oxygen, 1);
    public static Material Ammonia = new Material(659, "Ammonia", 0x3f3480, NONE).asGas().add(Nitrogen, 1, Hydrogen, 3);
    public static Material Chloromethane = new Material(664, "Chloromethane", 0xc82ca0, NONE).asGas().add(Carbon, 1, Hydrogen, 3, Chlorine, 1);
    public static Material Tetrafluoroethylene = new Material(666, "Tetrafluoroethylene", 0x7d7d7d, NONE).asGas().add(Carbon, 2, Fluorine, 4);
    public static Material CarbonMonoxide = new Material(674, "Carbon Monoxide", 0x0e4880, NONE).asGas(24).add(Carbon, 1, Oxygen, 1);
    public static Material Ethylene = new Material(677, "Ethylene", 0xe1e1e1, NONE).asGas(128).add(Carbon, 2, Hydrogen, 4);
    public static Material Propene = new Material(678, "Propene", 0xffdd55, NONE).asGas(192).add(Carbon, 3, Hydrogen, 6);
    public static Material Ethenone = new Material(641, "Ethenone", 0x141446, NONE).asGas().add(Carbon, 2, Hydrogen, 2, Oxygen, 1);
    public static Material HydricSulfide = new Material(460, "Hydric Sulfide", 0xffffff, NONE).asGas().add(Hydrogen, 2, Sulfur, 1);

    /** Fluids **/
    public static Material Lava = new Material(700, "Lava", 0xff4000, NONE).asFluid();
    public static Material UUAmplifier = new Material(721, "UU-Amplifier", 0x600080, NONE).asFluid();
    public static Material UUMatter = new Material(703, "UU-Matter", 0x8000c4, NONE).asFluid();
    public static Material Antimatter = new Material(999, "Antimatter", 0x8000c4, NONE).asFluid();
    public static Material CharcoalByproducts = new Material(675, "Charcoal Byproducts", 0x784421, NONE).asFluid(); //TODO rename
    public static Material FermentedBiomass = new Material(691, "Fermented Biomass", 0x445500, NONE).asFluid(); //TODO needed?
    public static Material Glue = new Material(726, "Glue", 0xc8c400, NONE).asFluid();
    public static Material Honey = new Material(725, "Honey", 0xd2c800, NONE).asFluid();
    public static Material Lubricant = new Material(724, "Lubricant", 0xffc400, NONE).asFluid();
    public static Material WoodTar = new Material(662, "Wood Tar", 0x28170b, NONE).asFluid();
    public static Material WoodVinegar = new Material(661, "Wood Vinegar", 0xd45500, NONE).asFluid();
    public static Material LiquidAir = new Material(495, "Liquid Air", 0xa9d0f5, NONE).asFluid()/*.setTemp(79, 0)*/.add(Nitrogen, 40, Oxygen, 11, Argon, 1, NobleGases, 1); //TODO Rrename to liquid oxygen
    public static Material Water = new Material(701, "Water", 0x0000ff, NONE).asFluid().add(Hydrogen, 2, Oxygen, 1);
    public static Material Glyceryl = new Material(714, "Glyceryl", 0x009696, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Nitrogen, 3, Oxygen, 9);
    public static Material Titaniumtetrachloride = new Material(376, "Titaniumtetrachloride", 0xd40d5c, NONE).asFluid().add(Titanium, 1, Chlorine, 4);
    public static Material SaltWater = new Material(692, "Salt Water", 0x0000c8, NONE).asFluid(); //TODO needed?
    public static Material SodiumPersulfate = new Material(718, "Sodium Persulfate", 0xffffff, NONE).asFluid().add(Sodium, 2, Sulfur, 2, Oxygen, 8);
    public static Material DilutedHydrochloricAcid = new Material(606, "Diluted Hydrochloric Acid", 0x99a7a3, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1);
    public static Material GrowthMediumRaw = new Material(608, "Raw Growth Medium", 0xd38d5f, NONE).asFluid(); //TODO needed?
    public static Material GrowthMediumSterilized = new Material(609, "Growth Medium Sterilized", 0xdeaa87, NONE).asFluid(); //TODO needed?
    public static Material NitrationMixture = new Material(628, "Nitration Mixture", 0xe6e2ab, NONE).asFluid();
    public static Material Dichlorobenzene = new Material(632, "Dichlorobenzene", 0x004455, NONE).asFluid().add(Carbon, 6, Hydrogen, 4, Chlorine, 2);
    public static Material Styrene = new Material(637, "Styrene", 0xd2c8be, NONE).asFluid().add(Carbon, 8, Hydrogen, 8);
    public static Material Isoprene = new Material(638, "Isoprene", 0x141414, NONE).asFluid().add(Carbon, 8, Hydrogen, 8);
    public static Material Tetranitromethane = new Material(639, "Tetranitromethane", 0x0f2828, NONE).asFluid().add(Carbon, 1, Nitrogen, 4, Oxygen, 8);
    public static Material Epichlorohydrin = new Material(648, "Epichlorohydrin", 0x501d05, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Chlorine, 1, Oxygen, 1);
    public static Material NitricAcid = new Material(653, "Nitric Acid", 0xe6e2ab, NONE).asFluid().add(Hydrogen, 1, Nitrogen, 1, Oxygen, 3);
    public static Material Dimethylhydrazine = new Material(654, "1,1-Dimethylhydrazine", 0x000055, NONE).asFluid().add(Carbon, 2, Hydrogen, 8, Nitrogen, 2);
    public static Material Chloramine = new Material(655, "Chloramine", 0x3f9f80, NONE).asFluid().add(Nitrogen, 1, Hydrogen, 2, Chlorine, 1);
    public static Material Dimethyldichlorosilane = new Material(663, "Dimethyldichlorosilane", 0x441650, NONE).asFluid().add(Carbon, 2, Hydrogen, 6, Chlorine, 2, Silicon, 1);
    public static Material HydrofluoricAcid = new Material(667, "Hydrofluoric Acid", 0x0088aa, NONE).asFluid().add(Hydrogen, 1, Fluorine, 1);
    public static Material Chloroform = new Material(668, "Chloroform", 0x892ca0, NONE).asFluid().add(Carbon, 1, Hydrogen, 1, Chlorine, 3);
    public static Material BisphenolA = new Material(669, "Bisphenol A", 0xd4b300, NONE).asFluid().add(Carbon, 15, Hydrogen, 16, Oxygen, 2);
    public static Material AceticAcid = new Material(670, "Acetic Acid", 0xc8b4a0, NONE).asFluid().add(Carbon, 2, Hydrogen, 4, Oxygen, 2);
    public static Material CalciumAcetateSolution = new Material(671, "Calcium Acetate Solution", 0xdcc8b4, RUBY).asFluid().add(Calcium, 1, Carbon, 2, Oxygen, 4, Hydrogen, 6);
    public static Material Acetone = new Material(672, "Acetone", 0xafafaf, NONE).asFluid().add(Carbon, 3, Hydrogen, 6, Oxygen, 1);
    public static Material Methanol = new Material(673, "Methanol", 0xaa8800, NONE).asFluid(84).add(Carbon, 1, Hydrogen, 4, Oxygen, 1);
    public static Material VinylAcetate = new Material(679, "Vinyl Acetate", 0xffb380, NONE).asFluid().add(Carbon, 4, Hydrogen, 6, Oxygen, 2);
    public static Material PolyvinylAcetate = new Material(680, "Polyvinyl Acetate", 0xff9955, NONE).asFluid().add(Carbon, 4, Hydrogen, 6, Oxygen, 2);
    public static Material MethylAcetate = new Material(681, "Methyl Acetate", 0xeec6af, NONE).asFluid().add(Carbon, 3, Hydrogen, 6, Oxygen, 2);
    public static Material AllylChloride = new Material(682, "Allyl Chloride", 0x87deaa, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Chlorine, 1);
    public static Material HydrochloricAcid = new Material(683, "Hydrochloric Acid", 0x6f8a91, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1);
    public static Material HypochlorousAcid = new Material(684, "Hypochlorous Acid", 0x6f8a91, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1, Oxygen, 1);
    public static Material Cumene = new Material(688, "Cumene", 0x552200, NONE).asFluid().add(Carbon, 9, Hydrogen, 12);
    public static Material PhosphoricAcid = new Material(689, "Phosphoric Acid", 0xdcdc00, NONE).asFluid().add(Hydrogen, 3, Phosphor, 1, Oxygen, 4);
    //    public static Materials Coolant = new Materials(690, "Coolant", 220, 220, 0, lightBlue, NONE).asFluid(); //TODO remove?
    public static Material SulfuricAcid = new Material(720, "Sulfuric Acid", 0xff8000, NONE).asFluid().add(Hydrogen, 2, Sulfur, 1, Oxygen, 4);
    public static Material DilutedSulfuricAcid = new Material(640, "Diluted Sulfuric Acid", 0xc07820, NONE).asFluid().add(SulfuricAcid, 1);
    public static Material Benzene = new Material(686, "Benzene", 0x1a1a1a, NONE).asFluid(288).add(Carbon, 6, Hydrogen, 6);
    public static Material Phenol = new Material(687, "Phenol", 0x784421, NONE).asFluid(288).add(Carbon, 6, Hydrogen, 6, Oxygen, 1);
    public static Material Toluene = new Material(647, "Toluene", 0x501d05, NONE).asFluid(328).add(Carbon, 7, Hydrogen, 8);
    public static Material SulfuricNaphtha = new Material(736, "Sulfuric Naphtha", 0xffff00, NONE).asFluid(32);
    public static Material Naphtha = new Material(739, "Naphtha", 0xffff00, NONE).asFluid(256);
    //TODO
    public static Material DrillingFluid = new Material(743, "Drilling Fluid", 0xffffff, NONE).asFluid();
    public static Material BlueVitriol = new Material(744, "Blue Vitriol Water Solution", 0xffffff, NONE).asFluid();
    public static Material IndiumConcentrate = new Material(745, "Indium Concentrate", 0xffffff, NONE).asFluid();
    public static Material NickelSulfate = new Material(746, "Nickel Sulfate", 0xffffff, NONE).asFluid();
    public static Material RocketFuel = new Material(747, "Rocket Fuel", 0xffffff, NONE).asFluid();
    public static Material LeadZincSolution = new Material(748, "Lead-Zinc Solution", 0xffffff, NONE).asFluid();

    /** Fuels **/
    public static Material Diesel = new Material(708, "Diesel", 0xffff00, NONE).asFluid(128);
    public static Material NitroFuel = new Material(709, "Cetane-Boosted Diesel", 0xc8ff00, NONE).asFluid(512);
    public static Material BioDiesel = new Material(627, "Bio Diesel", 0xff8000, NONE).asFluid(192);
    public static Material Biomass = new Material(704, "Biomass", 0x00ff00, NONE).asFluid(8);
    public static Material Ethanol = new Material(706, "Ethanol", 0xff8000, NONE).asFluid(148).add(Carbon, 2, Hydrogen, 6, Oxygen, 1);
    public static Material Creosote = new Material(712, "Creosote", 0x804000, NONE).asFluid(8);
    public static Material FishOil = new Material(711, "Fish Oil", 0xffc400, NONE).asFluid(2);
    public static Material Oil = new Material(707, "Oil", 0x0a0a0a, NONE).asFluid(16);
    public static Material SeedOil = new Material(713, "Seed Oil", 0xc4ff00, NONE).asFluid(2);
    //public static Materials SeedOilHemp = new Materials(722, "Hemp Seed Oil", 196, 255, 0, lime, NONE).asSemi(2);
    //public static Materials SeedOilLin = new Materials(723, "Lin Seed Oil", 196, 255, 0, lime, NONE).asSemi(2);
    public static Material OilExtraHeavy = new Material(729, "Extra Heavy Oil", 0x0a0a0a, NONE).asFluid(40);
    public static Material OilHeavy = new Material(730, "Heavy Oil", 0x0a0a0a, NONE).asFluid(32);
    public static Material OilMedium = new Material(731, "Raw Oil", 0x0a0a0a, NONE).asFluid(24);
    public static Material OilLight = new Material(732, "Light Oil", 0x0a0a0a, NONE).asFluid(16);
    public static Material SulfuricLightFuel = new Material(737, "Sulfuric Light Diesel", 0xffff00, NONE).asFluid(32);
    public static Material SulfuricHeavyFuel = new Material(738, "Sulfuric Heavy Diesel", 0xffff00, NONE).asFluid(32);
    public static Material LightDiesel = new Material(740, "Light Diesel", 0xffff00, NONE).asFluid(256);
    public static Material HeavyDiesel = new Material(741, "Heavy Diesel", 0xffff00, NONE).asFluid(192);
    public static Material Glycerol = new Material(629, "Glycerol", 0x87de87, NONE).asFluid(164).add(Carbon, 3, Hydrogen, 8, Oxygen, 3);

    /** Dusts **/
    public static Material SodiumSulfide = new Material(719, "Sodium Sulfide", 0xffe680, NONE).asDust().add(Sodium, 2, Sulfur, 1);
    public static Material IridiumSodiumOxide = new Material(240, "Iridium Sodium Oxide", 0xffffff, NONE).asDust();
    public static Material PlatinumGroupSludge = new Material(241, "Platinum Group Sludge", 0x001e00, NONE).asDust();
    public static Material Glowstone = new Material(811, "Glowstone", 0xffff00, SHINY).asDust();
    public static Material Graphene = new Material(819, "Graphene", 0x808080, DULL).asDust();
    public static Material Oilsands = new Material(878, "Oilsands", 0x0a0a0a, NONE).asDust().addOre();
    //    public static Materials Paper = new Materials(879, "Paper", 0xfafafa, PAPER).asDust(); //TODO needed?
    public static Material RareEarth = new Material(891, "Rare Earth", 0x808064, NONE).asDust();
    public static Material Endstone = new Material(808, "Endstone", 0xffffff, DULL).asDust();
    public static Material Netherrack = new Material(807, "Netherrack", 0xc80000, DULL).asDust();
    public static Material Almandine = new Material(820, "Almandine", 0xff0000, ROUGH).asDust().addOre().add(Aluminium, 2, Iron, 3, Silicon, 3, Oxygen, 12);
    public static Material Andradite = new Material(821, "Andradite", 0x967800, ROUGH).asDust().add(Calcium, 3, Iron, 2, Silicon, 3, Oxygen, 12);
    public static Material Ash = new Material(815, "Ash", 0x969696, DULL).asDust();
    public static Material BandedIron = new Material(917, "Banded Iron", 0x915a5a, DULL).asDust().addOre().add(Iron, 2, Oxygen, 3);
    public static Material BrownLimonite = new Material(930, "Brown Limonite", 0xc86400, METALLIC).asDust().addOre().add(Iron, 1, Hydrogen, 1, Oxygen, 2);
    public static Material Calcite = new Material(823, "Calcite", 0xfae6dc, DULL).asDust().addOre().add(Calcium, 1, Carbon, 1, Oxygen, 3);
    public static Material Cassiterite = new Material(824, "Cassiterite", 0xdcdcdc, METALLIC).asDust().addOre().add(Tin, 1, Oxygen, 2);
    public static Material Chalcopyrite = new Material(855, "Chalcopyrite", 0xa07828, DULL).asDust().addOre().add(Copper, 1, Iron, 1, Sulfur, 2);
    public static Material Clay = new Material(805, "Clay", 0xc8c8dc, ROUGH).asDust().add(Sodium, 2, Lithium, 1, Aluminium, 2, Silicon, 2, Water, 6);
    public static Material Cobaltite = new Material(827, "Cobaltite", 0x5050fa, METALLIC).asDust().addOre().add(Cobalt, 1, Arsenic, 1, Sulfur, 1);
    public static Material Sheldonite = new Material(828, "Sheldonite", 0xffffc8, METALLIC).asDust().addOre().add(Platinum, 3, Nickel, 1, Sulfur, 1, Palladium, 1);
    public static Material DarkAsh = new Material(816, "Dark Ash", 0x323232, DULL).asDust();
    public static Material Galena = new Material(830, "Galena", 0x643c64, DULL).asDust().addOre().add(Lead, 3, Silver, 3, Sulfur, 2);
    public static Material Garnierite = new Material(906, "Garnierite", 0x32c846, METALLIC).asDust().addOre().add(Nickel, 1, Oxygen, 1);
    public static Material Grossular = new Material(831, "Grossular", 0xc86400, ROUGH).asDust().addOre().add(Calcium, 3, Aluminium, 2, Silicon, 3, Oxygen, 12);
    public static Material Ilmenite = new Material(918, "Ilmenite", 0x463732, METALLIC).asDust().addOre().add(Iron, 1, Titanium, 1, Oxygen, 3);
    public static Material Rutile = new Material(375, "Rutile", 0xd40d5c, GEM_H).asDust().add(Titanium, 1, Oxygen, 2);
    public static Material Bauxite = new Material(822, "Bauxite", 0xc86400, DULL).asDust().addOre().add(Rutile, 2, Aluminium, 16, Hydrogen, 10, Oxygen, 11);
    public static Material Magnesiumchloride = new Material(377, "Magnesiumchloride", 0xd40d5c, DULL).asDust().add(Magnesium, 1, Chlorine, 2);
    public static Material Magnesite = new Material(908, "Magnesite", 0xfafab4, METALLIC).asDust().addOre().add(Magnesium, 1, Carbon, 1, Oxygen, 3);
    public static Material Magnetite = new Material(870, "Magnetite", 0x1e1e1e, METALLIC).asDust().addOre().add(Iron, 3, Oxygen, 4);
    public static Material Molybdenite = new Material(942, "Molybdenite", 0x91919, METALLIC).asDust().addOre().add(Molybdenum, 1, Sulfur, 2);
    public static Material Obsidian = new Material(804, "Obsidian", 0x503264, DULL).asDust().add(Magnesium, 1, Iron, 1, Silicon, 2, Oxygen, 8);
    public static Material Phosphate = new Material(833, "Phosphate", 0xffff00, DULL).asDust().addOre().add(Phosphor, 1, Oxygen, 4);
    public static Material Polydimethylsiloxane = new Material(633, "Polydimethylsiloxane", 0xf5f5f5, NONE).asDust().add(Carbon, 2, Hydrogen, 6, Oxygen, 1, Silicon, 1);
    public static Material Powellite = new Material(883, "Powellite", 0xffff00, DULL).asDust().addOre().add(Calcium, 1, Molybdenum, 1, Oxygen, 4);
    public static Material Pyrite = new Material(834, "Pyrite", 0x967828, ROUGH).asDust().addOre().add(Iron, 1, Sulfur, 2);
    public static Material Pyrolusite = new Material(943, "Pyrolusite", 0x9696aa, DULL).asDust().addOre().add(Manganese, 1, Oxygen, 2);
    public static Material Pyrope = new Material(835, "Pyrope", 0x783264, METALLIC).asDust().addOre().add(Aluminium, 2, Magnesium, 3, Silicon, 3, Oxygen, 12);
    public static Material RockSalt = new Material(944, "Rock Salt", 0xf0c8c8, NONE).asDust().addOre().add(Potassium, 1, Chlorine, 1);
    public static Material RawRubber = new Material(896, "Raw Rubber", 0xccc789, DULL).asDust().add(Carbon, 5, Hydrogen, 8);
    public static Material Salt = new Material(817, "Salt", 0xfafafa, NONE).asDust().addOre().add(Sodium, 1, Chlorine, 1);
    public static Material Saltpeter = new Material(836, "Saltpeter", 0xe6e6e6, NONE).asDust().add(Potassium, 1, Nitrogen, 1, Oxygen, 3);
    public static Material Scheelite = new Material(910, "Scheelite", 0xc88c14, DULL).asDust(2500, 2500).addOre().add(Tungsten, 1, Calcium, 2, Oxygen, 4);
    public static Material SiliconDioxide = new Material(837, "Silicon Dioxide", 0xc8c8c8, QUARTZ).asDust().add(Silicon, 1, Oxygen, 2);
    public static Material Pyrochlore = new Material(607, "Pyrochlore", 0x2b1100, METALLIC).asDust().addOre().add(Calcium, 2, Niobium, 2, Oxygen, 7);
    public static Material FerriteMixture = new Material(612, "Ferrite Mixture", 0xb4b4b4, METALLIC).asDust().add(Nickel, 1, Zinc, 1, Iron, 4);
    public static Material Massicot = new Material(614, "Massicot", 0xffdd55, DULL).asDust().add(Lead, 1, Oxygen, 1);
    public static Material ArsenicTrioxide = new Material(615, "Arsenic Trioxide", 0xffffff, SHINY).asDust().add(Arsenic, 2, Oxygen, 3);
    public static Material CobaltOxide = new Material(616, "Cobalt Oxide", 0x668000, DULL).asDust().add(Cobalt, 1, Oxygen, 1);
    public static Material Zincite = new Material(617, "Zincite", 0xfffff5, DULL).asDust().add(Zinc, 1, Oxygen, 1); //TODO needed?
    public static Material Magnesia = new Material(621, "Magnesia", 0xffffff, DULL).asDust().add(Magnesium, 1, Oxygen, 1);
    public static Material Quicklime = new Material(622, "Quicklime", 0xf0f0f0, DULL).asDust().add(Calcium, 1, Oxygen, 1);
    public static Material Potash = new Material(623, "Potash", 0x784237, DULL).asDust().add(Potassium, 2, Oxygen, 1);
    public static Material SodaAsh = new Material(624, "Soda Ash", 0xdcdcff, DULL).asDust().add(Sodium, 2, Carbon, 1, Oxygen, 3);
    public static Material Brick = new Material(625, "Brick", 0x9b5643, ROUGH).asDust().add(Aluminium, 4, Silicon, 3, Oxygen, 12);
    public static Material Fireclay = new Material(626, "Fireclay", 0xada09b, ROUGH).asDust().add(Brick, 1);
    public static Material SodiumBisulfate = new Material(630, "Sodium Bisulfate", 0x004455, NONE).asDust().add(Sodium, 1, Hydrogen, 1, Sulfur, 1, Oxygen, 4);
    public static Material RawStyreneButadieneRubber = new Material(634, "Raw Styrene-Butadiene Rubber", 0x54403d, SHINY).asDust().add(Styrene, 1, Butadiene, 3);
    public static Material PhosphorousPentoxide = new Material(665, "Phosphorous Pentoxide", 0xdcdc00, NONE).asDust().add(Phosphor, 4, Oxygen, 10);
    public static Material MetalMixture = new Material(676, "Metal Mixture", 0x502d16, METALLIC).asDust();
    public static Material SodiumHydroxide = new Material(685, "Sodium Hydroxide", 0x003380, DULL).asDust().add(Sodium, 1, Oxygen, 1, Hydrogen, 1);
    public static Material Spessartine = new Material(838, "Spessartine", 0xff6464, DULL).asDust().addOre().add(Aluminium, 2, Manganese, 3, Silicon, 3, Oxygen, 12);
    public static Material Sphalerite = new Material(839, "Sphalerite", 0xffffff, DULL).asDust().addOre().add(Zinc, 1, Sulfur, 1);
    public static Material Stibnite = new Material(945, "Stibnite", 0x464646, METALLIC).asDust().addOre().add(Antimony, 2, Sulfur, 3);
    public static Material Tetrahedrite = new Material(840, "Tetrahedrite", 0xc82000, DULL).asDust().addOre().add(Copper, 3, Antimony, 1, Sulfur, 3, Iron, 1);
    public static Material Tungstate = new Material(841, "Tungstate", 0x373223, DULL).asDust().addOre().add(Tungsten, 1, Lithium, 2, Oxygen, 4);
    public static Material Uraninite = new Material(922, "Uraninite", 0x232323, METALLIC).asDust().addOre().add(Uranium, 1, Oxygen, 2);
    public static Material Uvarovite = new Material(842, "Uvarovite", 0xb4ffb4, DIAMOND).asDust().add(Calcium, 3, Chrome, 2, Silicon, 3, Oxygen, 12);
    public static Material Wood = new Material(809, "Wood", 0x643200, NONE).asDust().addTools(2.0F, 16, 0).add(PLATE, GEAR).add(Carbon, 1, Oxygen, 1, Hydrogen, 1);
    public static Material Stone = new Material(299, "Stone", 0xcdcdcd, ROUGH).asDust().add(GEAR).addTools(4.0F, 32, 1);
    public static Material Wulfenite = new Material(882, "Wulfenite", 0xff8000, DULL).asDust().addOre().add(Lead, 1, Molybdenum, 1, Oxygen, 4);
    public static Material YellowLimonite = new Material(931, "Yellow Limonite", 0xc8c800, METALLIC).asDust().addOre().add(Iron, 1, Hydrogen, 1, Oxygen, 2);
    public static Material WoodSealed = new Material(889, "Sealed Wood", 0x502800, NONE).asDust().addTools(3.0F, 24, 0).add(Wood, 1);
    public static Material Blaze = new Material(801, "Blaze", 0xffc800, NONE).asDust().add(DarkAsh, 1, Sulfur, 1/*, Magic, 1*/);
    public static Material Flint = new Material(802, "Flint", 0x002040, FLINT).asDust().addTools(2.5F, 64, 1).add(SiliconDioxide, 1);
    public static Material Marble = new Material(845, "Marble", 0xc8c8c8, NONE).asDust().add(Magnesium, 1, Calcite, 7);
    public static Material PotassiumFeldspar = new Material(847, "Potassium Feldspar", 0x782828, NONE).asDust().add(Potassium, 1, Aluminium, 1, Silicon, 3, Oxygen, 8);
    public static Material Biotite = new Material(848, "Biotite", 0x141e14, METALLIC).asDust().add(Potassium, 1, Magnesium, 3, Aluminium, 3, Fluorine, 2, Silicon, 3, Oxygen, 10);
    public static Material GraniteBlack = new Material(849, "Black Granite", 0x0a0a0a, ROUGH).asDust().addTools(4.0F, 64, 3).add(SiliconDioxide, 4, Biotite, 1);
    public static Material GraniteRed = new Material(850, "Red Granite", 0xff0080, ROUGH).asDust().addTools(4.0F, 64, 3).add(Aluminium, 2, PotassiumFeldspar, 1, Oxygen, 3);
    public static Material VanadiumMagnetite = new Material(923, "Vanadium Magnetite", 0x23233c, METALLIC).asDust().addOre().add(Magnetite, 1, Vanadium, 1);
    public static Material Bastnasite = new Material(905, "Bastnasite", 0xc86e2d, NONE).asDust().addOre().add(Cerium, 1, Carbon, 1, Fluorine, 1, Oxygen, 3);
    public static Material Pentlandite = new Material(909, "Pentlandite", 0xa59605, DULL).asDust().addOre().add(Nickel, 9, Sulfur, 8);
    public static Material Spodumene = new Material(920, "Spodumene", 0xbeaaaa, DULL).asDust().addOre().add(Lithium, 1, Aluminium, 1, Silicon, 2, Oxygen, 6);
    public static Material Tantalite = new Material(921, "Tantalite", 0x915028, METALLIC).asDust().addOre().add(Manganese, 1, Tantalum, 2, Oxygen, 6);
    public static Material Lepidolite = new Material(907, "Lepidolite", 0xf0328c, NONE).asDust().addOre().add(Potassium, 1, Lithium, 3, Aluminium, 4, Fluorine, 2, Oxygen, 10);
    public static Material Glauconite = new Material(933, "Glauconite", 0x82b43c, DULL).asDust().addOre().add(Potassium, 1, Magnesium, 2, Aluminium, 4, Hydrogen, 2, Oxygen, 12);
    public static Material Bentonite = new Material(927, "Bentonite", 0xf5d7d2, ROUGH).asDust().addOre().add(Sodium, 1, Magnesium, 6, Silicon, 12, Hydrogen, 6, Water, 5, Oxygen, 36);
    public static Material Pitchblende = new Material(873, "Pitchblende", 0xc8d200, DULL).asDust().addOre().add(Uraninite, 3, Thorium, 1, Lead, 1);
    public static Material Malachite = new Material(871, "Malachite", 0x055f05, DULL).asDust().addOre().add(Copper, 2, Carbon, 1, Hydrogen, 2, Oxygen, 5);
    public static Material Barite = new Material(904, "Barite", 0xe6ebff, DULL).asDust().addOre().add(Barium, 1, Sulfur, 1, Oxygen, 4);
    public static Material Talc = new Material(902, "Talc", 0x5ab45a, DULL).asDust().addOre().add(Magnesium, 3, Silicon, 4, Hydrogen, 2, Oxygen, 12);
    public static Material Soapstone = new Material(877, "Soapstone", 0x5f915f, DULL).asDust().addOre().add(Magnesium, 3, Silicon, 4, Hydrogen, 2, Oxygen, 12);
    public static Material Concrete = new Material(947, "Concrete", 0x646464, ROUGH).asDust(300).add(Stone, 1);
    public static Material AntimonyTrioxide = new Material(618, "Antimony Trioxide", 0xe6e6f0, DULL).asDust().add(Antimony, 2, Oxygen, 3);
    public static Material CupricOxide = new Material(619, "Cupric Oxide", 0x0f0f0f, DULL).asDust().add(Copper, 1, Oxygen, 1);
    public static Material Ferrosilite = new Material(620, "Ferrosilite", 0x97632a, DULL).asDust().add(Iron, 1, Silicon, 1, Oxygen, 3);

    /** Gems **/
    public static Material CertusQuartz = new Material(516, "Certus Quartz", 0xd2d2e6, QUARTZ).asGemBasic(false).addOre().addTools(5.0F, 32, 1).add(PLATE);
    public static Material Dilithium = new Material(515, "Dilithium", 0xfffafa, DIAMOND).asGemBasic(true);
    public static Material NetherQuartz = new Material(522, "Nether Quartz", 0xe6d2d2, QUARTZ).asGemBasic(false).addTools(1.0F, 32, 1).addOre();
    public static Material NetherStar = new Material(506, "Nether Star", 0xffffff, NONE).asGemBasic(false).addTools(1.0F, 5120, 4);
    public static Material Quartzite = new Material(523, "Quartzite", 0xd2e6d2, QUARTZ).asGemBasic(false).addOre().add(Silicon, 1, Oxygen, 2);

    //Brittle Gems
    public static Material BlueTopaz = new Material(513, "Blue Topaz", 0x0000ff, GEM_H).asGem(true).addTools(7.0F, 256, 3).add(Aluminium, 2, Silicon, 1, Fluorine, 2, Hydrogen, 2, Oxygen, 6);
    public static Material Charcoal = new Material(536, "Charcoal", 0x644646, NONE).asGemBasic(false).add(BLOCK).add(Carbon, 1);
    public static Material Coal = new Material(535, "Coal", 0x464646, ROUGH).asGemBasic(false).addOre().add(BLOCK).add(Carbon, 1);
    public static Material Lignite = new Material(538, "Lignite Coal", 0x644646, ROUGH).asGemBasic(false).addOre().add(BLOCK).add(Carbon, 3, Water, 1);

    public static Material Diamond = new Material(500, "Diamond", 0xc8ffff, DIAMOND).asGem(true).addOre().add(GEAR).addTools(8.0F, 1280, 3).add(Carbon, 1);
    public static Material Emerald = new Material(501, "Emerald", 0x50ff50, NONE).asGem(true).addOre().addTools(7.0F, 256, 2).add(Silver, 1, Gold, 1);
    public static Material GreenSapphire = new Material(504, "Green Sapphire", 0x64c882, GEM_H).asGem(true).addOre().addTools(7.0F, 256, 2).add(Aluminium, 2, Oxygen, 3);
    public static Material Lazurite = new Material(524, "Lazurite", 0x6478ff, LAPIS).asGemBasic(false).addOre().add(Aluminium, 6, Silicon, 6, Calcium, 8, Sodium, 8);
    public static Material Ruby = new Material(502, "Ruby", 0xff6464, RUBY).asGem(true).addOre().addTools(7.0F, 256, 2).add(Chrome, 1, Aluminium, 2, Oxygen, 3);
    public static Material Sapphire = new Material(503, "Sapphire", 0x6464c8, GEM_V).asGem(true).addOre().addTools(7.0F, 256, 2).add(Aluminium, 2, Oxygen, 3);
    public static Material Sodalite = new Material(525, "Sodalite", 0x1414ff, LAPIS).asGemBasic(false).addOre().add(Aluminium, 3, Silicon, 3, Sodium, 4, Chlorine, 1);
    public static Material Tanzanite = new Material(508, "Tanzanite", 0x4000c8, GEM_V).asGem(true).addTools(7.0F, 256, 2).add(Calcium, 2, Aluminium, 3, Silicon, 3, Hydrogen, 1, Oxygen, 13);
    public static Material Topaz = new Material(507, "Topaz", 0xff8000, GEM_H).asGem(true).addTools(7.0F, 256, 3).add(Aluminium, 2, Silicon, 1, Fluorine, 2, Hydrogen, 2, Oxygen, 6);
    public static Material Glass = new Material(890, "Glass", 0xfafafa, NONE).asGem(true).add(SiliconDioxide, 1);
    public static Material Olivine = new Material(505, "Olivine", 0x96ff96, RUBY).asGem(true).addOre().addTools(7.0F, 256, 2).add(Magnesium, 2, Iron, 1, SiliconDioxide, 2);
    public static Material Opal = new Material(510, "Opal", 0x0000ff, RUBY).asGem(true).addTools(7.0F, 256, 2).add(SiliconDioxide, 1);
    public static Material Amethyst = new Material(509, "Amethyst", 0xd232d2, FLINT).asGem(true).addTools(7.0F, 256, 3).add(SiliconDioxide, 4, Iron, 1);
    public static Material Lapis = new Material(526, "Lapis", 0x4646dc, LAPIS).asGemBasic(false).addOre().add(Lazurite, 12, Sodalite, 2, Pyrite, 1, Calcite, 1);
    public static Material EnderPearl = new Material(532, "Enderpearl", 0x6cdcc8, SHINY).asGemBasic(false).add(ROD, PLATE).add(Beryllium, 1, Potassium, 4, Nitrogen, 5/*, Magic, 6*/);
    public static Material EnderEye = new Material(533, "Endereye", 0xa0fae6, SHINY).asGemBasic(false).add(ROD, PLATE).add(EnderPearl, 1, Blaze, 1);
    public static Material Apatite = new Material(530, "Apatite", 0xc8c8ff, DIAMOND).asGemBasic(false).addOre().add(Calcium, 5, Phosphate, 3, Chlorine, 1);
    public static Material Phosphorus = new Material(534, "Phosphorus", 0xffff00, FLINT).asGemBasic(false).addOre().add(Calcium, 3, Phosphate, 2);
    public static Material GarnetRed = new Material(527, "Red Garnet", 0xc85050, RUBY).asGemBasic(true).addTools(7.0F, 128, 2).add(Pyrope, 3, Almandine, 5, Spessartine, 8);
    public static Material GarnetYellow = new Material(528, "Yellow Garnet", 0xc8c850, RUBY).asGemBasic(true).addTools(7.0F, 128, 2).add(Andradite, 5, Grossular, 8, Uvarovite, 3);
    public static Material Monazite = new Material(520, "Monazite", 0x324632, DIAMOND).asGemBasic(false).addOre().add(RareEarth, 1, Phosphate, 1);

    /** Metals **/
    public static Material AnnealedCopper = new Material(68, "Annealed Copper", 0xff7814, SHINY).asMetal(1357).add(PLATE, FOIL, ROD, WIREF).add(Copper, 1);
    public static Material BatteryAlloy = new Material(69, "Battery Alloy", 0x9c7ca0, DULL).asMetal(295).add(PLATE).add(Lead, 4, Antimony, 1);
    public static Material Brass = new Material(70, "Brass", 0xffb400, METALLIC).asMetal(1170).add(FRAME).addTools(7.0F, 96, 1).add(Zinc, 1, Copper, 3);
    public static Material Bronze = new Material(71, "Bronze", 0xff8000, METALLIC).asMetal(1125).add(GEAR, FRAME).addTools(6.0F, 192, 2).add(Tin, 1, Copper, 3);
    public static Material Cupronickel = new Material(72, "Cupronickel", 0xe39680, METALLIC).asMetal(1728).addTools(6.0F, 64, 1).add(Copper, 1, Nickel, 1);
    public static Material Electrum = new Material(303, "Electrum", 0xffff64, SHINY).asMetal(1330).add(PLATE, FOIL, ROD, WIREF).addTools(12.0F, 64, 2).add(Silver, 1, Gold, 1);
    public static Material Invar = new Material(302, "Invar", 0xb4b478, METALLIC).asMetal(1700).add(FRAME).addTools(6.0F, 256, 2).add(Iron, 2, Nickel, 1);
    public static Material Kanthal = new Material(312, "Kanthalm", 0xc2d2df, METALLIC).asMetal(1800, 1800).addTools(6.0F, 64, 2).add(Iron, 1, Aluminium, 1, Chrome, 1);
    public static Material Magnalium = new Material(313, "Magnalium", 0xc8beff, DULL).asMetal(870).addTools(6.0F, 256, 2).add(Magnesium, 1, Aluminium, 2);
    public static Material Nichrome = new Material(311, "Nichrome", 0xcdcef6, METALLIC).asMetal(2700, 2700).addTools(6.0F, 64, 2).add(Nickel, 4, Chrome, 1);
    public static Material NiobiumTitanium = new Material(360, "Niobium Titanium", 0x1d1d29, DULL).asMetal(4500, 4500).add(PLATE, FOIL, ROD, WIREF).add(Nickel, 4, Chrome, 1);
    public static Material PigIron = new Material(307, "Pig Iron", 0xc8b4b4, METALLIC).asMetal(1420).addTools(6.0F, 384, 2).add(Iron, 1);
    public static Material SolderingAlloy = new Material(314, "Soldering Alloy", 0xdcdce6, DULL).asMetal(400, 400).add(PLATE, FOIL, ROD, WIREF).add(Tin, 9, Antimony, 1);
    public static Material StainlessSteel = new Material(306, "Stainless Steel", 0xc8c8dc, SHINY).asMetal(1700, 1700).add(SCREW, GEAR, SGEAR, FRAME).addTools(7.0F, 480, 2).add(Iron, 6, Chrome, 1, Manganese, 1, Nickel, 1);
    public static Material Steel = new Material(305, "Steel", 0x808080, METALLIC).asMetal(1811, 1000).add(GEAR, SGEAR, PLATE, FOIL, WIREF, SCREW, ROD, RING, FRAME).addTools(6.0F, 512, 2).add(Iron, 50, Carbon, 1);
    public static Material Ultimet = new Material(344, "Ultimet", 0xb4b4e6, SHINY).asMetal(2700, 2700).add(Cobalt, 5, Chrome, 2, Nickel, 1, Molybdenum, 1);
    public static Material VanadiumGallium = new Material(357, "Vanadium Gallium", 0x80808c, SHINY).asMetal(4500, 4500).add(ROD).add(Vanadium, 3, Gallium, 1);
    public static Material WroughtIron = new Material(304, "Wrought Iron", 0xc8b4b4, METALLIC).asMetal(1811, 0).add(RING, FRAME).addTools(6.0F, 384, 2).add(Iron, 1);
    public static Material YttriumBariumCuprate = new Material(358, "Yttrium Barium Cuprate", 0x504046, METALLIC).asMetal(4500, 4500).add(PLATE, FOIL, ROD, WIREF).add(Yttrium, 1, Barium, 2, Copper, 3, Oxygen, 7);
    public static Material SterlingSilver = new Material(350, "Sterling Silver", 0xfadce1, SHINY).asMetal(1700, 1700).addTools(13.0F, 128, 2).add(Copper, 1, Silver, 4);
    public static Material RoseGold = new Material(351, "Rose Gold", 0xffe61e, SHINY).asMetal(1600, 1600).addTools(14.0F, 128, 2).add(Copper, 1, Gold, 4);
    public static Material BlackBronze = new Material(352, "Black Bronze", 0x64327d, DULL).asMetal(2000, 2000).addTools(12.0F, 256, 2).add(Gold, 1, Silver, 1, Copper, 3);
    public static Material BismuthBronze = new Material(353, "Bismuth Bronze", 0x647d7d, DULL).asMetal(1100, 1100).addTools(8.0F, 256, 2).add(Bismuth, 1, Zinc, 1, Copper, 3);
    public static Material BlackSteel = new Material(334, "Black Steel", 0x646464, METALLIC).asMetal(1200, 1200).add(FRAME).addTools(6.5F, 768, 2).add(Nickel, 1, BlackBronze, 1, Steel, 3);
    public static Material RedSteel = new Material(348, "Red Steel", 0x8c6464, METALLIC).asMetal(1300, 1300).addTools(7.0F, 896, 2).add(SterlingSilver, 1, BismuthBronze, 1, Steel, 2, BlackSteel, 4);
    public static Material BlueSteel = new Material(349, "Blue Steel", 0x64648c, METALLIC).asMetal(1400, 1400).add(FRAME).addTools(7.5F, 1024, 2).add(RoseGold, 1, Brass, 1, Steel, 2, BlackSteel, 4);
    public static Material DamascusSteel = new Material(335, "Damascus Steel", 0x6e6e6e, METALLIC).asMetal(2500, 1500).addTools(8.0F, 1280, 2).add(Steel, 1);
    public static Material TungstenSteel = new Material(316, "Tungstensteel", 0x6464a0, METALLIC).asMetal(3000, 3000).add(SCREW, GEAR, SGEAR, ROD, RING, FRAME).addTools(8.0F, 2560, 4).add(Steel, 1, Tungsten, 1);
    public static Material RedAlloy = new Material(308, "Red Alloy", 0xc80000, DULL).asMetal(295).add(PLATE, FOIL, ROD, WIREF).add(Copper, 1/*, Redstone, 4*/);
    public static Material CobaltBrass = new Material(343, "Cobalt Brass", 0xb4b4a0, METALLIC).asMetal(1500).add(GEAR).addTools(8.0F, 256, 2).add(Brass, 7, Aluminium, 1, Cobalt, 1);
    public static Material IronMagnetic = new Material(354, "Magnetic Iron", 0xc8c8c8, MAGNETIC).asMetal(1811).addTools(6.0F, 256, 2).add(Iron, 1);
    public static Material SteelMagnetic = new Material(355, "Magnetic Steel", 0x808080, MAGNETIC).asMetal(1000, 1000).addTools(6.0F, 512, 2).add(Steel, 1);
    public static Material NeodymiumMagnetic = new Material(356, "Magnetic Neodymium", 0x646464, MAGNETIC).asMetal(1297, 1297).addTools(7.0F, 512, 2).add(Neodymium, 1);
    public static Material NickelZincFerrite = new Material(613, "Nickel-Zinc Ferrite", 0x3c3c3c, ROUGH).asMetal(1500, 1500).addTools(3.0F, 32, 1).add(Nickel, 1, Zinc, 1, Iron, 4, Oxygen, 8);
    public static Material TungstenCarbide = new Material(370, "Tungsten Carbide", 0x330066, METALLIC).asMetal(2460, 2460).addTools(14.0F, 1280, 4).add(Tungsten, 1, Carbon, 1);
    public static Material VanadiumSteel = new Material(371, "Vanadium Steel", 0xc0c0c0, METALLIC).asMetal(1453, 1453).addTools(3.0F, 1920, 3).add(Vanadium, 1, Chrome, 1, Steel, 7);
    public static Material HSSG = new Material(372, "HSSG", 0x999900, METALLIC).asMetal(4500, 4500).add(GEAR, SGEAR, FRAME).addTools(10.0F, 4000, 3).add(TungstenSteel, 5, Chrome, 1, Molybdenum, 2, Vanadium, 1);
    public static Material HSSE = new Material(373, "HSSE", 0x336600, METALLIC).asMetal(5400, 5400).add(GEAR, SGEAR, FRAME).addTools(10.0F, 5120, 4).add(HSSG, 6, Cobalt, 1, Manganese, 1, Silicon, 1);
    public static Material HSSS = new Material(374, "HSSS", 0x660033, METALLIC).asMetal(5400, 5400).addTools(14.0F, 3000, 4).add(HSSG, 6, Iridium, 2, Osmium, 1);
    public static Material Osmiridium = new Material(317, "Osmiridium", 0x6464ff, METALLIC).asMetal(3333, 2500).add(FRAME).addTools(7.0F, 1600, 3);
    public static Material Duranium = new Material(328, "Duranium", 0xffffff, METALLIC).asMetal(295).addTools(16.0F, 5120, 5);
    public static Material Naquadah = new Material(324, "Naquadah", 0x323232, METALLIC).asMetal(5400, 5400).addTools(6.0F, 1280, 4).addOre();
    public static Material NaquadahAlloy = new Material(325, "Naquadah Alloy", 0x282828, METALLIC).asMetal(7200, 7200).addTools(8.0F, 5120, 5);
    public static Material NaquadahEnriched = new Material(326, "Naquadah Enriched", 0x323232, METALLIC).asMetal(4500, 4500).addOre().addTools(6.0F, 1280, 4); //TODO ORE flag added due to bee recipes, replace with OrePrefixes.mGeneratedItems
    public static Material Naquadria = new Material(327, "Naquadria", 0x1e1e1e, SHINY).asMetal(9000, 9000).addTools(1.0F, 512, 4).addOre();
    public static Material Tritanium = new Material(329, "Tritanium", 0xffffff, METALLIC).asMetal(295).add(FRAME).addTools(20.0F, 10240, 6);

    /** Solids **/
    public static Material Plastic = new Material(874, "Plastic", 0xc8c8c8, DULL).asSolid(295).add(PLATE).add(Carbon, 1, Hydrogen, 2);
    public static Material Epoxid = new Material(470, "Epoxid", 0xc88c14, DULL).asSolid(400).add(PLATE).addTools(3.0F, 32, 1).add(Carbon, 2, Hydrogen, 4, Oxygen, 1);
    public static Material Silicone = new Material(471, "Silicone", 0xdcdcdc, DULL).asSolid(900).add(PLATE, FOIL).addTools(3.0F, 128, 1).add(Carbon, 2, Hydrogen, 6, Oxygen, 1, Silicon, 1);
    public static Material Polycaprolactam = new Material(472, "Polycaprolactam", 0x323232, DULL).asSolid(500).addTools(3.0F, 32, 1).add(Carbon, 6, Hydrogen, 11, Nitrogen, 1, Oxygen, 1);
    public static Material Polytetrafluoroethylene = new Material(473, "Polytetrafluoroethylene", 0x646464, DULL).asSolid(1400).add(PLATE, FRAME).addTools(3.0F, 32, 1).add(Carbon, 2, Fluorine, 4);
    public static Material Rubber = new Material(880, "Rubber", 0x000000, SHINY).asSolid(295).add(PLATE, RING).addTools(1.5F, 32, 0).add(Carbon, 5, Hydrogen, 8);
    public static Material PolyphenyleneSulfide = new Material(631, "PolyphenyleneSulfide", 0xaa8800, DULL).asSolid(295).add(PLATE, FOIL).addTools(3.0F, 32, 1).add(Carbon, 6, Hydrogen, 4, Sulfur, 1);
    public static Material Polystyrene = new Material(636, "Polystyrene", 0xbeb4aa, DULL).asSolid(295).add(Carbon, 8, Hydrogen, 8);
    public static Material StyreneButadieneRubber = new Material(635, "Styrene-Butadiene Rubber", 0x211a18, SHINY).asSolid(295).addTools(3.0F, 128, 1).add(Styrene, 1, Butadiene, 3);
    public static Material PolyvinylChloride = new Material(649, "Polyvinyl Chloride", 0xd7e6e6, NONE).asSolid(295).add(PLATE, FOIL).addTools(3.0F, 32, 1).add(Carbon, 2, Hydrogen, 3, Chlorine, 1);
    public static Material GalliumArsenide = new Material(980, "Gallium Arsenide", 0xa0a0a0, DULL).asSolid(295, 1200).add(Arsenic, 1, Gallium, 1);
    public static Material EpoxidFiberReinforced = new Material(610, "Fiber-Reinforced Epoxy Resin", 0xa07010, DULL).asSolid(400).addTools(3.0F, 64, 1).add(Epoxid, 1);

    /** **/
    public static Material Redstone = new Material(810, "Redstone", 0xc80000, ROUGH).asDust().addOre().add(Silicon, 1, Pyrite, 5, Ruby, 1, Mercury, 3);
    public static Material Basalt = new Material(844, "Basalt", 0x1e1414, ROUGH).asDust().add(Olivine, 1, Calcite, 3, Flint, 8, DarkAsh, 4);

    public static Material get(int id) {
        return generated[id];
    }

    public static Material get(String name) {
        return generatedMap.get(name);
    }

    public static int getCount() {
        return generatedMap.size();
    }
}
