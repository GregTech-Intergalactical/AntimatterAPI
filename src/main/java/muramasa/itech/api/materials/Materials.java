package muramasa.itech.api.materials;

import muramasa.itech.api.enums.Element;
import muramasa.itech.api.enums.ItemFlag;
import muramasa.itech.api.enums.Prefix;
import muramasa.itech.api.enums.RecipeFlag;
import muramasa.itech.api.interfaces.IMaterialFlag;
import muramasa.itech.api.items.MetaItem;
import muramasa.itech.api.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

import static muramasa.itech.api.enums.Element.*;
import static muramasa.itech.api.enums.ItemFlag.*;
import static muramasa.itech.api.materials.MaterialSet.*;
import static muramasa.itech.api.enums.RecipeFlag.METAL;

public class Materials {

    private static HashMap<String, Materials> generatedMap = new HashMap<>();
    public static Materials[] generated = new Materials[1000]; //TODO remove Material IDs

    /** Basic Members **/
    private int id, rgb, itemMask, recipeMask, mass;
    private String name, displayName;
    private MaterialSet set;
    private boolean hasLocName;

    /** Element Members **/
    private Element element;

    /** Solid Members **/
    private int meltingPoint, blastFurnaceTemp;
    private boolean needsBlastFurnace;

    /** Gem Members **/
    private boolean transparent;

    /** Fluid/Gas/Plasma Members **/
    private int fuelPower;

    /** Tool Members **/
    private float toolSpeed;
    private int toolDurability, toolQuality;
    private String handleMaterial;

    /** Processing Members **/
    private ArrayList<MaterialStack> processInto = new ArrayList<>();
    private ArrayList<Materials> byProducts = new ArrayList<>();

    public static Materials Aluminium = new Materials(0, "Aluminium", 0x80c8f0, DULL, Al).asMetal(933, 1700).addOre().addTools(10.0F, 128, 2).add(RING, FOIL, SGEAR, GEAR, FRAME);
    public static Materials Beryllium = new Materials(1, "Beryllium", 0x64b464, METALLIC, Be).asMetal(1560).addTools(14.0F, 64, 2).addOre();
    public static Materials Bismuth = new Materials(2, "Bismuth", 0x64a0a0, METALLIC, Bi).asMetal(544).addTools(6.0F, 64, 1);
    public static Materials Carbon = new Materials(3, "Carbon", 0x141414, DULL, C).asSolid().addTools(1.0F, 64, 2);
    public static Materials Chrome = new Materials(4, "Chrome", 0xffe6e6, SHINY, Cr).asMetal(2180, 1700).addOre().addTools(11.0F, 256, 3).add(SCREW, RING, PLATE, ROTOR);
    public static Materials Cobalt = new Materials(5, "Cobalt", 0x5050fa, METALLIC, Co).asMetal(1768).addTools(8.0F, 512, 3);
    public static Materials Gold = new Materials(6, "Gold", 0xffff1e, SHINY, Au).asMetal(1337).addOre().addTools(12.0F, 64, 2).add(FOIL, ROD, WIREF, GEAR, BLOCK);
    public static Materials Iridium = new Materials(7, "Iridium", 0xf0f0f5, DULL, Ir).asMetal(2719, 2719).addOre().add(FRAME).addTools(6.0F, 2560, 3);
    public static Materials Iron = new Materials(8, "Iron", 0xc8c8c8, METALLIC, Fe).asMetal(1811).asPlasma().addTools(6.0F, 256, 2).add(RING, GEAR, FRAME, BLOCK);
    public static Materials Lanthanum = new Materials(9, "Lanthanum", 0xffffff, METALLIC, La).asSolid(1193, 1193);
    public static Materials Lead = new Materials(10, "Lead", 0x8c648c, DULL, Pb).asMetal(600).addOre().addTools(8.0F, 64, 1).add(PLATE, FOIL, ROD, WIREF, DPLATE);
    public static Materials Manganese = new Materials(11, "Manganese", 0xfafafa, DULL, Mn).asMetal(1519).addOre().addTools(7.0F, 512, 2).add(FOIL);
    public static Materials Molybdenum = new Materials(12, "Molybdenum", 0xb4b4dc, SHINY, Mo).asMetal(2896).addTools(7.0F, 512, 2).addOre();
    public static Materials Neodymium = new Materials(13, "Neodymium", 0x646464, METALLIC, Nd).asMetal(1297, 1297).addTools(7.0F, 512, 2).addOre();
    public static Materials Neutronium = new Materials(14, "Neutronium", 0xfafafa, DULL, Nt).asMetal(10000, 10000).addTools(24.0F, 655360, 6).add(SCREW, RING, GEAR, SGEAR, FRAME);
    public static Materials Nickel = new Materials(15, "Nickel", 0xc8c8fa, METALLIC, Ni).asMetal(1728).asPlasma().addOre().addTools(6.0F, 64, 2);
    public static Materials Osmium = new Materials(16, "Osmium", 0x3232ff, METALLIC, Os).asMetal(3306, 3306).addOre().addTools(16.0F, 1280, 4).add(SCREW, RING, PLATE, FOIL, ROD, WIREF);
    public static Materials Palladium = new Materials(17, "Palladium", 0x808080, SHINY, Pd).asMetal(1828, 1828).addTools(8.0F, 512, 2).addOre();
    public static Materials Platinum = new Materials(18, "Platinum", 0xffffc8, SHINY, Pt).asMetal(2041).addOre().addTools(12.0F, 64, 2).add(PLATE, FOIL, ROD, WIREF);
    public static Materials Plutonium = new Materials(19, "Plutonium 239", 0xf03232, METALLIC, Pu).asMetal(912).addTools(6.0F, 512, 3).addOre();
    public static Materials Plutonium241 = new Materials(20, "Plutonium 241", 0xfa4646, SHINY, Pu241).asMetal(912).addTools(6.0F, 512, 3);
    public static Materials Silver = new Materials(21, "Silver", 0xdcdcff, SHINY, Ag).asMetal(1234).addTools(10.0F, 64, 2).addOre();
    public static Materials Thorium = new Materials(22, "Thorium", 0x001e00, SHINY, Th).asMetal(2115).addTools(6.0F, 512, 2).addOre();
    public static Materials Titanium = new Materials(23, "Titanium", 0xdca0f0, METALLIC).asMetal();
    public static Materials Tungsten = new Materials(24, "Tungsten", 0x323232, METALLIC, W).asMetal(3695, 3000).addTools(7.0F, 2560, 3).add(FOIL);
    public static Materials Uranium = new Materials(25, "Uranium 238", 0x32f032, METALLIC, U).asMetal(1405).addTools(6.0F, 512, 3).addOre();
    public static Materials Uranium235 = new Materials(26, "Uranium 235", 0x46fa46, METALLIC, U235).asMetal(1405).addTools(6.0F, 512, 3).addOre();
    public static Materials Graphite = new Materials(27, "Graphite", 0x808080, DULL).asDust().addOre().addTools(5.0F, 32, 2);
    public static Materials Americium = new Materials(28, "Americium", 0xc8c8c8, METALLIC, Am).asMetal(1149, 0).add(PLATE, ROD);
    public static Materials Antimony = new Materials(29, "Antimony", 0xdcdcf0, SHINY, Sb).asMetal(1449, 0);
    public static Materials Argon = new Materials(30, "Argon", 0xff00f0, NONE, Ar).asGas();
    public static Materials Arsenic = new Materials(31, "Arsenic", 0xffffff, DULL, As).asSolid();
    public static Materials Barium = new Materials(32, "Barium", 0xffffff, METALLIC, Ba).asDust(1000).add(FOIL);
    public static Materials Boron = new Materials(33, "Boron", 0xfafafa, DULL, B).asDust(2349);
    public static Materials Caesium = new Materials(34, "Caesium", 0xffffff, METALLIC, Cs).asMetal(2349);
    public static Materials Calcium = new Materials(35, "Calcium", 0xfff5f5, METALLIC, Ca).asDust(1115);
    public static Materials Cadmium = new Materials(36, "Cadmium", 0x32323c, SHINY, Cd).asDust(594);
    public static Materials Cerium = new Materials(37, "Cerium", 0xffffff, METALLIC, Ce).asSolid(1068, 1068);
    public static Materials Chlorine = new Materials(38, "Chlorine", 0xffffff, NONE, Cr).asGas();
    public static Materials Copper = new Materials(39, "Copper", 0xff6400, SHINY, Cu).asMetal(1357).addOre().add(PLATE, DPLATE, ROD, FOIL, WIREF, GEAR);
    public static Materials Deuterium = new Materials(40, "Deuterium", 0xffff00, NONE, D).asFluid();
    public static Materials Dysprosium = new Materials(41, "Dysprosium", 0xffffff, METALLIC, D).asMetal(1680, 1680);
    public static Materials Europium = new Materials(42, "Europium", 0xffffff, METALLIC, Eu).asMetal(1099, 1099).add(PLATE, ROD);
    public static Materials Fluorine = new Materials(43, "Fluorine", 0xffffff, NONE, F).asFluid();
    public static Materials Gallium = new Materials(44, "Gallium", 0xdcdcff, SHINY, Ga).asMetal(302).add(PLATE);
    public static Materials Hydrogen = new Materials(45, "Hydrogen", 0x0000ff, NONE, H).asGas();
    public static Materials Helium = new Materials(46, "Helium", 0xffff00, NONE, He).asPlasma();
    public static Materials Helium3 = new Materials(47, "Helium-3", 0xffffff, NONE, He_3).asGas();
    public static Materials Indium = new Materials(48, "Indium", 0x400080, METALLIC, In).asSolid(429);
    public static Materials Lithium = new Materials(49, "Lithium", 0xe1dcff, DULL, Li).asSolid(454).addOre();
    public static Materials Lutetium = new Materials(50, "Lutetium", 0xffffff, DULL, Lu).asMetal(1925, 1925);
    public static Materials Magnesium = new Materials(51, "Magnesium", 0xffc8c8, METALLIC, Mg).asMetal(923);
    public static Materials Mercury = new Materials(52, "Mercury", 0xffdcdc, SHINY, Hg).asFluid();
    public static Materials Niobium = new Materials(53, "Niobium", 0xbeb4c8, METALLIC, Nb).asMetal(2750, 2750);
    public static Materials Nitrogen = new Materials(54, "Nitrogen", 0x0096c8, NONE, N).asPlasma();
    public static Materials Oxygen = new Materials(55, "Oxygen", 0x0064c8, NONE, O).asPlasma();
    public static Materials Phosphor = new Materials(56, "Phosphor", 0xffff00, DULL, P).asDust(317);
    public static Materials Potassium = new Materials(57, "Potassium", 0xfafafa, METALLIC, K).asSolid(336);
    public static Materials Radon = new Materials(58, "Radon", 0xff00ff, NONE, Rn).asGas();
    public static Materials Silicon = new Materials(59, "Silicon", 0x3c3c50, METALLIC, Si).asMetal(1687, 1687).add(PLATE, FOIL, BLOCK);
    public static Materials Sodium = new Materials(60, "Sodium", 0x000096, METALLIC, Na).asDust(370);
    public static Materials Sulfur = new Materials(61, "Sulfur", 0xc8c800, DULL, S).asDust(388).addOre().asPlasma();
    public static Materials Tantalum = new Materials(62, "Tantalum", 0xffffff, METALLIC, Ta).asSolid(3290);
    public static Materials Tin = new Materials(63, "Tin", 0xdcdcdc, DULL, Sn).asMetal(505, 505).addOre().add(PLATE, ROD, BOLT, SCREW, RING, GEAR, FOIL, WIREF, FRAME);
    public static Materials Tritium = new Materials(64, "Tritium", 0xff0000, METALLIC, T).asFluid();
    public static Materials Vanadium = new Materials(65, "Vanadium", 0x323232, METALLIC, V).asMetal(2183, 2183);
    public static Materials Yttrium = new Materials(66, "Yttrium", 0xdcfadc, METALLIC, Y).asMetal(1799, 1799);
    public static Materials Zinc = new Materials(67, "Zinc", 0xfaf0f0, METALLIC, Zn).asMetal(692).addOre().add(PLATE, FOIL); //TODO ORE flag added due to bee recipes

    /** Gases **/
    public static Materials WoodGas = new Materials(660, "Wood Gas", 0xdecd87, NONE).asGas(24);
    public static Materials Methane = new Materials(715, "Methane", 0xffffff, NONE).asGas(104).add(Carbon, 1, Hydrogen, 4);
    public static Materials CarbonDioxide = new Materials(497, "Carbon Dioxide", 0xa9d0f5, NONE).asGas().add(Carbon, 1, Oxygen, 2);
    public static Materials NobleGases = new Materials(496, "Noble Gases", 0xc9e3fc, NONE).asGas()/*.setTemp(79, 0)*/.add(CarbonDioxide, 21, Helium, 9, Methane, 3, Deuterium, 1);
    public static Materials Air = new Materials(494, "Air", 0xc9e3fc, NONE).asGas().add(Nitrogen, 40, Oxygen, 11, Argon, 1, NobleGases, 1);
    public static Materials NitrogenDioxide = new Materials(717, "Nitrogen Dioxide", 0x64afff, NONE).asGas().add(Nitrogen, 1, Oxygen, 2);
    public static Materials NaturalGas = new Materials(733, "Natural Gas", 0xffffff, NONE).asGas(15);
    public static Materials SulfuricGas = new Materials(734, "Sulfuric Gas", 0xffffff, NONE).asGas(20);
    public static Materials ReNONEryGas = new Materials(735, "ReNONEry Gas", 0xffffff, NONE).asGas(128);
    public static Materials LPG = new Materials(742, "LPG", 0xffff00, NONE).asGas(256);
    public static Materials Ethane = new Materials(642, "Ethane", 0xc8c8ff, NONE).asGas(168).add(Carbon, 2, Hydrogen, 6);
    public static Materials Propane = new Materials(643, "Propane", 0xfae250, NONE).asGas(232).add(Carbon, 2, Hydrogen, 6);
    public static Materials Butane = new Materials(644, "Butane", 0xb6371e, NONE).asGas(296).add(Carbon, 4, Hydrogen, 10);
    public static Materials Butene = new Materials(645, "Butene", 0xcf5005, NONE).asGas(256).add(Carbon, 4, Hydrogen, 8);
    public static Materials Butadiene = new Materials(646, "Butadiene", 0xe86900, NONE).asGas(206).add(Carbon, 4, Hydrogen, 6);
    public static Materials VinylChloride = new Materials(650, "Vinyl Chloride", 0xfff0f0, NONE).asGas().add(Carbon, 2, Hydrogen, 3, Chlorine, 1);
    public static Materials SulfurDioxide = new Materials(651, "Sulfur Dioxide", 0xc8c819, NONE).asGas().add(Sulfur, 1, Oxygen, 2);
    public static Materials SulfurTrioxide = new Materials(652, "Sulfur Trioxide", 0xa0a014, NONE).asGas()/*.setTemp(344, 1)*/.add(Sulfur, 1, Oxygen, 3);
    public static Materials Dimethylamine = new Materials(656, "Dimethylamine", 0x554469, NONE).asGas().add(Carbon, 2, Hydrogen, 7, Nitrogen, 1);
    public static Materials DinitrogenTetroxide = new Materials(657, "Dinitrogen Tetroxide", 0x004184, NONE).asGas().add(Nitrogen, 2, Oxygen, 4);
    public static Materials NitricOxide = new Materials(658, "Nitric Oxide", 0x7dc8f0, NONE).asGas().add(Nitrogen, 1, Oxygen, 1);
    public static Materials Ammonia = new Materials(659, "Ammonia", 0x3f3480, NONE).asGas().add(Nitrogen, 1, Hydrogen, 3);
    public static Materials Chloromethane = new Materials(664, "Chloromethane", 0xc82ca0, NONE).asGas().add(Carbon, 1, Hydrogen, 3, Chlorine, 1);
    public static Materials Tetrafluoroethylene = new Materials(666, "Tetrafluoroethylene", 0x7d7d7d, NONE).asGas().add(Carbon, 2, Fluorine, 4);
    public static Materials CarbonMonoxide = new Materials(674, "Carbon Monoxide", 0x0e4880, NONE).asGas(24).add(Carbon, 1, Oxygen, 1);
    public static Materials Ethylene = new Materials(677, "Ethylene", 0xe1e1e1, NONE).asGas(128).add(Carbon, 2, Hydrogen, 4);
    public static Materials Propene = new Materials(678, "Propene", 0xffdd55, NONE).asGas(192).add(Carbon, 3, Hydrogen, 6);
    public static Materials Ethenone = new Materials(641, "Ethenone", 0x141446, NONE).asGas().add(Carbon, 2, Hydrogen, 2, Oxygen, 1);
    public static Materials HydricSulfide = new Materials(460, "Hydric Sulfide", 0xffffff, NONE).asGas().add(Hydrogen, 2, Sulfur, 1);

    /** Fluids **/
    public static Materials Lava = new Materials(700, "Lava", 0xff4000, NONE).asFluid();
    public static Materials UUAmplifier = new Materials(721, "UU-Amplifier", 0x600080, NONE).asFluid();
    public static Materials UUMatter = new Materials(703, "UU-Matter", 0x8000c4, NONE).asFluid();
    public static Materials Antimatter = new Materials(999, "Antimatter", 0x8000c4, NONE).asFluid();
    public static Materials CharcoalByproducts = new Materials(675, "Charcoal Byproducts", 0x784421, NONE).asFluid(); //TODO rename
    public static Materials FermentedBiomass = new Materials(691, "Fermented Biomass", 0x445500, NONE).asFluid(); //TODO needed?
    public static Materials Glue = new Materials(726, "Glue", 0xc8c400, NONE).asFluid();
    public static Materials Honey = new Materials(725, "Honey", 0xd2c800, NONE).asFluid();
    public static Materials Lubricant = new Materials(724, "Lubricant", 0xffc400, NONE).asFluid();
    public static Materials WoodTar = new Materials(662, "Wood Tar", 0x28170b, NONE).asFluid();
    public static Materials WoodVinegar = new Materials(661, "Wood Vinegar", 0xd45500, NONE).asFluid();
    public static Materials LiquidAir = new Materials(495, "Liquid Air", 0xa9d0f5, NONE).asFluid()/*.setTemp(79, 0)*/.add(Nitrogen, 40, Oxygen, 11, Argon, 1, NobleGases, 1); //TODO Rrename to liquid oxygen
    public static Materials Water = new Materials(701, "Water", 0x0000ff, NONE).asFluid().add(Hydrogen, 2, Oxygen, 1);
    public static Materials Glyceryl = new Materials(714, "Glyceryl", 0x009696, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Nitrogen, 3, Oxygen, 9);
    public static Materials Titaniumtetrachloride = new Materials(376, "Titaniumtetrachloride", 0xd40d5c, NONE).asFluid().add(Titanium, 1, Chlorine, 4);
    public static Materials SaltWater = new Materials(692, "Salt Water", 0x0000c8, NONE).asFluid(); //TODO needed?
    public static Materials SodiumPersulfate = new Materials(718, "Sodium Persulfate", 0xffffff, NONE).asFluid().add(Sodium, 2, Sulfur, 2, Oxygen, 8);
    public static Materials DilutedHydrochloricAcid = new Materials(606, "Diluted Hydrochloric Acid", 0x99a7a3, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1);
    public static Materials GrowthMediumRaw = new Materials(608, "Raw Growth Medium", 0xd38d5f, NONE).asFluid(); //TODO needed?
    public static Materials GrowthMediumSterilized = new Materials(609, "Growth Medium Sterilized", 0xdeaa87, NONE).asFluid(); //TODO needed?
    public static Materials NitrationMixture = new Materials(628, "Nitration Mixture", 0xe6e2ab, NONE).asFluid();
    public static Materials Dichlorobenzene = new Materials(632, "Dichlorobenzene", 0x004455, NONE).asFluid().add(Carbon, 6, Hydrogen, 4, Chlorine, 2);
    public static Materials Styrene = new Materials(637, "Styrene", 0xd2c8be, NONE).asFluid().add(Carbon, 8, Hydrogen, 8);
    public static Materials Isoprene = new Materials(638, "Isoprene", 0x141414, NONE).asFluid().add(Carbon, 8, Hydrogen, 8);
    public static Materials Tetranitromethane = new Materials(639, "Tetranitromethane", 0x0f2828, NONE).asFluid().add(Carbon, 1, Nitrogen, 4, Oxygen, 8);
    public static Materials Epichlorohydrin = new Materials(648, "Epichlorohydrin", 0x501d05, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Chlorine, 1, Oxygen, 1);
    public static Materials NitricAcid = new Materials(653, "Nitric Acid", 0xe6e2ab, NONE).asFluid().add(Hydrogen, 1, Nitrogen, 1, Oxygen, 3);
    public static Materials Dimethylhydrazine = new Materials(654, "1,1-Dimethylhydrazine", 0x000055, NONE).asFluid().add(Carbon, 2, Hydrogen, 8, Nitrogen, 2);
    public static Materials Chloramine = new Materials(655, "Chloramine", 0x3f9f80, NONE).asFluid().add(Nitrogen, 1, Hydrogen, 2, Chlorine, 1);
    public static Materials Dimethyldichlorosilane = new Materials(663, "Dimethyldichlorosilane", 0x441650, NONE).asFluid().add(Carbon, 2, Hydrogen, 6, Chlorine, 2, Silicon, 1);
    public static Materials HydrofluoricAcid = new Materials(667, "Hydrofluoric Acid", 0x0088aa, NONE).asFluid().add(Hydrogen, 1, Fluorine, 1);
    public static Materials Chloroform = new Materials(668, "Chloroform", 0x892ca0, NONE).asFluid().add(Carbon, 1, Hydrogen, 1, Chlorine, 3);
    public static Materials BisphenolA = new Materials(669, "Bisphenol A", 0xd4b300, NONE).asFluid().add(Carbon, 15, Hydrogen, 16, Oxygen, 2);
    public static Materials AceticAcid = new Materials(670, "Acetic Acid", 0xc8b4a0, NONE).asFluid().add(Carbon, 2, Hydrogen, 4, Oxygen, 2);
    public static Materials CalciumAcetateSolution = new Materials(671, "Calcium Acetate Solution", 0xdcc8b4, RUBY).asFluid().add(Calcium, 1, Carbon, 2, Oxygen, 4, Hydrogen, 6);
    public static Materials Acetone = new Materials(672, "Acetone", 0xafafaf, NONE).asFluid().add(Carbon, 3, Hydrogen, 6, Oxygen, 1);
    public static Materials Methanol = new Materials(673, "Methanol", 0xaa8800, NONE).asFluid(84).add(Carbon, 1, Hydrogen, 4, Oxygen, 1);
    public static Materials VinylAcetate = new Materials(679, "Vinyl Acetate", 0xffb380, NONE).asFluid().add(Carbon, 4, Hydrogen, 6, Oxygen, 2);
    public static Materials PolyvinylAcetate = new Materials(680, "Polyvinyl Acetate", 0xff9955, NONE).asFluid().add(Carbon, 4, Hydrogen, 6, Oxygen, 2);
    public static Materials MethylAcetate = new Materials(681, "Methyl Acetate", 0xeec6af, NONE).asFluid().add(Carbon, 3, Hydrogen, 6, Oxygen, 2);
    public static Materials AllylChloride = new Materials(682, "Allyl Chloride", 0x87deaa, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Chlorine, 1);
    public static Materials HydrochloricAcid = new Materials(683, "Hydrochloric Acid", 0x6f8a91, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1);
    public static Materials HypochlorousAcid = new Materials(684, "Hypochlorous Acid", 0x6f8a91, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1, Oxygen, 1);
    public static Materials Cumene = new Materials(688, "Cumene", 0x552200, NONE).asFluid().add(Carbon, 9, Hydrogen, 12);
    public static Materials PhosphoricAcid = new Materials(689, "Phosphoric Acid", 0xdcdc00, NONE).asFluid().add(Hydrogen, 3, Phosphor, 1, Oxygen, 4);
    //    public static Materials Coolant = new Materials(690, "Coolant", 220, 220, 0, lightBlue, NONE).asFluid(); //TODO remove?
    public static Materials SulfuricAcid = new Materials(720, "Sulfuric Acid", 0xff8000, NONE).asFluid().add(Hydrogen, 2, Sulfur, 1, Oxygen, 4);
    public static Materials DilutedSulfuricAcid = new Materials(640, "Diluted Sulfuric Acid", 0xc07820, NONE).asFluid().add(SulfuricAcid, 1);
    public static Materials Benzene = new Materials(686, "Benzene", 0x1a1a1a, NONE).asFluid(288).add(Carbon, 6, Hydrogen, 6);
    public static Materials Phenol = new Materials(687, "Phenol", 0x784421, NONE).asFluid(288).add(Carbon, 6, Hydrogen, 6, Oxygen, 1);
    public static Materials Toluene = new Materials(647, "Toluene", 0x501d05, NONE).asFluid(328).add(Carbon, 7, Hydrogen, 8);
    public static Materials SulfuricNaphtha = new Materials(736, "Sulfuric Naphtha", 0xffff00, NONE).asFluid(32);
    public static Materials Naphtha = new Materials(739, "Naphtha", 0xffff00, NONE).asFluid(256);
    //TODO
    public static Materials DrillingFluid = new Materials(743, "Drilling Fluid", 0xffffff, NONE).asFluid();
    public static Materials BlueVitriol = new Materials(744, "Blue Vitriol Water Solution", 0xffffff, NONE).asFluid();
    public static Materials IndiumConcentrate = new Materials(745, "Indium Concentrate", 0xffffff, NONE).asFluid();
    public static Materials NickelSulfate = new Materials(746, "Nickel Sulfate", 0xffffff, NONE).asFluid();
    public static Materials RocketFuel = new Materials(747, "Rocket Fuel", 0xffffff, NONE).asFluid();
    public static Materials LeadZincSolution = new Materials(748, "Lead-Zinc Solution", 0xffffff, NONE).asFluid();

    /** Fuels **/
    public static Materials Diesel = new Materials(708, "Diesel", 0xffff00, NONE).asFluid(128);
    public static Materials NitroFuel = new Materials(709, "Cetane-Boosted Diesel", 0xc8ff00, NONE).asFluid(512);
    public static Materials BioDiesel = new Materials(627, "Bio Diesel", 0xff8000, NONE).asFluid(192);
    public static Materials Biomass = new Materials(704, "Biomass", 0x00ff00, NONE).asFluid(8);
    public static Materials Ethanol = new Materials(706, "Ethanol", 0xff8000, NONE).asFluid(148).add(Carbon, 2, Hydrogen, 6, Oxygen, 1);
    public static Materials Creosote = new Materials(712, "Creosote", 0x804000, NONE).asFluid(8);
    public static Materials FishOil = new Materials(711, "Fish Oil", 0xffc400, NONE).asFluid(2);
    public static Materials Oil = new Materials(707, "Oil", 0x0a0a0a, NONE).asFluid(16);
    public static Materials SeedOil = new Materials(713, "Seed Oil", 0xc4ff00, NONE).asFluid(2);
    //public static Materials SeedOilHemp = new Materials(722, "Hemp Seed Oil", 196, 255, 0, lime, NONE).asSemi(2);
    //public static Materials SeedOilLin = new Materials(723, "Lin Seed Oil", 196, 255, 0, lime, NONE).asSemi(2);
    public static Materials OilExtraHeavy = new Materials(729, "Extra Heavy Oil", 0x0a0a0a, NONE).asFluid(40);
    public static Materials OilHeavy = new Materials(730, "Heavy Oil", 0x0a0a0a, NONE).asFluid(32);
    public static Materials OilMedium = new Materials(731, "Raw Oil", 0x0a0a0a, NONE).asFluid(24);
    public static Materials OilLight = new Materials(732, "Light Oil", 0x0a0a0a, NONE).asFluid(16);
    public static Materials SulfuricLightFuel = new Materials(737, "Sulfuric Light Diesel", 0xffff00, NONE).asFluid(32);
    public static Materials SulfuricHeavyFuel = new Materials(738, "Sulfuric Heavy Diesel", 0xffff00, NONE).asFluid(32);
    public static Materials LightDiesel = new Materials(740, "Light Diesel", 0xffff00, NONE).asFluid(256);
    public static Materials HeavyDiesel = new Materials(741, "Heavy Diesel", 0xffff00, NONE).asFluid(192);
    public static Materials Glycerol = new Materials(629, "Glycerol", 0x87de87, NONE).asFluid(164).add(Carbon, 3, Hydrogen, 8, Oxygen, 3);

    /** Dusts **/
    public static Materials SodiumSulfide = new Materials(719, "Sodium Sulfide", 0xffe680, NONE).asDust().add(Sodium, 2, Sulfur, 1);
    public static Materials IridiumSodiumOxide = new Materials(240, "Iridium Sodium Oxide", 0xffffff, NONE).asDust();
    public static Materials PlatinumGroupSludge = new Materials(241, "Platinum Group Sludge", 0x001e00, NONE).asDust();
    public static Materials Glowstone = new Materials(811, "Glowstone", 0xffff00, SHINY).asDust();
    public static Materials Graphene = new Materials(819, "Graphene", 0x808080, DULL).asDust();
    public static Materials Oilsands = new Materials(878, "Oilsands", 0x0a0a0a, NONE).asDust().addOre();
//    public static Materials Paper = new Materials(879, "Paper", 0xfafafa, PAPER).asDust(); //TODO needed?
    public static Materials RareEarth = new Materials(891, "Rare Earth", 0x808064, NONE).asDust();
    public static Materials Endstone = new Materials(808, "Endstone", 0xffffff, DULL).asDust();
    public static Materials Netherrack = new Materials(807, "Netherrack", 0xc80000, DULL).asDust();
    public static Materials Almandine = new Materials(820, "Almandine", 0xff0000, ROUGH).asDust().addOre().add(Aluminium, 2, Iron, 3, Silicon, 3, Oxygen, 12);
    public static Materials Andradite = new Materials(821, "Andradite", 0x967800, ROUGH).asDust().add(Calcium, 3, Iron, 2, Silicon, 3, Oxygen, 12);
    public static Materials Ash = new Materials(815, "Ash", 0x969696, DULL).asDust();
    public static Materials BandedIron = new Materials(917, "Banded Iron", 0x915a5a, DULL).asDust().addOre().add(Iron, 2, Oxygen, 3);
    public static Materials BrownLimonite = new Materials(930, "Brown Limonite", 0xc86400, METALLIC).asDust().addOre().add(Iron, 1, Hydrogen, 1, Oxygen, 2);
    public static Materials Calcite = new Materials(823, "Calcite", 0xfae6dc, DULL).asDust().addOre().add(Calcium, 1, Carbon, 1, Oxygen, 3);
    public static Materials Cassiterite = new Materials(824, "Cassiterite", 0xdcdcdc, METALLIC).asDust().addOre().add(Tin, 1, Oxygen, 2);
    public static Materials Chalcopyrite = new Materials(855, "Chalcopyrite", 0xa07828, DULL).asDust().addOre().add(Copper, 1, Iron, 1, Sulfur, 2);
    public static Materials Clay = new Materials(805, "Clay", 0xc8c8dc, ROUGH).asDust().add(Sodium, 2, Lithium, 1, Aluminium, 2, Silicon, 2, Water, 6);
    public static Materials Cobaltite = new Materials(827, "Cobaltite", 0x5050fa, METALLIC).asDust().addOre().add(Cobalt, 1, Arsenic, 1, Sulfur, 1);
    public static Materials Sheldonite = new Materials(828, "Sheldonite", 0xffffc8, METALLIC).asDust().addOre().add(Platinum, 3, Nickel, 1, Sulfur, 1, Palladium, 1);
    public static Materials DarkAsh = new Materials(816, "Dark Ash", 0x323232, DULL).asDust();
    public static Materials Galena = new Materials(830, "Galena", 0x643c64, DULL).asDust().addOre().add(Lead, 3, Silver, 3, Sulfur, 2);
    public static Materials Garnierite = new Materials(906, "Garnierite", 0x32c846, METALLIC).asDust().addOre().add(Nickel, 1, Oxygen, 1);
    public static Materials Grossular = new Materials(831, "Grossular", 0xc86400, ROUGH).asDust().addOre().add(Calcium, 3, Aluminium, 2, Silicon, 3, Oxygen, 12);
    public static Materials Ilmenite = new Materials(918, "Ilmenite", 0x463732, METALLIC).asDust().addOre().add(Iron, 1, Titanium, 1, Oxygen, 3);
    public static Materials Rutile = new Materials(375, "Rutile", 0xd40d5c, GEMH).asDust().add(Titanium, 1, Oxygen, 2);
    public static Materials Bauxite = new Materials(822, "Bauxite", 0xc86400, DULL).asDust().addOre().add(Rutile, 2, Aluminium, 16, Hydrogen, 10, Oxygen, 11);
    public static Materials Magnesiumchloride = new Materials(377, "Magnesiumchloride", 0xd40d5c, DULL).asDust().add(Magnesium, 1, Chlorine, 2);
    public static Materials Magnesite = new Materials(908, "Magnesite", 0xfafab4, METALLIC).asDust().addOre().add(Magnesium, 1, Carbon, 1, Oxygen, 3);
    public static Materials Magnetite = new Materials(870, "Magnetite", 0x1e1e1e, METALLIC).asDust().addOre().add(Iron, 3, Oxygen, 4);
    public static Materials Molybdenite = new Materials(942, "Molybdenite", 0x91919, METALLIC).asDust().addOre().add(Molybdenum, 1, Sulfur, 2);
    public static Materials Obsidian = new Materials(804, "Obsidian", 0x503264, DULL).asDust().add(Magnesium, 1, Iron, 1, Silicon, 2, Oxygen, 8);
    public static Materials Phosphate = new Materials(833, "Phosphate", 0xffff00, DULL).asDust().addOre().add(Phosphor, 1, Oxygen, 4);
    public static Materials Polydimethylsiloxane = new Materials(633, "Polydimethylsiloxane", 0xf5f5f5, NONE).asDust().add(Carbon, 2, Hydrogen, 6, Oxygen, 1, Silicon, 1);
    public static Materials Powellite = new Materials(883, "Powellite", 0xffff00, DULL).asDust().addOre().add(Calcium, 1, Molybdenum, 1, Oxygen, 4);
    public static Materials Pyrite = new Materials(834, "Pyrite", 0x967828, ROUGH).asDust().addOre().add(Iron, 1, Sulfur, 2);
    public static Materials Pyrolusite = new Materials(943, "Pyrolusite", 0x9696aa, DULL).asDust().addOre().add(Manganese, 1, Oxygen, 2);
    public static Materials Pyrope = new Materials(835, "Pyrope", 0x783264, METALLIC).asDust().addOre().add(Aluminium, 2, Magnesium, 3, Silicon, 3, Oxygen, 12);
    public static Materials RockSalt = new Materials(944, "Rock Salt", 0xf0c8c8, NONE).asDust().addOre().add(Potassium, 1, Chlorine, 1);
    public static Materials RawRubber = new Materials(896, "Raw Rubber", 0xccc789, DULL).asDust().add(Carbon, 5, Hydrogen, 8);
    public static Materials Salt = new Materials(817, "Salt", 0xfafafa, NONE).asDust().addOre().add(Sodium, 1, Chlorine, 1);
    public static Materials Saltpeter = new Materials(836, "Saltpeter", 0xe6e6e6, NONE).asDust().add(Potassium, 1, Nitrogen, 1, Oxygen, 3);
    public static Materials Scheelite = new Materials(910, "Scheelite", 0xc88c14, DULL).asDust(2500, 2500).addOre().add(Tungsten, 1, Calcium, 2, Oxygen, 4);
    public static Materials SiliconDioxide = new Materials(837, "Silicon Dioxide", 0xc8c8c8, QUARTZ).asDust().add(Silicon, 1, Oxygen, 2);
    public static Materials Pyrochlore = new Materials(607, "Pyrochlore", 0x2b1100, METALLIC).asDust().addOre().add(Calcium, 2, Niobium, 2, Oxygen, 7);
    public static Materials FerriteMixture = new Materials(612, "Ferrite Mixture", 0xb4b4b4, METALLIC).asDust().add(Nickel, 1, Zinc, 1, Iron, 4);
    public static Materials Massicot = new Materials(614, "Massicot", 0xffdd55, DULL).asDust().add(Lead, 1, Oxygen, 1);
    public static Materials ArsenicTrioxide = new Materials(615, "Arsenic Trioxide", 0xffffff, SHINY).asDust().add(Arsenic, 2, Oxygen, 3);
    public static Materials CobaltOxide = new Materials(616, "Cobalt Oxide", 0x668000, DULL).asDust().add(Cobalt, 1, Oxygen, 1);
    public static Materials Zincite = new Materials(617, "Zincite", 0xfffff5, DULL).asDust().add(Zinc, 1, Oxygen, 1); //TODO needed?
    public static Materials Magnesia = new Materials(621, "Magnesia", 0xffffff, DULL).asDust().add(Magnesium, 1, Oxygen, 1);
    public static Materials Quicklime = new Materials(622, "Quicklime", 0xf0f0f0, DULL).asDust().add(Calcium, 1, Oxygen, 1);
    public static Materials Potash = new Materials(623, "Potash", 0x784237, DULL).asDust().add(Potassium, 2, Oxygen, 1);
    public static Materials SodaAsh = new Materials(624, "Soda Ash", 0xdcdcff, DULL).asDust().add(Sodium, 2, Carbon, 1, Oxygen, 3);
    public static Materials Brick = new Materials(625, "Brick", 0x9b5643, ROUGH).asDust().add(Aluminium, 4, Silicon, 3, Oxygen, 12);
    public static Materials Fireclay = new Materials(626, "Fireclay", 0xada09b, ROUGH).asDust().add(Brick, 1);
    public static Materials SodiumBisulfate = new Materials(630, "Sodium Bisulfate", 0x004455, NONE).asDust().add(Sodium, 1, Hydrogen, 1, Sulfur, 1, Oxygen, 4);
    public static Materials RawStyreneButadieneRubber = new Materials(634, "Raw Styrene-Butadiene Rubber", 0x54403d, SHINY).asDust().add(Styrene, 1, Butadiene, 3);
    public static Materials PhosphorousPentoxide = new Materials(665, "Phosphorous Pentoxide", 0xdcdc00, NONE).asDust().add(Phosphor, 4, Oxygen, 10);
    public static Materials MetalMixture = new Materials(676, "Metal Mixture", 0x502d16, METALLIC).asDust();
    public static Materials SodiumHydroxide = new Materials(685, "Sodium Hydroxide", 0x003380, DULL).asDust().add(Sodium, 1, Oxygen, 1, Hydrogen, 1);
    public static Materials Spessartine = new Materials(838, "Spessartine", 0xff6464, DULL).asDust().addOre().add(Aluminium, 2, Manganese, 3, Silicon, 3, Oxygen, 12);
    public static Materials Sphalerite = new Materials(839, "Sphalerite", 0xffffff, DULL).asDust().addOre().add(Zinc, 1, Sulfur, 1);
    public static Materials Stibnite = new Materials(945, "Stibnite", 0x464646, METALLIC).asDust().addOre().add(Antimony, 2, Sulfur, 3);
    public static Materials Tetrahedrite = new Materials(840, "Tetrahedrite", 0xc82000, DULL).asDust().addOre().add(Copper, 3, Antimony, 1, Sulfur, 3, Iron, 1);
    public static Materials Tungstate = new Materials(841, "Tungstate", 0x373223, DULL).asDust().addOre().add(Tungsten, 1, Lithium, 2, Oxygen, 4);
    public static Materials Uraninite = new Materials(922, "Uraninite", 0x232323, METALLIC).asDust().addOre().add(Uranium, 1, Oxygen, 2);
    public static Materials Uvarovite = new Materials(842, "Uvarovite", 0xb4ffb4, DIAMOND).asDust().add(Calcium, 3, Chrome, 2, Silicon, 3, Oxygen, 12);
    public static Materials Wood = new Materials(809, "Wood", 0x643200, NONE).asDust().addTools(2.0F, 16, 0).add(PLATE, GEAR).add(Carbon, 1, Oxygen, 1, Hydrogen, 1);
    public static Materials Stone = new Materials(299, "Stone", 0xcdcdcd, ROUGH).asDust().add(GEAR).addTools(4.0F, 32, 1);
    public static Materials Wulfenite = new Materials(882, "Wulfenite", 0xff8000, DULL).asDust().addOre().add(Lead, 1, Molybdenum, 1, Oxygen, 4);
    public static Materials YellowLimonite = new Materials(931, "Yellow Limonite", 0xc8c800, METALLIC).asDust().addOre().add(Iron, 1, Hydrogen, 1, Oxygen, 2);
    public static Materials WoodSealed = new Materials(889, "Sealed Wood", 0x502800, NONE).asDust().addTools(3.0F, 24, 0).add(Wood, 1);
    public static Materials Blaze = new Materials(801, "Blaze", 0xffc800, NONE).asDust().add(DarkAsh, 1, Sulfur, 1/*, Magic, 1*/);
    public static Materials Flint = new Materials(802, "Flint", 0x002040, FLINT).asDust().addTools(2.5F, 64, 1).add(SiliconDioxide, 1);
    public static Materials Marble = new Materials(845, "Marble", 0xc8c8c8, NONE).asDust().add(Magnesium, 1, Calcite, 7);
    public static Materials PotassiumFeldspar = new Materials(847, "Potassium Feldspar", 0x782828, NONE).asDust().add(Potassium, 1, Aluminium, 1, Silicon, 3, Oxygen, 8);
    public static Materials Biotite = new Materials(848, "Biotite", 0x141e14, METALLIC).asDust().add(Potassium, 1, Magnesium, 3, Aluminium, 3, Fluorine, 2, Silicon, 3, Oxygen, 10);
    public static Materials GraniteBlack = new Materials(849, "Black Granite", 0x0a0a0a, ROUGH).asDust().addTools(4.0F, 64, 3).add(SiliconDioxide, 4, Biotite, 1);
    public static Materials GraniteRed = new Materials(850, "Red Granite", 0xff0080, ROUGH).asDust().addTools(4.0F, 64, 3).add(Aluminium, 2, PotassiumFeldspar, 1, Oxygen, 3);
    public static Materials VanadiumMagnetite = new Materials(923, "Vanadium Magnetite", 0x23233c, METALLIC).asDust().addOre().add(Magnetite, 1, Vanadium, 1);
    public static Materials Bastnasite = new Materials(905, "Bastnasite", 0xc86e2d, NONE).asDust().addOre().add(Cerium, 1, Carbon, 1, Fluorine, 1, Oxygen, 3);
    public static Materials Pentlandite = new Materials(909, "Pentlandite", 0xa59605, DULL).asDust().addOre().add(Nickel, 9, Sulfur, 8);
    public static Materials Spodumene = new Materials(920, "Spodumene", 0xbeaaaa, DULL).asDust().addOre().add(Lithium, 1, Aluminium, 1, Silicon, 2, Oxygen, 6);
    public static Materials Tantalite = new Materials(921, "Tantalite", 0x915028, METALLIC).asDust().addOre().add(Manganese, 1, Tantalum, 2, Oxygen, 6);
    public static Materials Lepidolite = new Materials(907, "Lepidolite", 0xf0328c, NONE).asDust().addOre().add(Potassium, 1, Lithium, 3, Aluminium, 4, Fluorine, 2, Oxygen, 10);
    public static Materials Glauconite = new Materials(933, "Glauconite", 0x82b43c, DULL).asDust().addOre().add(Potassium, 1, Magnesium, 2, Aluminium, 4, Hydrogen, 2, Oxygen, 12);
    public static Materials Bentonite = new Materials(927, "Bentonite", 0xf5d7d2, ROUGH).asDust().addOre().add(Sodium, 1, Magnesium, 6, Silicon, 12, Hydrogen, 6, Water, 5, Oxygen, 36);
    public static Materials Pitchblende = new Materials(873, "Pitchblende", 0xc8d200, DULL).asDust().addOre().add(Uraninite, 3, Thorium, 1, Lead, 1);
    public static Materials Malachite = new Materials(871, "Malachite", 0x055f05, DULL).asDust().addOre().add(Copper, 2, Carbon, 1, Hydrogen, 2, Oxygen, 5);
    public static Materials Barite = new Materials(904, "Barite", 0xe6ebff, DULL).asDust().addOre().add(Barium, 1, Sulfur, 1, Oxygen, 4);
    public static Materials Talc = new Materials(902, "Talc", 0x5ab45a, DULL).asDust().addOre().add(Magnesium, 3, Silicon, 4, Hydrogen, 2, Oxygen, 12);
    public static Materials Soapstone = new Materials(877, "Soapstone", 0x5f915f, DULL).asDust().addOre().add(Magnesium, 3, Silicon, 4, Hydrogen, 2, Oxygen, 12);
    public static Materials Concrete = new Materials(947, "Concrete", 0x646464, ROUGH).asDust(300).add(Stone, 1);
    public static Materials AntimonyTrioxide = new Materials(618, "Antimony Trioxide", 0xe6e6f0, DULL).asDust().add(Antimony, 2, Oxygen, 3);
    public static Materials CupricOxide = new Materials(619, "Cupric Oxide", 0x0f0f0f, DULL).asDust().add(Copper, 1, Oxygen, 1);
    public static Materials Ferrosilite = new Materials(620, "Ferrosilite", 0x97632a, DULL).asDust().add(Iron, 1, Silicon, 1, Oxygen, 3);
    public static Materials Redstone = new Materials(810, "Redstone", 0xc80000, ROUGH).asDust().addOre().add(Silicon, 1, Pyrite, 5, Materials.Ruby, 1, Mercury, 3);
    public static Materials Basalt = new Materials(844, "Basalt", 0x1e1414, ROUGH).asDust().add(Materials.Olivine, 1, Calcite, 3, Flint, 8, DarkAsh, 4);

    /** Gems **/
    public static Materials CertusQuartz = new Materials(516, "Certus Quartz", 0xd2d2e6, QUARTZ).asGemBasic(false).addOre().addTools(5.0F, 32, 1).add(PLATE);
    public static Materials Dilithium = new Materials(515, "Dilithium", 0xfffafa, DIAMOND).asGemBasic(true);
    public static Materials NetherQuartz = new Materials(522, "Nether Quartz", 0xe6d2d2, QUARTZ).asGemBasic(false).addTools(1.0F, 32, 1).addOre();
    public static Materials NetherStar = new Materials(506, "Nether Star", 0xffffff, NONE).asGemBasic(false).addTools(1.0F, 5120, 4);
    public static Materials Quartzite = new Materials(523, "Quartzite", 0xd2e6d2, QUARTZ).asGemBasic(false).addOre().add(Silicon, 1, Oxygen, 2);

    //Brittle Gems
    public static Materials BlueTopaz = new Materials(513, "Blue Topaz", 0x0000ff, GEMH).asGem(true).addTools(7.0F, 256, 3).add(Aluminium, 2, Silicon, 1, Fluorine, 2, Hydrogen, 2, Oxygen, 6);
    public static Materials Charcoal = new Materials(536, "Charcoal", 0x644646, NONE).asGemBasic(false).add(BLOCK).add(Carbon, 1);
    public static Materials Coal = new Materials(535, "Coal", 0x464646, ROUGH).asGemBasic(false).addOre().add(BLOCK).add(Carbon, 1);
    public static Materials Lignite = new Materials(538, "Lignite Coal", 0x644646, ROUGH).asGemBasic(false).addOre().add(BLOCK).add(Carbon, 3, Water, 1);

    public static Materials Diamond = new Materials(500, "Diamond", 0xc8ffff, DIAMOND).asGem(true).addOre().add(GEAR).addTools(8.0F, 1280, 3).add(Carbon, 1);
    public static Materials Emerald = new Materials(501, "Emerald", 0x50ff50, NONE).asGem(true).addOre().addTools(7.0F, 256, 2).add(Silver, 1, Gold, 1);
    public static Materials GreenSapphire = new Materials(504, "Green Sapphire", 0x64c882, GEMH).asGem(true).addOre().addTools(7.0F, 256, 2).add(Aluminium, 2, Oxygen, 3);
    public static Materials Lazurite = new Materials(524, "Lazurite", 0x6478ff, LAPIS).asGemBasic(false).addOre().add(Aluminium, 6, Silicon, 6, Calcium, 8, Sodium, 8);
    public static Materials Ruby = new Materials(502, "Ruby", 0xff6464, RUBY).asGem(true).addOre().addTools(7.0F, 256, 2).add(Chrome, 1, Aluminium, 2, Oxygen, 3);
    public static Materials Sapphire = new Materials(503, "Sapphire", 0x6464c8, GEMV).asGem(true).addOre().addTools(7.0F, 256, 2).add(Aluminium, 2, Oxygen, 3);
    public static Materials Sodalite = new Materials(525, "Sodalite", 0x1414ff, LAPIS).asGemBasic(false).addOre().add(Aluminium, 3, Silicon, 3, Sodium, 4, Chlorine, 1);
    public static Materials Tanzanite = new Materials(508, "Tanzanite", 0x4000c8, GEMV).asGem(true).addTools(7.0F, 256, 2).add(Calcium, 2, Aluminium, 3, Silicon, 3, Hydrogen, 1, Oxygen, 13);
    public static Materials Topaz = new Materials(507, "Topaz", 0xff8000, GEMH).asGem(true).addTools(7.0F, 256, 3).add(Aluminium, 2, Silicon, 1, Fluorine, 2, Hydrogen, 2, Oxygen, 6);
    public static Materials Glass = new Materials(890, "Glass", 0xfafafa, NONE).asGem(true).add(SiliconDioxide, 1);
    public static Materials Olivine = new Materials(505, "Olivine", 0x96ff96, RUBY).asGem(true).addOre().addTools(7.0F, 256, 2).add(Magnesium, 2, Iron, 1, SiliconDioxide, 2);
    public static Materials Opal = new Materials(510, "Opal", 0x0000ff, RUBY).asGem(true).addTools(7.0F, 256, 2).add(SiliconDioxide, 1);
    public static Materials Amethyst = new Materials(509, "Amethyst", 0xd232d2, FLINT).asGem(true).addTools(7.0F, 256, 3).add(SiliconDioxide, 4, Iron, 1);
    public static Materials Lapis = new Materials(526, "Lapis", 0x4646dc, LAPIS).asGemBasic(false).addOre().add(Lazurite, 12, Sodalite, 2, Pyrite, 1, Calcite, 1);
    public static Materials EnderPearl = new Materials(532, "Enderpearl", 0x6cdcc8, SHINY).asGemBasic(false).add(ROD, PLATE).add(Beryllium, 1, Potassium, 4, Nitrogen, 5/*, Magic, 6*/);
    public static Materials EnderEye = new Materials(533, "Endereye", 0xa0fae6, SHINY).asGemBasic(false).add(ROD, PLATE).add(EnderPearl, 1, Blaze, 1);
    public static Materials Apatite = new Materials(530, "Apatite", 0xc8c8ff, DIAMOND).asGemBasic(false).addOre().add(Calcium, 5, Phosphate, 3, Chlorine, 1);
    public static Materials Phosphorus = new Materials(534, "Phosphorus", 0xffff00, FLINT).asGemBasic(false).addOre().add(Calcium, 3, Phosphate, 2);
    public static Materials GarnetRed = new Materials(527, "Red Garnet", 0xc85050, RUBY).asGemBasic(true).addTools(7.0F, 128, 2).add(Pyrope, 3, Almandine, 5, Spessartine, 8);
    public static Materials GarnetYellow = new Materials(528, "Yellow Garnet", 0xc8c850, RUBY).asGemBasic(true).addTools(7.0F, 128, 2).add(Andradite, 5, Grossular, 8, Uvarovite, 3);
    public static Materials Monazite = new Materials(520, "Monazite", 0x324632, DIAMOND).asGemBasic(false).addOre().add(RareEarth, 1, Phosphate, 1);

    /** Metals **/
    public static Materials AnnealedCopper = new Materials(68, "Annealed Copper", 0xff7814, SHINY).asMetal(1357).add(PLATE, FOIL, ROD, WIREF).add(Copper, 1);
    public static Materials BatteryAlloy = new Materials(69, "Battery Alloy", 0x9c7ca0, DULL).asMetal(295).add(PLATE).add(Lead, 4, Antimony, 1);
    public static Materials Brass = new Materials(70, "Brass", 0xffb400, METALLIC).asMetal(1170).add(FRAME).addTools(7.0F, 96, 1).add(Zinc, 1, Copper, 3);
    public static Materials Bronze = new Materials(71, "Bronze", 0xff8000, METALLIC).asMetal(1125).add(GEAR, FRAME).addTools(6.0F, 192, 2).add(Tin, 1, Copper, 3);
    public static Materials Cupronickel = new Materials(72, "Cupronickel", 0xe39680, METALLIC).asMetal(1728).addTools(6.0F, 64, 1).add(Copper, 1, Nickel, 1);
    public static Materials Electrum = new Materials(303, "Electrum", 0xffff64, SHINY).asMetal(1330).add(PLATE, FOIL, ROD, WIREF).addTools(12.0F, 64, 2).add(Silver, 1, Gold, 1);
    public static Materials Invar = new Materials(302, "Invar", 0xb4b478, METALLIC).asMetal(1700).add(FRAME).addTools(6.0F, 256, 2).add(Iron, 2, Nickel, 1);
    public static Materials Kanthal = new Materials(312, "Kanthalm", 0xc2d2df, METALLIC).asMetal(1800, 1800).addTools(6.0F, 64, 2).add(Iron, 1, Aluminium, 1, Chrome, 1);
    public static Materials Magnalium = new Materials(313, "Magnalium", 0xc8beff, DULL).asMetal(870).addTools(6.0F, 256, 2).add(Magnesium, 1, Aluminium, 2);
    public static Materials Nichrome = new Materials(311, "Nichrome", 0xcdcef6, METALLIC).asMetal(2700, 2700).addTools(6.0F, 64, 2).add(Nickel, 4, Chrome, 1);
    public static Materials NiobiumTitanium = new Materials(360, "Niobium Titanium", 0x1d1d29, DULL).asMetal(4500, 4500).add(PLATE, FOIL, ROD, WIREF).add(Nickel, 4, Chrome, 1);
    public static Materials PigIron = new Materials(307, "Pig Iron", 0xc8b4b4, METALLIC).asMetal(1420).addTools(6.0F, 384, 2).add(Iron, 1);
    public static Materials SolderingAlloy = new Materials(314, "Soldering Alloy", 0xdcdce6, DULL).asMetal(400, 400).add(PLATE, FOIL, ROD, WIREF).add(Tin, 9, Antimony, 1);
    public static Materials StainlessSteel = new Materials(306, "Stainless Steel", 0xc8c8dc, SHINY).asMetal(1700, 1700).add(SCREW, GEAR, SGEAR, FRAME).addTools(7.0F, 480, 2).add(Iron, 6, Chrome, 1, Manganese, 1, Nickel, 1);
    public static Materials Steel = new Materials(305, "Steel", 0x808080, METALLIC).asMetal(1811, 1000).add(GEAR, SGEAR, PLATE, FOIL, WIREF, SCREW, ROD, RING, FRAME).addTools(6.0F, 512, 2).add(Iron, 50, Carbon, 1);
    public static Materials Ultimet = new Materials(344, "Ultimet", 0xb4b4e6, SHINY).asMetal(2700, 2700).add(Cobalt, 5, Chrome, 2, Nickel, 1, Molybdenum, 1);
    public static Materials VanadiumGallium = new Materials(357, "Vanadium Gallium", 0x80808c, SHINY).asMetal(4500, 4500).add(ROD).add(Vanadium, 3, Gallium, 1);
    public static Materials WroughtIron = new Materials(304, "Wrought Iron", 0xc8b4b4, METALLIC).asMetal(1811, 0).add(RING, FRAME).addTools(6.0F, 384, 2).add(Iron, 1);
    public static Materials YttriumBariumCuprate = new Materials(358, "Yttrium Barium Cuprate", 0x504046, METALLIC).asMetal(4500, 4500).add(PLATE, FOIL, ROD, WIREF).add(Yttrium, 1, Barium, 2, Copper, 3, Oxygen, 7);
    public static Materials SterlingSilver = new Materials(350, "Sterling Silver", 0xfadce1, SHINY).asMetal(1700, 1700).addTools(13.0F, 128, 2).add(Copper, 1, Silver, 4);
    public static Materials RoseGold = new Materials(351, "Rose Gold", 0xffe61e, SHINY).asMetal(1600, 1600).addTools(14.0F, 128, 2).add(Copper, 1, Gold, 4);
    public static Materials BlackBronze = new Materials(352, "Black Bronze", 0x64327d, DULL).asMetal(2000, 2000).addTools(12.0F, 256, 2).add(Gold, 1, Silver, 1, Copper, 3);
    public static Materials BismuthBronze = new Materials(353, "Bismuth Bronze", 0x647d7d, DULL).asMetal(1100, 1100).addTools(8.0F, 256, 2).add(Bismuth, 1, Zinc, 1, Copper, 3);
    public static Materials BlackSteel = new Materials(334, "Black Steel", 0x646464, METALLIC).asMetal(1200, 1200).add(FRAME).addTools(6.5F, 768, 2).add(Nickel, 1, BlackBronze, 1, Steel, 3);
    public static Materials RedSteel = new Materials(348, "Red Steel", 0x8c6464, METALLIC).asMetal(1300, 1300).addTools(7.0F, 896, 2).add(SterlingSilver, 1, BismuthBronze, 1, Steel, 2, BlackSteel, 4);
    public static Materials BlueSteel = new Materials(349, "Blue Steel", 0x64648c, METALLIC).asMetal(1400, 1400).add(FRAME).addTools(7.5F, 1024, 2).add(RoseGold, 1, Brass, 1, Steel, 2, BlackSteel, 4);
    public static Materials DamascusSteel = new Materials(335, "Damascus Steel", 0x6e6e6e, METALLIC).asMetal(2500, 1500).addTools(8.0F, 1280, 2).add(Steel, 1);
    public static Materials TungstenSteel = new Materials(316, "Tungstensteel", 0x6464a0, METALLIC).asMetal(3000, 3000).add(SCREW, GEAR, SGEAR, ROD, RING, FRAME).addTools(8.0F, 2560, 4).add(Steel, 1, Tungsten, 1);
    public static Materials RedAlloy = new Materials(308, "Red Alloy", 0xc80000, DULL).asMetal(295).add(PLATE, FOIL, ROD, WIREF).add(Copper, 1/*, Redstone, 4*/);
    public static Materials CobaltBrass = new Materials(343, "Cobalt Brass", 0xb4b4a0, METALLIC).asMetal(1500).add(GEAR).addTools(8.0F, 256, 2).add(Brass, 7, Aluminium, 1, Cobalt, 1);
    public static Materials IronMagnetic = new Materials(354, "Magnetic Iron", 0xc8c8c8, MAGNETIC).asMetal(1811).addTools(6.0F, 256, 2).add(Iron, 1);
    public static Materials SteelMagnetic = new Materials(355, "Magnetic Steel", 0x808080, MAGNETIC).asMetal(1000, 1000).addTools(6.0F, 512, 2).add(Steel, 1);
    public static Materials NeodymiumMagnetic = new Materials(356, "Magnetic Neodymium", 0x646464, MAGNETIC).asMetal(1297, 1297).addTools(7.0F, 512, 2).add(Neodymium, 1);
    public static Materials NickelZincFerrite = new Materials(613, "Nickel-Zinc Ferrite", 0x3c3c3c, ROUGH).asMetal(1500, 1500).addTools(3.0F, 32, 1).add(Nickel, 1, Zinc, 1, Iron, 4, Oxygen, 8);
    public static Materials TungstenCarbide = new Materials(370, "Tungsten Carbide", 0x330066, METALLIC).asMetal(2460, 2460).addTools(14.0F, 1280, 4).add(Tungsten, 1, Carbon, 1);
    public static Materials VanadiumSteel = new Materials(371, "Vanadium Steel", 0xc0c0c0, METALLIC).asMetal(1453, 1453).addTools(3.0F, 1920, 3).add(Vanadium, 1, Chrome, 1, Steel, 7);
    public static Materials HSSG = new Materials(372, "HSSG", 0x999900, METALLIC).asMetal(4500, 4500).add(GEAR, SGEAR, FRAME).addTools(10.0F, 4000, 3).add(TungstenSteel, 5, Chrome, 1, Molybdenum, 2, Vanadium, 1);
    public static Materials HSSE = new Materials(373, "HSSE", 0x336600, METALLIC).asMetal(5400, 5400).add(GEAR, SGEAR, FRAME).addTools(10.0F, 5120, 4).add(HSSG, 6, Cobalt, 1, Manganese, 1, Silicon, 1);
    public static Materials HSSS = new Materials(374, "HSSS", 0x660033, METALLIC).asMetal(5400, 5400).addTools(14.0F, 3000, 4).add(HSSG, 6, Iridium, 2, Osmium, 1);
    public static Materials Osmiridium = new Materials(317, "Osmiridium", 0x6464ff, METALLIC).asMetal(3333, 2500).add(FRAME).addTools(7.0F, 1600, 3);
    public static Materials Duranium = new Materials(328, "Duranium", 0xffffff, METALLIC).asMetal(295).addTools(16.0F, 5120, 5);
    public static Materials Naquadah = new Materials(324, "Naquadah", 0x323232, METALLIC).asMetal(5400, 5400).addTools(6.0F, 1280, 4).addOre();
    public static Materials NaquadahAlloy = new Materials(325, "Naquadah Alloy", 0x282828, METALLIC).asMetal(7200, 7200).addTools(8.0F, 5120, 5);
    public static Materials NaquadahEnriched = new Materials(326, "Naquadah Enriched", 0x323232, METALLIC).asMetal(4500, 4500).addOre().addTools(6.0F, 1280, 4); //TODO ORE flag added due to bee recipes, replace with OrePrefixes.mGeneratedItems
    public static Materials Naquadria = new Materials(327, "Naquadria", 0x1e1e1e, SHINY).asMetal(9000, 9000).addTools(1.0F, 512, 4).addOre();
    public static Materials Tritanium = new Materials(329, "Tritanium", 0xffffff, METALLIC).asMetal(295).add(FRAME).addTools(20.0F, 10240, 6);

    /** Solids **/
    public static Materials Plastic = new Materials(874, "Plastic", 0xc8c8c8, DULL).asSolid(295).add(PLATE).add(Carbon, 1, Hydrogen, 2);
    public static Materials Epoxid = new Materials(470, "Epoxid", 0xc88c14, DULL).asSolid(400).add(PLATE).addTools(3.0F, 32, 1).add(Carbon, 2, Hydrogen, 4, Oxygen, 1);
    public static Materials Silicone = new Materials(471, "Silicone", 0xdcdcdc, DULL).asSolid(900).add(PLATE, FOIL).addTools(3.0F, 128, 1).add(Carbon, 2, Hydrogen, 6, Oxygen, 1, Silicon, 1);
    public static Materials Polycaprolactam = new Materials(472, "Polycaprolactam", 0x323232, DULL).asSolid(500).addTools(3.0F, 32, 1).add(Carbon, 6, Hydrogen, 11, Nitrogen, 1, Oxygen, 1);
    public static Materials Polytetrafluoroethylene = new Materials(473, "Polytetrafluoroethylene", 0x646464, DULL).asSolid(1400).add(PLATE, FRAME).addTools(3.0F, 32, 1).add(Carbon, 2, Fluorine, 4);
    public static Materials Rubber = new Materials(880, "Rubber", 0x000000, SHINY).asSolid(295).add(PLATE, RING).addTools(1.5F, 32, 0).add(Carbon, 5, Hydrogen, 8);
    public static Materials PolyphenyleneSulfide = new Materials(631, "PolyphenyleneSulfide", 0xaa8800, DULL).asSolid(295).add(PLATE, FOIL).addTools(3.0F, 32, 1).add(Carbon, 6, Hydrogen, 4, Sulfur, 1);
    public static Materials Polystyrene = new Materials(636, "Polystyrene", 0xbeb4aa, DULL).asSolid(295).add(Carbon, 8, Hydrogen, 8);
    public static Materials StyreneButadieneRubber = new Materials(635, "Styrene-Butadiene Rubber", 0x211a18, SHINY).asSolid(295).addTools(3.0F, 128, 1).add(Styrene, 1, Butadiene, 3);
    public static Materials PolyvinylChloride = new Materials(649, "Polyvinyl Chloride", 0xd7e6e6, NONE).asSolid(295).add(PLATE, FOIL).addTools(3.0F, 32, 1).add(Carbon, 2, Hydrogen, 3, Chlorine, 1);
    public static Materials GalliumArsenide = new Materials(980, "Gallium Arsenide", 0xa0a0a0, DULL).asSolid(295, 1200).add(Arsenic, 1, Gallium, 1);
    public static Materials EpoxidFiberReinforced = new Materials(610, "Fiber-Reinforced Epoxy Resin", 0xa07010, DULL).asSolid(400).addTools(3.0F, 64, 1).add(Epoxid, 1);

    public static void init() {
//        for (Material material : generated) {
//            if (material == Blaze) {
//                material.handleMaterial = "blaze";
//            } /*else if (aMaterial.contains(SubTag.MAGICAL) && aMaterial.contains(SubTag.CRYSTAL) && Loader.isModLoaded(MOD_ID_TC)) {
//                    aMaterial.mHandleMaterial = Thaumium;
//                }*/ else if (material.getMass() > Element.Tc.getMass() * 2) {
//                material.handleMaterial = Tungstensteel.;
//            } else if (material.getMass() > Element.Tc.getMass()) {
//                material.handleMaterial = Steel;
//            } else {
//                material.handleMaterial = Wood;
//            }
//        }
    }

    private Materials(int id, String name, int rgb, MaterialSet set, Element element) {
        this(id, name, rgb, set);
        this.element = element;
    }

    private Materials(int id, String displayName, int rgb, MaterialSet set) {
        this.id = id;
        this.displayName = displayName;
        this.name = displayName.toLowerCase().replaceAll("-", "").replaceAll(" ", "");
        this.rgb = rgb;
        this.set = set;
        generated[id] = this;
        generatedMap.put(name, this);
    }

    public static Materials get(String name) {
        return generatedMap.get(name);
    }

    private Materials asDust(int... temps) {
        add(DUST);
        if (temps.length >= 1 && temps[0] > 0) {
            meltingPoint = temps[0];
            asFluid();
        }
        return this;
    }

    private Materials asSolid(int... temps) {
        asDust(temps);
        add(INGOT);
        if (temps.length >= 2 && temps[1] > 0) {
            needsBlastFurnace = temps[1] >= 1000;
            blastFurnaceTemp = temps[1];
            if (temps[1] > 1750) add(HINGOT);
        }
        return this;
    }

    private Materials asMetal(int... temps) {
        asSolid(temps);
        add(METAL);
        return this;
    }

    public Materials asGemBasic(boolean transparent) {
        asDust();
        add(BGEM);
        if (transparent) {
            this.transparent = true;
            add(PLATE);
        }
        return this;
    }

    public Materials asGem(boolean transparent) {
        asGemBasic(transparent);
        add(GEM);
        return this;
    }

    private Materials asFluid(int... fuelPower) {
        add(FLUID);
        if (fuelPower.length >= 1) this.fuelPower = fuelPower[0];
        return this;
    }

    private Materials asGas(int... fuelPower) {
        asFluid(fuelPower);
        add(GAS);
        return this;
    }

    private Materials asPlasma(int... fuelPower) {
        asFluid(fuelPower);
        add(GAS, PLASMA);
        return this;
    }

    private Materials addOre() {
        add(CRUSHED, CRUSHEDC, CRUSHEDP);
        return this;
    }

    private Materials addTools(float toolSpeed, int toolDurability, int toolQuality) {
        if (hasFlag(INGOT)) {
            add(TOOL, PLATE, ROD, BOLT);
        } else if (hasFlag(BGEM)) {
            add(TOOL, ROD);
        } /*else if (this == Material.Stone || this == Material.Wood) {
            add(TOOL);
        }*/
        this.toolSpeed = toolSpeed;
        this.toolDurability = toolDurability;
        this.toolQuality = toolQuality;
        return this;
    }

    public boolean hasFlag(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof ItemFlag) {
                if (!Utils.hasFlag(itemMask, flag.getMask())) return false;
            } else if (flag instanceof RecipeFlag) {
                if (!Utils.hasFlag(recipeMask, flag.getMask())) return false;
            }
        }
        return true;
    }

    public Materials add(IMaterialFlag... flags) {
        for (IMaterialFlag flag : flags) {
            if (flag instanceof ItemFlag) {
                itemMask = Utils.addFlag(itemMask, flag.getMask());
            } else if (flag instanceof RecipeFlag) {
                recipeMask = Utils.addFlag(recipeMask, flag.getMask());
            }
            flag.add(this);
        }
        return this;
    }

    public Materials add(Object... objects) {
        if (objects.length % 2 == 0) {
            for (int i = 0; i < objects.length; i += 2) {
                processInto.add(new MaterialStack((Materials) objects[i], (int) objects[i + 1]));
            }
        }
        return this;
    }

    public Materials add(Materials... mats) {
        for (Materials mat : mats) {
            byProducts.add(mat);
        }
        return this;
    }

    public static Materials[] getMatsFor(IMaterialFlag... aFlags) {
        ArrayList<Materials> aList = new ArrayList<>();
        for (IMaterialFlag aFlag : aFlags) {
            for (Materials aMat : aFlag.getMats()) {
                if (!aList.contains(aMat)) {
                    aList.add(aMat);
                }
            }
        }
        return aList.toArray(new Materials[0]);
    }

    /** Basic Getters**/
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (!hasLocName) {
            displayName = I18n.format("material." + getName() + ".name");
        }
        return displayName;
    }

    public int getRGB() {
        return rgb;
    }

    public MaterialSet getSet() {
        return set;
    }

    public int getItemMask() {
        return itemMask;
    }

    public int getRecipeMask() {
        return recipeMask;
    }

    public int getMass() {
        if (mass == 0) {
            if (element != null) return element.getMass();
            if (processInto.size() <= 0) return Tc.getMass();
            for (MaterialStack tMaterial : processInto) {
                mass += tMaterial.amount * tMaterial.material.getMass();
            }
        }
        return mass;
    }

    /** Element Getters **/
    public Element getElement() {
        return element;
    }

    /** Solid Getters **/
    public int getMeltingPoint() {
        return meltingPoint;
    }

    public int getBlastFurnaceTemp() {
        return blastFurnaceTemp;
    }

    public boolean needsBlastFurnace() {
        return needsBlastFurnace;
    }

    /** Gem Getters **/
    public boolean transparent() {
        return transparent;
    }

    /** Tool Getters **/
    public float getToolSpeed() {
        return toolSpeed;
    }

    public int getToolDurability() {
        return toolDurability;
    }

    public int getToolQuality() {
        return toolQuality;
    }

    /** Processing Getters **/
    public ArrayList<MaterialStack> getProcessInto() {
        return processInto;
    }

    public ItemStack getChunk(int amount) {
        return MetaItem.get(Prefix.CHUNK, this, amount);
    }

    public ItemStack getCrushed(int amount) {
        return MetaItem.get(Prefix.CRUSHED, this, amount);
    }

    public ItemStack getCrushedC(int amount) {
        return MetaItem.get(Prefix.CRUSHEDCENTRIFUGED, this, amount);
    }

    public ItemStack getCrushedP(int amount) {
        return MetaItem.get(Prefix.CRUSHEDPURIFIED, this, amount);
    }

    public ItemStack getDust(int amount) {
        return MetaItem.get(Prefix.DUST, this, amount);
    }

    public ItemStack getDustS(int amount) {
        return MetaItem.get(Prefix.DUSTSMALL, this, amount);
    }

    public ItemStack getDustT(int amount) {
        return MetaItem.get(Prefix.DUSTTINY, this, amount);
    }

    public ItemStack getNugget(int amount) {
        return MetaItem.get(Prefix.NUGGET, this, amount);
    }

    public ItemStack getIngot(int amount) {
        return MetaItem.get(Prefix.INGOT, this, amount);
    }

    public ItemStack getIngotH(int amount) {
        return MetaItem.get(Prefix.INGOTHOT, this, amount);
    }

    public ItemStack getPlate(int amount) {
        return MetaItem.get(Prefix.PLATE, this, amount);
    }

    public ItemStack getPlateD(int amount) {
        return MetaItem.get(Prefix.PLATEDENSE, this, amount);
    }

    public ItemStack getGem(int amount) {
        return MetaItem.get(Prefix.GEM, this, amount);
    }

    public ItemStack getGemChipped(int amount) {
        return MetaItem.get(Prefix.GEMCHIPPED, this, amount);
    }

    public ItemStack getGemFlawed(int amount) {
        return MetaItem.get(Prefix.GEMFLAWED, this, amount);
    }

    public ItemStack getGemFlawless(int amount) {
        return MetaItem.get(Prefix.GEMFLAWLESS, this, amount);
    }

    public ItemStack getGemExquisite(int amount) {
        return MetaItem.get(Prefix.GEMEXQUISITE, this, amount);
    }

    public ItemStack getFoil(int amount) {
        return MetaItem.get(Prefix.FOIL, this, amount);
    }

    public ItemStack getRod(int amount) {
        return MetaItem.get(Prefix.ROD, this, amount);
    }

    public ItemStack getBolt(int amount) {
        return MetaItem.get(Prefix.BOLT, this, amount);
    }

    public ItemStack getScrew(int amount) {
        return MetaItem.get(Prefix.SCREW, this, amount);
    }

    public ItemStack getRing(int amount) {
        return MetaItem.get(Prefix.RING, this, amount);
    }

    public ItemStack getSpring(int amount) {
        return MetaItem.get(Prefix.SPRING, this, amount);
    }

    public ItemStack getWireF(int amount) {
        return MetaItem.get(Prefix.WIREFINE, this, amount);
    }

    public ItemStack getRotor(int amount) {
        return MetaItem.get(Prefix.ROTOR, this, amount);
    }

    public ItemStack getGear(int amount) {
        return MetaItem.get(Prefix.GEAR, this, amount);
    }

    public ItemStack getGearS(int amount) {
        return MetaItem.get(Prefix.GEARSMALL, this, amount);
    }

    public ItemStack getLens(int amount) {
        return MetaItem.get(Prefix.LENS, this, amount);
    }

    public ItemStack getCell(int amount) {
        return MetaItem.get(Prefix.CELL, this, amount);
    }

    public ItemStack getCellP(int amount) {
        return MetaItem.get(Prefix.CELLPLASMA, this, amount);
    }
}
