package muramasa.gregtech.api.data;

import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.common.fluid.GTFluid;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Collection;
import java.util.LinkedHashMap;

import static muramasa.gregtech.api.enums.Element.*;
import static muramasa.gregtech.api.materials.ItemFlag.*;
import static muramasa.gregtech.api.materials.RecipeFlag.*;
import static muramasa.gregtech.api.materials.MaterialSet.*;

public class Materials {

    public static LinkedHashMap<String, Material> MATERIAL_LOOKUP = new LinkedHashMap<>();

    public static Material Aluminium = new Material("Aluminium", 0x80c8f0, DULL, Al).asMetal(933, 1700, RING, FOIL, SGEAR, GEAR, FRAME, ORE).addTools(10.0F, 128, 2);
    public static Material Beryllium = new Material("Beryllium", 0x64b464, METALLIC, Be).asMetal(1560, 0, ORE).addTools(14.0F, 64, 2);
    public static Material Bismuth = new Material("Bismuth", 0x64a0a0, METALLIC, Bi).asMetal(544, 0).addTools(6.0F, 64, 1);
    public static Material Carbon = new Material("Carbon", 0x141414, DULL, C).asSolid().addTools(1.0F, 64, 2);
    public static Material Chrome = new Material("Chrome", 0xffe6e6, SHINY, Cr).asMetal(2180, 1700, SCREW, RING, PLATE, ROTOR, ORE).addTools(11.0F, 256, 3);
    public static Material Cobalt = new Material("Cobalt", 0x5050fa, METALLIC, Co).asMetal(1768, 0).addTools(8.0F, 512, 3);
    public static Material Gold = new Material( "Gold", 0xffff1e, SHINY, Au).asMetal(1337, 0, FOIL, ROD, WIREF, GEAR, BLOCK, ORE).addTools(12.0F, 64, 2);
    public static Material Iridium = new Material("Iridium", 0xf0f0f5, DULL, Ir).asMetal(2719, 2719, FRAME, ORE).addTools(6.0F, 2560, 3);
    public static Material Iron = new Material("Iron", 0xc8c8c8, METALLIC, Fe).asMetal(1811, 0, RING, GEAR, FRAME, BLOCK).asPlasma().addTools(6.0F, 256, 2);
    public static Material Lanthanum = new Material("Lanthanum", 0xffffff, METALLIC, La).asSolid(1193, 1193);
    public static Material Lead = new Material("Lead", 0x8c648c, DULL, Pb).asMetal(600, 0, PLATE, FOIL, ROD, WIREF, DPLATE, ORE).addTools(8.0F, 64, 1);
    public static Material Manganese = new Material("Manganese", 0xfafafa, DULL, Mn).asMetal(1519, 0, FOIL, ORE).addTools(7.0F, 512, 2);
    public static Material Molybdenum = new Material("Molybdenum", 0xb4b4dc, SHINY, Mo).asMetal(2896, 0, ORE).addTools(7.0F, 512, 2);
    public static Material Neodymium = new Material("Neodymium", 0x646464, METALLIC, Nd).asMetal(1297, 1297, ORE).addTools(7.0F, 512, 2);
    public static Material Neutronium = new Material("Neutronium", 0xfafafa, DULL, Nt).asMetal(10000, 10000, SCREW, RING, GEAR, SGEAR, FRAME).addTools(24.0F, 655360, 6);
    public static Material Nickel = new Material("Nickel", 0xc8c8fa, METALLIC, Ni).asMetal(1728, 0, ORE).asPlasma().addTools(6.0F, 64, 2);
    public static Material Osmium = new Material("Osmium", 0x3232ff, METALLIC, Os).asMetal(3306, 3306, SCREW, RING, PLATE, FOIL, ROD, WIREF, ORE).addTools(16.0F, 1280, 4);
    public static Material Palladium = new Material("Palladium", 0x808080, SHINY, Pd).asMetal(1828, 1828, ORE).addTools(8.0F, 512, 2);
    public static Material Platinum = new Material("Platinum", 0xffffc8, SHINY, Pt).asMetal(2041, 0, PLATE, FOIL, ROD, WIREF, ORE).addTools(12.0F, 64, 2);
    public static Material Plutonium = new Material("Plutonium 239", 0xf03232, METALLIC, Pu).asMetal(912, 0, ORE).addTools(6.0F, 512, 3);
    public static Material Plutonium241 = new Material("Plutonium 241", 0xfa4646, SHINY, Pu241).asMetal(912, 0).addTools(6.0F, 512, 3);
    public static Material Silver = new Material("Silver", 0xdcdcff, SHINY, Ag).asMetal(1234, 0, ORE).addTools(10.0F, 64, 2);
    public static Material Thorium = new Material("Thorium", 0x001e00, SHINY, Th).asMetal(2115, 0, ORE).addTools(6.0F, 512, 2);
    public static Material Titanium = new Material("Titanium", 0xdca0f0, METALLIC).asMetal(1941, 1940, ROD, SPRING);
    public static Material Tungsten = new Material("Tungsten", 0x323232, METALLIC, W).asMetal(3695, 3000, FOIL).addTools(7.0F, 2560, 3);
    public static Material Uranium = new Material("Uranium 238", 0x32f032, METALLIC, U).asMetal(1405, 0).addTools(6.0F, 512, 3);
    public static Material Uranium235 = new Material("Uranium 235", 0x46fa46, METALLIC, U235).asMetal(1405, 0).addTools(6.0F, 512, 3);
    public static Material Graphite = new Material("Graphite", 0x808080, DULL).asDust(ORE).addTools(5.0F, 32, 2);
    public static Material Americium = new Material("Americium", 0xc8c8c8, METALLIC, Am).asMetal(1149, 0, PLATE, ROD);
    public static Material Antimony = new Material("Antimony", 0xdcdcf0, SHINY, Sb).asMetal(1449, 0);
    public static Material Argon = new Material("Argon", 0xff00f0, NONE, Ar).asGas();
    public static Material Arsenic = new Material("Arsenic", 0xffffff, DULL, As).asSolid();
    public static Material Barium = new Material("Barium", 0xffffff, METALLIC, Ba).asDust(1000);
    public static Material Boron = new Material("Boron", 0xfafafa, DULL, B).asDust(2349);
    public static Material Caesium = new Material("Caesium", 0xffffff, METALLIC, Cs).asMetal(2349, 0);
    public static Material Calcium = new Material("Calcium", 0xfff5f5, METALLIC, Ca).asDust(1115);
    public static Material Cadmium = new Material("Cadmium", 0x32323c, SHINY, Cd).asDust(594);
    public static Material Cerium = new Material("Cerium", 0xffffff, METALLIC, Ce).asSolid(1068, 1068);
    public static Material Chlorine = new Material("Chlorine", 0xffffff, NONE, Cr).asGas();
    public static Material Copper = new Material("Copper", 0xff6400, SHINY, Cu).asMetal(1357, 0, PLATE, DPLATE, ROD, FOIL, WIREF, GEAR, ORE);
    public static Material Deuterium = new Material("Deuterium", 0xffff00, NONE, D).asGas();
    public static Material Dysprosium = new Material("Dysprosium", 0xffffff, METALLIC, D).asMetal(1680, 1680);
    public static Material Europium = new Material("Europium", 0xffffff, METALLIC, Eu).asMetal(1099, 1099, PLATE, ROD);
    public static Material Fluorine = new Material("Fluorine", 0xffffff, NONE, F).asFluid();
    public static Material Gallium = new Material("Gallium", 0xdcdcff, SHINY, Ga).asMetal(302, 0, PLATE);
    public static Material Hydrogen = new Material("Hydrogen", 0x0000ff, NONE, H).asGas();
    public static Material Helium = new Material("Helium", 0xffff00, NONE, He).asPlasma();
    public static Material Helium3 = new Material("Helium-3", 0xffffff, NONE, He_3).asGas();
    public static Material Indium = new Material("Indium", 0x400080, METALLIC, In).asSolid(429, 0);
    public static Material Lithium = new Material("Lithium", 0xe1dcff, DULL, Li).asSolid(454, 0, ORE);
    public static Material Lutetium = new Material("Lutetium", 0xffffff, DULL, Lu).asMetal(1925, 1925);
    public static Material Magnesium = new Material("Magnesium", 0xffc8c8, METALLIC, Mg).asMetal(923, 0);
    public static Material Mercury = new Material("Mercury", 0xffdcdc, SHINY, Hg).asFluid();
    public static Material Niobium = new Material("Niobium", 0xbeb4c8, METALLIC, Nb).asMetal(2750, 2750);
    public static Material Nitrogen = new Material("Nitrogen", 0x0096c8, NONE, N).asPlasma();
    public static Material Oxygen = new Material("Oxygen", 0x0064c8, NONE, O).asPlasma();
    public static Material Phosphor = new Material("Phosphor", 0xffff00, DULL, P).asDust(317);
    public static Material Potassium = new Material("Potassium", 0xfafafa, METALLIC, K).asSolid(336, 0);
    public static Material Radon = new Material("Radon", 0xff00ff, NONE, Rn).asGas();
    public static Material Silicon = new Material("Silicon", 0x3c3c50, METALLIC, Si).asMetal(1687, 1687, PLATE, FOIL, BLOCK);
    public static Material Sodium = new Material("Sodium", 0x000096, METALLIC, Na).asDust(370);
    public static Material Sulfur = new Material("Sulfur", 0xc8c800, DULL, S).asDust(388, ORE).asPlasma();
    public static Material Tantalum = new Material("Tantalum", 0xffffff, METALLIC, Ta).asSolid(3290, 0);
    public static Material Tin = new Material("Tin", 0xdcdcdc, DULL, Sn).asMetal(505, 505, PLATE, ROD, BOLT, SCREW, RING, GEAR, FOIL, WIREF, FRAME, ORE);
    public static Material Tritium = new Material("Tritium", 0xff0000, METALLIC, T).asFluid();
    public static Material Vanadium = new Material("Vanadium", 0x323232, METALLIC, V).asMetal(2183, 2183);
    public static Material Yttrium = new Material("Yttrium", 0xdcfadc, METALLIC, Y).asMetal(1799, 1799);
    public static Material Zinc = new Material("Zinc", 0xfaf0f0, METALLIC, Zn).asMetal(692, 0, PLATE, FOIL, ORE);

    /** Gases **/
    public static Material WoodGas = new Material("Wood Gas", 0xdecd87, NONE).asGas(24);
    public static Material Methane = new Material("Methane", 0xffffff, NONE).asGas(104).add(Carbon, 1, Hydrogen, 4);
    public static Material CarbonDioxide = new Material("Carbon Dioxide", 0xa9d0f5, NONE).asGas().add(Carbon, 1, Oxygen, 2);
    public static Material NobleGases = new Material("Noble Gases", 0xc9e3fc, NONE).asGas()/*.setTemp(79, 0)*/.add(CarbonDioxide, 21, Helium, 9, Methane, 3, Deuterium, 1);
    public static Material Air = new Material("Air", 0xc9e3fc, NONE).asGas().add(Nitrogen, 40, Oxygen, 11, Argon, 1, NobleGases, 1);
    public static Material NitrogenDioxide = new Material("Nitrogen Dioxide", 0x64afff, NONE).asGas().add(Nitrogen, 1, Oxygen, 2);
    public static Material NaturalGas = new Material("Natural Gas", 0xffffff, NONE).asGas(15);
    public static Material SulfuricGas = new Material("Sulfuric Gas", 0xffffff, NONE).asGas(20);
    public static Material RefineryGas = new Material("Refinery Gas", 0xffffff, NONE).asGas(128);
    public static Material LPG = new Material("LPG", 0xffff00, NONE).asGas(256);
    public static Material Ethane = new Material("Ethane", 0xc8c8ff, NONE).asGas(168).add(Carbon, 2, Hydrogen, 6);
    public static Material Propane = new Material("Propane", 0xfae250, NONE).asGas(232).add(Carbon, 2, Hydrogen, 6);
    public static Material Butane = new Material("Butane", 0xb6371e, NONE).asGas(296).add(Carbon, 4, Hydrogen, 10);
    public static Material Butene = new Material("Butene", 0xcf5005, NONE).asGas(256).add(Carbon, 4, Hydrogen, 8);
    public static Material Butadiene = new Material("Butadiene", 0xe86900, NONE).asGas(206).add(Carbon, 4, Hydrogen, 6);
    public static Material VinylChloride = new Material("Vinyl Chloride", 0xfff0f0, NONE).asGas().add(Carbon, 2, Hydrogen, 3, Chlorine, 1);
    public static Material SulfurDioxide = new Material("Sulfur Dioxide", 0xc8c819, NONE).asGas().add(Sulfur, 1, Oxygen, 2);
    public static Material SulfurTrioxide = new Material("Sulfur Trioxide", 0xa0a014, NONE).asGas()/*.setTemp(344, 1)*/.add(Sulfur, 1, Oxygen, 3);
    public static Material Dimethylamine = new Material("Dimethylamine", 0x554469, NONE).asGas().add(Carbon, 2, Hydrogen, 7, Nitrogen, 1);
    public static Material DinitrogenTetroxide = new Material("Dinitrogen Tetroxide", 0x004184, NONE).asGas().add(Nitrogen, 2, Oxygen, 4);
    public static Material NitricOxide = new Material("Nitric Oxide", 0x7dc8f0, NONE).asGas().add(Nitrogen, 1, Oxygen, 1);
    public static Material Ammonia = new Material("Ammonia", 0x3f3480, NONE).asGas().add(Nitrogen, 1, Hydrogen, 3);
    public static Material Chloromethane = new Material("Chloromethane", 0xc82ca0, NONE).asGas().add(Carbon, 1, Hydrogen, 3, Chlorine, 1);
    public static Material Tetrafluoroethylene = new Material("Tetrafluoroethylene", 0x7d7d7d, NONE).asGas().add(Carbon, 2, Fluorine, 4);
    public static Material CarbonMonoxide = new Material("Carbon Monoxide", 0x0e4880, NONE).asGas(24).add(Carbon, 1, Oxygen, 1);
    public static Material Ethylene = new Material("Ethylene", 0xe1e1e1, NONE).asGas(128).add(Carbon, 2, Hydrogen, 4);
    public static Material Propene = new Material("Propene", 0xffdd55, NONE).asGas(192).add(Carbon, 3, Hydrogen, 6);
    public static Material Ethenone = new Material("Ethenone", 0x141446, NONE).asGas().add(Carbon, 2, Hydrogen, 2, Oxygen, 1);
    public static Material HydricSulfide = new Material("Hydric Sulfide", 0xffffff, NONE).asGas().add(Hydrogen, 2, Sulfur, 1);

    /** Fluids **/
    public static Material Lava = new Material("Lava", 0xff4000, NONE).asFluid();
    public static Material UUAmplifier = new Material("UU-Amplifier", 0x600080, NONE).asFluid();
    public static Material UUMatter = new Material("UU-Matter", 0x8000c4, NONE).asFluid();
    public static Material Antimatter = new Material("Antimatter", 0x8000c4, NONE).asFluid();
    public static Material CharcoalByproducts = new Material("Charcoal Byproducts", 0x784421, NONE).asFluid(); //TODO rename
    public static Material FermentedBiomass = new Material("Fermented Biomass", 0x445500, NONE).asFluid(); //TODO needed?
    public static Material Glue = new Material("Glue", 0xc8c400, NONE).asFluid();
    public static Material Honey = new Material("Honey", 0xd2c800, NONE).asFluid();
    public static Material Lubricant = new Material("Lubricant", 0xffc400, NONE).asFluid();
    public static Material WoodTar = new Material("Wood Tar", 0x28170b, NONE).asFluid();
    public static Material WoodVinegar = new Material("Wood Vinegar", 0xd45500, NONE).asFluid();
    public static Material LiquidAir = new Material("Liquid Air", 0xa9d0f5, NONE).asFluid()/*.setTemp(79, 0)*/.add(Nitrogen, 40, Oxygen, 11, Argon, 1, NobleGases, 1); //TODO Rrename to liquid oxygen
    public static Material Water = new Material("Water", 0x0000ff, NONE).asFluid().add(Hydrogen, 2, Oxygen, 1);
    public static Material DistilledWater = new Material("Distilled Water", 0x5C5CFF, NONE).asFluid().add(Hydrogen, 2, Oxygen, 1);
    public static Material Glyceryl = new Material("Glyceryl", 0x009696, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Nitrogen, 3, Oxygen, 9);
    public static Material Titaniumtetrachloride = new Material("Titaniumtetrachloride", 0xd40d5c, NONE).asFluid().add(Titanium, 1, Chlorine, 4);
    public static Material SaltWater = new Material("Salt Water", 0x0000c8, NONE).asFluid(); //TODO needed?
    public static Material SodiumPersulfate = new Material("Sodium Persulfate", 0xffffff, NONE).asFluid().add(Sodium, 2, Sulfur, 2, Oxygen, 8);
    public static Material DilutedHydrochloricAcid = new Material("Diluted Hydrochloric Acid", 0x99a7a3, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1);
    public static Material GrowthMediumRaw = new Material("Raw Growth Medium", 0xd38d5f, NONE).asFluid(); //TODO needed?
    public static Material GrowthMediumSterilized = new Material("Growth Medium Sterilized", 0xdeaa87, NONE).asFluid(); //TODO needed?
    public static Material NitrationMixture = new Material("Nitration Mixture", 0xe6e2ab, NONE).asFluid();
    public static Material Dichlorobenzene = new Material("Dichlorobenzene", 0x004455, NONE).asFluid().add(Carbon, 6, Hydrogen, 4, Chlorine, 2);
    public static Material Styrene = new Material("Styrene", 0xd2c8be, NONE).asFluid().add(Carbon, 8, Hydrogen, 8);
    public static Material Isoprene = new Material("Isoprene", 0x141414, NONE).asFluid().add(Carbon, 8, Hydrogen, 8);
    public static Material Tetranitromethane = new Material("Tetranitromethane", 0x0f2828, NONE).asFluid().add(Carbon, 1, Nitrogen, 4, Oxygen, 8);
    public static Material Epichlorohydrin = new Material("Epichlorohydrin", 0x501d05, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Chlorine, 1, Oxygen, 1);
    public static Material NitricAcid = new Material("Nitric Acid", 0xe6e2ab, NONE).asFluid().add(Hydrogen, 1, Nitrogen, 1, Oxygen, 3);
    public static Material Dimethylhydrazine = new Material("Dimethylhydrazine", 0x000055, NONE).asFluid().add(Carbon, 2, Hydrogen, 8, Nitrogen, 2);
    public static Material Chloramine = new Material("Chloramine", 0x3f9f80, NONE).asFluid().add(Nitrogen, 1, Hydrogen, 2, Chlorine, 1);
    public static Material Dimethyldichlorosilane = new Material("Dimethyldichlorosilane", 0x441650, NONE).asFluid().add(Carbon, 2, Hydrogen, 6, Chlorine, 2, Silicon, 1);
    public static Material HydrofluoricAcid = new Material("Hydrofluoric Acid", 0x0088aa, NONE).asFluid().add(Hydrogen, 1, Fluorine, 1);
    public static Material Chloroform = new Material("Chloroform", 0x892ca0, NONE).asFluid().add(Carbon, 1, Hydrogen, 1, Chlorine, 3);
    public static Material BisphenolA = new Material("Bisphenol A", 0xd4b300, NONE).asFluid().add(Carbon, 15, Hydrogen, 16, Oxygen, 2);
    public static Material AceticAcid = new Material("Acetic Acid", 0xc8b4a0, NONE).asFluid().add(Carbon, 2, Hydrogen, 4, Oxygen, 2);
    public static Material CalciumAcetateSolution = new Material("Calcium Acetate Solution", 0xdcc8b4, RUBY).asFluid().add(Calcium, 1, Carbon, 2, Oxygen, 4, Hydrogen, 6);
    public static Material Acetone = new Material("Acetone", 0xafafaf, NONE).asFluid().add(Carbon, 3, Hydrogen, 6, Oxygen, 1);
    public static Material Methanol = new Material("Methanol", 0xaa8800, NONE).asFluid(84).add(Carbon, 1, Hydrogen, 4, Oxygen, 1);
    public static Material VinylAcetate = new Material("Vinyl Acetate", 0xffb380, NONE).asFluid().add(Carbon, 4, Hydrogen, 6, Oxygen, 2);
    public static Material PolyvinylAcetate = new Material("Polyvinyl Acetate", 0xff9955, NONE).asFluid().add(Carbon, 4, Hydrogen, 6, Oxygen, 2);
    public static Material MethylAcetate = new Material("Methyl Acetate", 0xeec6af, NONE).asFluid().add(Carbon, 3, Hydrogen, 6, Oxygen, 2);
    public static Material AllylChloride = new Material("Allyl Chloride", 0x87deaa, NONE).asFluid().add(Carbon, 3, Hydrogen, 5, Chlorine, 1);
    public static Material HydrochloricAcid = new Material("Hydrochloric Acid", 0x6f8a91, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1);
    public static Material HypochlorousAcid = new Material("Hypochlorous Acid", 0x6f8a91, NONE).asFluid().add(Hydrogen, 1, Chlorine, 1, Oxygen, 1);
    public static Material Cumene = new Material("Cumene", 0x552200, NONE).asFluid().add(Carbon, 9, Hydrogen, 12);
    public static Material PhosphoricAcid = new Material("Phosphoric Acid", 0xdcdc00, NONE).asFluid().add(Hydrogen, 3, Phosphor, 1, Oxygen, 4);
    //    public static Materials Coolant = new Materials(690, "Coolant", 220, 220, 0, lightBlue, NONE).asFluid(); //TODO remove?
    public static Material SulfuricAcid = new Material("Sulfuric Acid", 0xff8000, NONE).asFluid().add(Hydrogen, 2, Sulfur, 1, Oxygen, 4);
    public static Material DilutedSulfuricAcid = new Material("Diluted Sulfuric Acid", 0xc07820, NONE).asFluid().add(SulfuricAcid, 1);
    public static Material Benzene = new Material("Benzene", 0x1a1a1a, NONE).asFluid(288).add(Carbon, 6, Hydrogen, 6);
    public static Material Phenol = new Material("Phenol", 0x784421, NONE).asFluid(288).add(Carbon, 6, Hydrogen, 6, Oxygen, 1);
    public static Material Toluene = new Material("Toluene", 0x501d05, NONE).asFluid(328).add(Carbon, 7, Hydrogen, 8);
    public static Material SulfuricNaphtha = new Material("Sulfuric Naphtha", 0xffff00, NONE).asFluid(32);
    public static Material Naphtha = new Material("Naphtha", 0xffff00, NONE).asFluid(256);
    //TODO
    public static Material DrillingFluid = new Material("Drilling Fluid", 0xffffff, NONE).asFluid();
    public static Material BlueVitriol = new Material("Blue Vitriol Water Solution", 0xffffff, NONE).asFluid();
    public static Material IndiumConcentrate = new Material("Indium Concentrate", 0xffffff, NONE).asFluid();
    public static Material NickelSulfate = new Material("Nickel Sulfate", 0xffffff, NONE).asFluid();
    public static Material RocketFuel = new Material("Rocket Fuel", 0xffffff, NONE).asFluid();
    public static Material LeadZincSolution = new Material("Lead-Zinc Solution", 0xffffff, NONE).asFluid();

    /** Fuels **/
    public static Material Diesel = new Material("Diesel", 0xffff00, NONE).asFluid(128);
    public static Material NitroFuel = new Material("Cetane-Boosted Diesel", 0xc8ff00, NONE).asFluid(512);
    public static Material BioDiesel = new Material("Bio Diesel", 0xff8000, NONE).asFluid(192);
    public static Material Biomass = new Material("Biomass", 0x00ff00, NONE).asFluid(8);
    public static Material Ethanol = new Material("Ethanol", 0xff8000, NONE).asFluid(148).add(Carbon, 2, Hydrogen, 6, Oxygen, 1);
    public static Material Creosote = new Material("Creosote", 0x804000, NONE).asFluid(8);
    public static Material FishOil = new Material("Fish Oil", 0xffc400, NONE).asFluid(2);
    public static Material Oil = new Material("Oil", 0x0a0a0a, NONE).asFluid(16);
    public static Material SeedOil = new Material("Seed Oil", 0xc4ff00, NONE).asFluid(2);
    //public static Materials SeedOilHemp = new Materials(722, "Hemp Seed Oil", 196, 255, 0, lime, NONE).asSemi(2);
    //public static Materials SeedOilLin = new Materials(723, "Lin Seed Oil", 196, 255, 0, lime, NONE).asSemi(2);
    public static Material OilExtraHeavy = new Material("Extra Heavy Oil", 0x0a0a0a, NONE).asFluid(40);
    public static Material OilHeavy = new Material("Heavy Oil", 0x0a0a0a, NONE).asFluid(32);
    public static Material OilMedium = new Material("Raw Oil", 0x0a0a0a, NONE).asFluid(24);
    public static Material OilLight = new Material("Light Oil", 0x0a0a0a, NONE).asFluid(16);
    public static Material SulfuricLightFuel = new Material("Sulfuric Light Diesel", 0xffff00, NONE).asFluid(32);
    public static Material SulfuricHeavyFuel = new Material("Sulfuric Heavy Diesel", 0xffff00, NONE).asFluid(32);
    public static Material LightDiesel = new Material("Light Diesel", 0xffff00, NONE).asFluid(256);
    public static Material HeavyDiesel = new Material("Heavy Diesel", 0xffff00, NONE).asFluid(192);
    public static Material Glycerol = new Material("Glycerol", 0x87de87, NONE).asFluid(164).add(Carbon, 3, Hydrogen, 8, Oxygen, 3);

    /** Dusts **/
    public static Material SodiumSulfide = new Material("Sodium Sulfide", 0xffe680, NONE).asDust().add(Sodium, 2, Sulfur, 1);
    public static Material IridiumSodiumOxide = new Material("Iridium Sodium Oxide", 0xffffff, NONE).asDust();
    public static Material PlatinumGroupSludge = new Material("Platinum Group Sludge", 0x001e00, NONE).asDust();
    public static Material Glowstone = new Material("Glowstone", 0xffff00, SHINY).asDust();
    public static Material Graphene = new Material("Graphene", 0x808080, DULL).asDust();
    public static Material Oilsands = new Material("Oilsands", 0x0a0a0a, NONE).asDust(ORE);
    //    public static Materials Paper = new Materials(879, "Paper", 0xfafafa, PAPER).asDust(); //TODO needed?
    public static Material RareEarth = new Material("Rare Earth", 0x808064, FINE).asDust();
    public static Material Endstone = new Material("Endstone", 0xffffff, DULL).asDust();
    public static Material Netherrack = new Material("Netherrack", 0xc80000, DULL).asDust();
    public static Material Almandine = new Material("Almandine", 0xff0000, ROUGH).asDust(ORE).add(Aluminium, 2, Iron, 3, Silicon, 3, Oxygen, 12);
    public static Material Andradite = new Material("Andradite", 0x967800, ROUGH).asDust().add(Calcium, 3, Iron, 2, Silicon, 3, Oxygen, 12);
    public static Material Ash = new Material("Ash", 0x969696, DULL).asDust();
    public static Material BandedIron = new Material("Banded Iron", 0x915a5a, DULL).asDust(ORE).add(Iron, 2, Oxygen, 3);
    public static Material BrownLimonite = new Material("Brown Limonite", 0xc86400, METALLIC).asDust(ORE).add(Iron, 1, Hydrogen, 1, Oxygen, 2);
    public static Material Calcite = new Material("Calcite", 0xfae6dc, DULL).asDust(ORE).add(Calcium, 1, Carbon, 1, Oxygen, 3);
    public static Material Cassiterite = new Material("Cassiterite", 0xdcdcdc, METALLIC).asDust(ORE).add(Tin, 1, Oxygen, 2);
    public static Material Chalcopyrite = new Material("Chalcopyrite", 0xa07828, DULL).asDust(ORE).add(Copper, 1, Iron, 1, Sulfur, 2);
    public static Material Clay = new Material("Clay", 0xc8c8dc, ROUGH).asDust().add(Sodium, 2, Lithium, 1, Aluminium, 2, Silicon, 2, Water, 6);
    public static Material Cobaltite = new Material("Cobaltite", 0x5050fa, METALLIC).asDust(ORE).add(Cobalt, 1, Arsenic, 1, Sulfur, 1);
    public static Material Sheldonite = new Material("Sheldonite", 0xffffc8, METALLIC).asDust(ORE).add(Platinum, 3, Nickel, 1, Sulfur, 1, Palladium, 1);
    public static Material DarkAsh = new Material("Dark Ash", 0x323232, DULL).asDust();
    public static Material Galena = new Material("Galena", 0x643c64, DULL).asDust(ORE).add(Lead, 3, Silver, 3, Sulfur, 2);
    public static Material Garnierite = new Material("Garnierite", 0x32c846, METALLIC).asDust(ORE).add(Nickel, 1, Oxygen, 1);
    public static Material Grossular = new Material("Grossular", 0xc86400, ROUGH).asDust(ORE).add(Calcium, 3, Aluminium, 2, Silicon, 3, Oxygen, 12);
    public static Material Ilmenite = new Material("Ilmenite", 0x463732, METALLIC).asDust(ORE).add(Iron, 1, Titanium, 1, Oxygen, 3);
    public static Material Rutile = new Material("Rutile", 0xd40d5c, GEM_H).asDust().add(Titanium, 1, Oxygen, 2);
    public static Material Bauxite = new Material("Bauxite", 0xc86400, DULL).asDust(ORE).add(Rutile, 2, Aluminium, 16, Hydrogen, 10, Oxygen, 11);
    public static Material Magnesiumchloride = new Material("Magnesiumchloride", 0xd40d5c, DULL).asDust().add(Magnesium, 1, Chlorine, 2);
    public static Material Magnesite = new Material("Magnesite", 0xfafab4, METALLIC).asDust(ORE).add(Magnesium, 1, Carbon, 1, Oxygen, 3);
    public static Material Magnetite = new Material("Magnetite", 0x1e1e1e, METALLIC).asDust(ORE).add(Iron, 3, Oxygen, 4);
    public static Material Molybdenite = new Material("Molybdenite", 0x91919, METALLIC).asDust(ORE).add(Molybdenum, 1, Sulfur, 2);
    public static Material Obsidian = new Material("Obsidian", 0x503264, DULL).asDust().add(Magnesium, 1, Iron, 1, Silicon, 2, Oxygen, 8);
    public static Material Phosphate = new Material("Phosphate", 0xffff00, DULL).asDust(ORE).add(Phosphor, 1, Oxygen, 4);
    public static Material Polydimethylsiloxane = new Material("Polydimethylsiloxane", 0xf5f5f5, NONE).asDust().add(Carbon, 2, Hydrogen, 6, Oxygen, 1, Silicon, 1);
    public static Material Powellite = new Material("Powellite", 0xffff00, DULL).asDust(ORE).add(Calcium, 1, Molybdenum, 1, Oxygen, 4);
    public static Material Pyrite = new Material("Pyrite", 0x967828, ROUGH).asDust(ORE).add(Iron, 1, Sulfur, 2);
    public static Material Pyrolusite = new Material("Pyrolusite", 0x9696aa, DULL).asDust(ORE).add(Manganese, 1, Oxygen, 2);
    public static Material Pyrope = new Material("Pyrope", 0x783264, METALLIC).asDust(ORE).add(Aluminium, 2, Magnesium, 3, Silicon, 3, Oxygen, 12);
    public static Material RockSalt = new Material("Rock Salt", 0xf0c8c8, FINE).asDust(ORE).add(Potassium, 1, Chlorine, 1);
    public static Material RawRubber = new Material("Raw Rubber", 0xccc789, DULL).asDust().add(Carbon, 5, Hydrogen, 8);
    public static Material Salt = new Material("Salt", 0xfafafa, FINE).asDust(ORE).add(Sodium, 1, Chlorine, 1);
    public static Material Saltpeter = new Material("Saltpeter", 0xe6e6e6, FINE).asDust().add(Potassium, 1, Nitrogen, 1, Oxygen, 3);
    public static Material Scheelite = new Material("Scheelite", 0xc88c14, DULL).asDust(2500, ORE).add(Tungsten, 1, Calcium, 2, Oxygen, 4);
    public static Material SiliconDioxide = new Material("Silicon Dioxide", 0xc8c8c8, QUARTZ).asDust().add(Silicon, 1, Oxygen, 2);
    public static Material Pyrochlore = new Material("Pyrochlore", 0x2b1100, METALLIC).asDust(ORE).add(Calcium, 2, Niobium, 2, Oxygen, 7);
    public static Material FerriteMixture = new Material("Ferrite Mixture", 0xb4b4b4, METALLIC).asDust().add(Nickel, 1, Zinc, 1, Iron, 4);
    public static Material Massicot = new Material("Massicot", 0xffdd55, DULL).asDust().add(Lead, 1, Oxygen, 1);
    public static Material ArsenicTrioxide = new Material("Arsenic Trioxide", 0xffffff, SHINY).asDust().add(Arsenic, 2, Oxygen, 3);
    public static Material CobaltOxide = new Material("Cobalt Oxide", 0x668000, DULL).asDust().add(Cobalt, 1, Oxygen, 1);
    public static Material Zincite = new Material("Zincite", 0xfffff5, DULL).asDust().add(Zinc, 1, Oxygen, 1); //TODO needed?
    public static Material Magnesia = new Material("Magnesia", 0xffffff, DULL).asDust().add(Magnesium, 1, Oxygen, 1);
    public static Material Quicklime = new Material("Quicklime", 0xf0f0f0, DULL).asDust().add(Calcium, 1, Oxygen, 1);
    public static Material Potash = new Material("Potash", 0x784237, DULL).asDust().add(Potassium, 2, Oxygen, 1);
    public static Material SodaAsh = new Material("Soda Ash", 0xdcdcff, DULL).asDust().add(Sodium, 2, Carbon, 1, Oxygen, 3);
    public static Material Brick = new Material("Brick", 0x9b5643, ROUGH).asDust().add(Aluminium, 4, Silicon, 3, Oxygen, 12);
    public static Material Fireclay = new Material("Fireclay", 0xada09b, ROUGH).asDust().add(Brick, 1);
    public static Material SodiumBisulfate = new Material("Sodium Bisulfate", 0x004455, NONE).asDust().add(Sodium, 1, Hydrogen, 1, Sulfur, 1, Oxygen, 4);
    public static Material RawStyreneButadieneRubber = new Material("Raw Styrene-Butadiene Rubber", 0x54403d, SHINY).asDust().add(Styrene, 1, Butadiene, 3);
    public static Material PhosphorousPentoxide = new Material("Phosphorous Pentoxide", 0xdcdc00, NONE).asDust().add(Phosphor, 4, Oxygen, 10);
    public static Material MetalMixture = new Material("Metal Mixture", 0x502d16, METALLIC).asDust();
    public static Material SodiumHydroxide = new Material("Sodium Hydroxide", 0x003380, DULL).asDust().add(Sodium, 1, Oxygen, 1, Hydrogen, 1);
    public static Material Spessartine = new Material("Spessartine", 0xff6464, DULL).asDust(ORE).add(Aluminium, 2, Manganese, 3, Silicon, 3, Oxygen, 12);
    public static Material Sphalerite = new Material("Sphalerite", 0xffffff, DULL).asDust(ORE).add(Zinc, 1, Sulfur, 1);
    public static Material Stibnite = new Material("Stibnite", 0x464646, METALLIC).asDust(ORE).add(Antimony, 2, Sulfur, 3);
    public static Material Tetrahedrite = new Material("Tetrahedrite", 0xc82000, DULL).asDust(ORE).add(Copper, 3, Antimony, 1, Sulfur, 3, Iron, 1);
    public static Material Tungstate = new Material("Tungstate", 0x373223, DULL).asDust(ORE).add(Tungsten, 1, Lithium, 2, Oxygen, 4);
    public static Material Uraninite = new Material("Uraninite", 0x232323, METALLIC).asDust(ORE).add(Uranium, 1, Oxygen, 2);
    public static Material Uvarovite = new Material("Uvarovite", 0xb4ffb4, DIAMOND).asDust().add(Calcium, 3, Chrome, 2, Silicon, 3, Oxygen, 12);
    public static Material Wood = new Material("Wood", 0x643200, NONE).asDust(PLATE, GEAR, ROD).addTools(2.0F, 16, 0).add(Carbon, 1, Oxygen, 1, Hydrogen, 1);
    public static Material Stone = new Material("Stone", 0xcdcdcd, ROUGH).asDust(GEAR).addTools(4.0F, 32, 1);
    public static Material Wulfenite = new Material("Wulfenite", 0xff8000, DULL).asDust(ORE).add(Lead, 1, Molybdenum, 1, Oxygen, 4);
    public static Material YellowLimonite = new Material("Yellow Limonite", 0xc8c800, METALLIC).asDust(ORE).add(Iron, 1, Hydrogen, 1, Oxygen, 2);
    public static Material WoodSealed = new Material("Sealed Wood", 0x502800, NONE).asDust().addTools(3.0F, 24, 0).add(Wood, 1);
    public static Material Blaze = new Material("Blaze", 0xffc800, NONE).asDust().add(DarkAsh, 1, Sulfur, 1/*, Magic, 1*/);
    public static Material Flint = new Material("Flint", 0x002040, FLINT).asDust().addTools(2.5F, 64, 1).add(SiliconDioxide, 1);
    public static Material Marble = new Material("Marble", 0xc8c8c8, NONE).asDust().add(Magnesium, 1, Calcite, 7);
    public static Material PotassiumFeldspar = new Material("Potassium Feldspar", 0x782828, FINE).asDust().add(Potassium, 1, Aluminium, 1, Silicon, 3, Oxygen, 8);
    public static Material Biotite = new Material("Biotite", 0x141e14, METALLIC).asDust().add(Potassium, 1, Magnesium, 3, Aluminium, 3, Fluorine, 2, Silicon, 3, Oxygen, 10);
    public static Material GraniteBlack = new Material("Black Granite", 0x0a0a0a, ROUGH).asDust().addTools(4.0F, 64, 3).add(SiliconDioxide, 4, Biotite, 1);
    public static Material GraniteRed = new Material("Red Granite", 0xff0080, ROUGH).asDust().addTools(4.0F, 64, 3).add(Aluminium, 2, PotassiumFeldspar, 1, Oxygen, 3);
    public static Material VanadiumMagnetite = new Material("Vanadium Magnetite", 0x23233c, METALLIC).asDust(ORE).add(Magnetite, 1, Vanadium, 1);
    public static Material Bastnasite = new Material("Bastnasite", 0xc86e2d, FINE).asDust(ORE).add(Cerium, 1, Carbon, 1, Fluorine, 1, Oxygen, 3);
    public static Material Pentlandite = new Material("Pentlandite", 0xa59605, DULL).asDust(ORE).add(Nickel, 9, Sulfur, 8);
    public static Material Spodumene = new Material("Spodumene", 0xbeaaaa, DULL).asDust(ORE).add(Lithium, 1, Aluminium, 1, Silicon, 2, Oxygen, 6);
    public static Material Tantalite = new Material("Tantalite", 0x915028, METALLIC).asDust(ORE).add(Manganese, 1, Tantalum, 2, Oxygen, 6);
    public static Material Lepidolite = new Material("Lepidolite", 0xf0328c, FINE).asDust(ORE).add(Potassium, 1, Lithium, 3, Aluminium, 4, Fluorine, 2, Oxygen, 10);
    public static Material Glauconite = new Material("Glauconite", 0x82b43c, DULL).asDust(ORE).add(Potassium, 1, Magnesium, 2, Aluminium, 4, Hydrogen, 2, Oxygen, 12);
    public static Material Bentonite = new Material("Bentonite", 0xf5d7d2, ROUGH).asDust(ORE).add(Sodium, 1, Magnesium, 6, Silicon, 12, Hydrogen, 6, Water, 5, Oxygen, 36);
    public static Material Pitchblende = new Material("Pitchblende", 0xc8d200, DULL).asDust(ORE).add(Uraninite, 3, Thorium, 1, Lead, 1);
    public static Material Malachite = new Material("Malachite", 0x055f05, DULL).asDust(ORE).add(Copper, 2, Carbon, 1, Hydrogen, 2, Oxygen, 5);
    public static Material Barite = new Material("Barite", 0xe6ebff, DULL).asDust(ORE).add(Barium, 1, Sulfur, 1, Oxygen, 4);
    public static Material Talc = new Material("Talc", 0x5ab45a, DULL).asDust(ORE).add(Magnesium, 3, Silicon, 4, Hydrogen, 2, Oxygen, 12);
    public static Material Soapstone = new Material("Soapstone", 0x5f915f, DULL).asDust(ORE).add(Magnesium, 3, Silicon, 4, Hydrogen, 2, Oxygen, 12);
    public static Material Concrete = new Material("Concrete", 0x646464, ROUGH).asDust(300).add(Stone, 1);
    public static Material AntimonyTrioxide = new Material("Antimony Trioxide", 0xe6e6f0, DULL).asDust().add(Antimony, 2, Oxygen, 3);
    public static Material CupricOxide = new Material("Cupric Oxide", 0x0f0f0f, DULL).asDust().add(Copper, 1, Oxygen, 1);
    public static Material Ferrosilite = new Material("Ferrosilite", 0x97632a, DULL).asDust().add(Iron, 1, Silicon, 1, Oxygen, 3);

    /** Gems **/
    public static Material CertusQuartz = new Material("Certus Quartz", 0xd2d2e6, QUARTZ).asGemBasic(false, PLATE, ORE).addTools(5.0F, 32, 1);
    public static Material Dilithium = new Material("Dilithium", 0xfffafa, DIAMOND).asGemBasic(true);
    public static Material NetherQuartz = new Material("Nether Quartz", 0xe6d2d2, QUARTZ).asGemBasic(false, ORE).addTools(1.0F, 32, 1);
    public static Material NetherStar = new Material("Nether Star", 0xffffff, NONE).asGemBasic(false).addTools(1.0F, 5120, 4);
    public static Material Quartzite = new Material("Quartzite", 0xd2e6d2, QUARTZ).asGemBasic(false, ORE).add(Silicon, 1, Oxygen, 2);

    //Brittle Gems
    public static Material BlueTopaz = new Material("Blue Topaz", 0x0000ff, GEM_H).asGem(true).addTools(7.0F, 256, 3).add(Aluminium, 2, Silicon, 1, Fluorine, 2, Hydrogen, 2, Oxygen, 6);
    public static Material Charcoal = new Material("Charcoal", 0x644646, FINE).asGemBasic(false, BLOCK).add(Carbon, 1);
    public static Material Coal = new Material("Coal", 0x464646, ROUGH).asGemBasic(false, BLOCK, ORE).add(Carbon, 1);
    public static Material Lignite = new Material("Lignite Coal", 0x644646, ROUGH).asGemBasic(false, BLOCK, ORE).add(Carbon, 3, Water, 1);

    public static Material Diamond = new Material("Diamond", 0xc8ffff, DIAMOND).asGem(true, ORE, GEAR).addTools(8.0F, 1280, 3).add(Carbon, 1);
    public static Material Emerald = new Material("Emerald", 0x50ff50, NONE).asGem(true, ORE).addTools(7.0F, 256, 2).add(Silver, 1, Gold, 1);
    public static Material GreenSapphire = new Material("Green Sapphire", 0x64c882, GEM_H).asGem(true, ORE).addTools(7.0F, 256, 2).add(Aluminium, 2, Oxygen, 3);
    public static Material Lazurite = new Material("Lazurite", 0x6478ff, LAPIS).asGemBasic(false, ORE).add(Aluminium, 6, Silicon, 6, Calcium, 8, Sodium, 8);
    public static Material Ruby = new Material("Ruby", 0xff6464, RUBY).asGem(true, ORE).addTools(7.0F, 256, 2).add(Chrome, 1, Aluminium, 2, Oxygen, 3);
    public static Material Sapphire = new Material("Sapphire", 0x6464c8, GEM_V).asGem(true, ORE).addTools(7.0F, 256, 2).add(Aluminium, 2, Oxygen, 3);
    public static Material Sodalite = new Material("Sodalite", 0x1414ff, LAPIS).asGemBasic(false, ORE).add(Aluminium, 3, Silicon, 3, Sodium, 4, Chlorine, 1);
    public static Material Tanzanite = new Material("Tanzanite", 0x4000c8, GEM_V).asGem(true).addTools(7.0F, 256, 2).add(Calcium, 2, Aluminium, 3, Silicon, 3, Hydrogen, 1, Oxygen, 13);
    public static Material Topaz = new Material("Topaz", 0xff8000, GEM_H).asGem(true).addTools(7.0F, 256, 3).add(Aluminium, 2, Silicon, 1, Fluorine, 2, Hydrogen, 2, Oxygen, 6);
    public static Material Glass = new Material("Glass", 0xfafafa, NONE).asDust(PLATE).add(SiliconDioxide, 1);
    public static Material Olivine = new Material("Olivine", 0x96ff96, RUBY).asGem(true, ORE).addTools(7.0F, 256, 2).add(Magnesium, 2, Iron, 1, SiliconDioxide, 2);
    public static Material Opal = new Material("Opal", 0x0000ff, RUBY).asGem(true).addTools(7.0F, 256, 2).add(SiliconDioxide, 1);
    public static Material Amethyst = new Material("Amethyst", 0xd232d2, FLINT).asGem(true).addTools(7.0F, 256, 3).add(SiliconDioxide, 4, Iron, 1);
    public static Material Lapis = new Material("Lapis", 0x4646dc, LAPIS).asGemBasic(false, ORE).add(Lazurite, 12, Sodalite, 2, Pyrite, 1, Calcite, 1);
    public static Material EnderPearl = new Material("Enderpearl", 0x6cdcc8, SHINY).asGemBasic(false, ROD, PLATE).add(Beryllium, 1, Potassium, 4, Nitrogen, 5/*, Magic, 6*/);
    public static Material EnderEye = new Material("Endereye", 0xa0fae6, SHINY).asGemBasic(false, ROD, PLATE).add(EnderPearl, 1, Blaze, 1);
    public static Material Apatite = new Material("Apatite", 0xc8c8ff, DIAMOND).asGemBasic(false, ORE).add(Calcium, 5, Phosphate, 3, Chlorine, 1);
    public static Material Phosphorus = new Material("Phosphorus", 0xffff00, FLINT).asGemBasic(false, ORE).add(Calcium, 3, Phosphate, 2);
    public static Material GarnetRed = new Material("Red Garnet", 0xc85050, RUBY).asGemBasic(true).addTools(7.0F, 128, 2).add(Pyrope, 3, Almandine, 5, Spessartine, 8);
    public static Material GarnetYellow = new Material("Yellow Garnet", 0xc8c850, RUBY).asGemBasic(true).addTools(7.0F, 128, 2).add(Andradite, 5, Grossular, 8, Uvarovite, 3);
    public static Material Monazite = new Material("Monazite", 0x324632, DIAMOND).asGemBasic(false, ORE).add(RareEarth, 1, Phosphate, 1);

    /** **/
    public static Material Redstone = new Material("Redstone", 0xc80000, ROUGH).asDust(ORE).add(Silicon, 1, Pyrite, 5, Ruby, 1, Mercury, 3);
    public static Material Basalt = new Material("Basalt", 0x1e1414, ROUGH).asDust().add(Olivine, 1, Calcite, 3, Flint, 8, DarkAsh, 4);

    /** Metals **/
    public static Material AnnealedCopper = new Material("Annealed Copper", 0xff7814, SHINY).asMetal(1357, 0, PLATE, FOIL, ROD, WIREF).add(Copper, 1);
    public static Material BatteryAlloy = new Material("Battery Alloy", 0x9c7ca0, DULL).asMetal(295, 0, PLATE).add(Lead, 4, Antimony, 1);
    public static Material Brass = new Material("Brass", 0xffb400, METALLIC).asMetal(1170, 0, FRAME).addTools(7.0F, 96, 1).add(Zinc, 1, Copper, 3);
    public static Material Bronze = new Material("Bronze", 0xff8000, METALLIC).asMetal(1125, 0, GEAR, FRAME).addTools(6.0F, 192, 2).add(Tin, 1, Copper, 3);
    public static Material Cupronickel = new Material("Cupronickel", 0xe39680, METALLIC).asMetal(1728, 0).addTools(6.0F, 64, 1).add(Copper, 1, Nickel, 1);
    public static Material Electrum = new Material("Electrum", 0xffff64, SHINY).asMetal(1330, 0, PLATE, FOIL, ROD, WIREF).addTools(12.0F, 64, 2).add(Silver, 1, Gold, 1);
    public static Material Invar = new Material("Invar", 0xb4b478, METALLIC).asMetal(1700, 0, FRAME).addTools(6.0F, 256, 2).add(Iron, 2, Nickel, 1);
    public static Material Kanthal = new Material("Kanthalm", 0xc2d2df, METALLIC).asMetal(1800, 1800).addTools(6.0F, 64, 2).add(Iron, 1, Aluminium, 1, Chrome, 1);
    public static Material Magnalium = new Material("Magnalium", 0xc8beff, DULL).asMetal(870, 0).addTools(6.0F, 256, 2).add(Magnesium, 1, Aluminium, 2);
    public static Material Nichrome = new Material("Nichrome", 0xcdcef6, METALLIC).asMetal(2700, 2700).addTools(6.0F, 64, 2).add(Nickel, 4, Chrome, 1);
    public static Material NiobiumTitanium = new Material("Niobium Titanium", 0x1d1d29, DULL).asMetal(4500, 4500, PLATE, FOIL, ROD, WIREF).add(Nickel, 4, Chrome, 1);
    public static Material PigIron = new Material("Pig Iron", 0xc8b4b4, METALLIC).asMetal(1420, 0).addTools(6.0F, 384, 2).add(Iron, 1);
    public static Material SolderingAlloy = new Material("Soldering Alloy", 0xdcdce6, DULL).asMetal(400, 400, PLATE, FOIL, ROD, WIREF).add(Tin, 9, Antimony, 1);
    public static Material StainlessSteel = new Material("Stainless Steel", 0xc8c8dc, SHINY).asMetal(1700, 1700, SCREW, GEAR, SGEAR, FRAME).addTools(7.0F, 480, 2).add(Iron, 6, Chrome, 1, Manganese, 1, Nickel, 1);
    public static Material Steel = new Material("Steel", 0x808080, METALLIC).asMetal(1811, 1000, GEAR, SGEAR, PLATE, FOIL, WIREF, SCREW, ROD, RING, FRAME).addTools(6.0F, 512, 2).add(Iron, 50, Carbon, 1);
    public static Material Ultimet = new Material("Ultimet", 0xb4b4e6, SHINY).asMetal(2700, 2700).add(Cobalt, 5, Chrome, 2, Nickel, 1, Molybdenum, 1);
    public static Material VanadiumGallium = new Material("Vanadium Gallium", 0x80808c, SHINY).asMetal(4500, 4500, ROD).add(Vanadium, 3, Gallium, 1);
    public static Material WroughtIron = new Material("Wrought Iron", 0xc8b4b4, METALLIC).asMetal(1811, 0, RING, FRAME).addTools(6.0F, 384, 2).add(Iron, 1);
    public static Material YttriumBariumCuprate = new Material("Yttrium Barium Cuprate", 0x504046, METALLIC).asMetal(4500, 4500, PLATE, FOIL, ROD, WIREF).add(Yttrium, 1, Barium, 2, Copper, 3, Oxygen, 7);
    public static Material SterlingSilver = new Material("Sterling Silver", 0xfadce1, SHINY).asMetal(1700, 1700).addTools(13.0F, 128, 2).add(Copper, 1, Silver, 4);
    public static Material RoseGold = new Material("Rose Gold", 0xffe61e, SHINY).asMetal(1600, 1600).addTools(14.0F, 128, 2).add(Copper, 1, Gold, 4);
    public static Material BlackBronze = new Material("Black Bronze", 0x64327d, DULL).asMetal(2000, 2000).addTools(12.0F, 256, 2).add(Gold, 1, Silver, 1, Copper, 3);
    public static Material BismuthBronze = new Material("Bismuth Bronze", 0x647d7d, DULL).asMetal(1100, 1100).addTools(8.0F, 256, 2).add(Bismuth, 1, Zinc, 1, Copper, 3);
    public static Material BlackSteel = new Material("Black Steel", 0x646464, METALLIC).asMetal(1200, 1200, FRAME).addTools(6.5F, 768, 2).add(Nickel, 1, BlackBronze, 1, Steel, 3);
    public static Material RedSteel = new Material("Red Steel", 0x8c6464, METALLIC).asMetal(1300, 1300).addTools(7.0F, 896, 2).add(SterlingSilver, 1, BismuthBronze, 1, Steel, 2, BlackSteel, 4);
    public static Material BlueSteel = new Material("Blue Steel", 0x64648c, METALLIC).asMetal(1400, 1400, FRAME).addTools(7.5F, 1024, 2).add(RoseGold, 1, Brass, 1, Steel, 2, BlackSteel, 4);
    public static Material DamascusSteel = new Material("Damascus Steel", 0x6e6e6e, METALLIC).asMetal(2500, 1500).addTools(8.0F, 1280, 2).add(Steel, 1);
    public static Material TungstenSteel = new Material("Tungstensteel", 0x6464a0, METALLIC).asMetal(3000, 3000, SCREW, GEAR, SGEAR, ROD, RING, FRAME).addTools(8.0F, 2560, 4).add(Steel, 1, Tungsten, 1);
    public static Material RedAlloy = new Material("Red Alloy", 0xc80000, DULL).asMetal(295, 0, PLATE, FOIL, ROD, WIREF).add(Copper, 1, Redstone, 4);
    public static Material CobaltBrass = new Material("Cobalt Brass", 0xb4b4a0, METALLIC).asMetal(1500, 0, GEAR).addTools(8.0F, 256, 2).add(Brass, 7, Aluminium, 1, Cobalt, 1);
    public static Material IronMagnetic = new Material("Magnetic Iron", 0xc8c8c8, MAGNETIC).asMetal(1811, 0).addTools(6.0F, 256, 2).add(Iron, 1);
    public static Material SteelMagnetic = new Material("Magnetic Steel", 0x808080, MAGNETIC).asMetal(1000, 1000).addTools(6.0F, 512, 2).add(Steel, 1);
    public static Material NeodymiumMagnetic = new Material("Magnetic Neodymium", 0x646464, MAGNETIC).asMetal(1297, 1297).addTools(7.0F, 512, 2).add(Neodymium, 1);
    public static Material NickelZincFerrite = new Material("Nickel-Zinc Ferrite", 0x3c3c3c, ROUGH).asMetal(1500, 1500).addTools(3.0F, 32, 1).add(Nickel, 1, Zinc, 1, Iron, 4, Oxygen, 8);
    public static Material TungstenCarbide = new Material("Tungsten Carbide", 0x330066, METALLIC).asMetal(2460, 2460).addTools(14.0F, 1280, 4).add(Tungsten, 1, Carbon, 1);
    public static Material VanadiumSteel = new Material("Vanadium Steel", 0xc0c0c0, METALLIC).asMetal(1453, 1453).addTools(3.0F, 1920, 3).add(Vanadium, 1, Chrome, 1, Steel, 7);
    public static Material HSSG = new Material("HSSG", 0x999900, METALLIC).asMetal(4500, 4500, GEAR, SGEAR, FRAME).addTools(10.0F, 4000, 3).add(TungstenSteel, 5, Chrome, 1, Molybdenum, 2, Vanadium, 1);
    public static Material HSSE = new Material("HSSE", 0x336600, METALLIC).asMetal(5400, 5400, GEAR, SGEAR, FRAME).addTools(10.0F, 5120, 4).add(HSSG, 6, Cobalt, 1, Manganese, 1, Silicon, 1);
    public static Material HSSS = new Material("HSSS", 0x660033, METALLIC).asMetal(5400, 5400).addTools(14.0F, 3000, 4).add(HSSG, 6, Iridium, 2, Osmium, 1);
    public static Material Osmiridium = new Material("Osmiridium", 0x6464ff, METALLIC).asMetal(3333, 2500, FRAME).addTools(7.0F, 1600, 3);
    public static Material Duranium = new Material("Duranium", 0xffffff, METALLIC).asMetal(295, 0).addTools(16.0F, 5120, 5);
    public static Material Naquadah = new Material("Naquadah", 0x323232, METALLIC).asMetal(5400, 5400, ORE).addTools(6.0F, 1280, 4);
    public static Material NaquadahAlloy = new Material("Naquadah Alloy", 0x282828, METALLIC).asMetal(7200, 7200).addTools(8.0F, 5120, 5);
    public static Material NaquadahEnriched = new Material("Naquadah Enriched", 0x323232, METALLIC).asMetal(4500, 4500, ORE).addTools(6.0F, 1280, 4); //TODO ORE flag added due to bee recipes, replace with OrePrefixes.mGeneratedItems
    public static Material Naquadria = new Material("Naquadria", 0x1e1e1e, SHINY).asMetal(9000, 9000, ORE).addTools(1.0F, 512, 4);
    public static Material Tritanium = new Material("Tritanium", 0xffffff, METALLIC).asMetal(295, 0, FRAME).addTools(20.0F, 10240, 6);

    /** Solids **/
    public static Material Plastic = new Material("Plastic", 0xc8c8c8, DULL).asSolid(295, 0, PLATE).add(Carbon, 1, Hydrogen, 2);
    public static Material Epoxid = new Material("Epoxid", 0xc88c14, DULL).asSolid(400, 0, PLATE).addTools(3.0F, 32, 1).add(Carbon, 2, Hydrogen, 4, Oxygen, 1);
    public static Material Silicone = new Material("Silicone", 0xdcdcdc, DULL).asSolid(900, 0, PLATE, FOIL).addTools(3.0F, 128, 1).add(Carbon, 2, Hydrogen, 6, Oxygen, 1, Silicon, 1);
    public static Material Polycaprolactam = new Material("Polycaprolactam", 0x323232, DULL).asSolid(500, 0).addTools(3.0F, 32, 1).add(Carbon, 6, Hydrogen, 11, Nitrogen, 1, Oxygen, 1);
    public static Material Polytetrafluoroethylene = new Material("Polytetrafluoroethylene", 0x646464, DULL).asSolid(1400, 0, PLATE, FRAME).addTools(3.0F, 32, 1).add(Carbon, 2, Fluorine, 4);
    public static Material Rubber = new Material("Rubber", 0x000000, SHINY).asSolid(295, 0, PLATE, RING).addTools(1.5F, 32, 0).add(Carbon, 5, Hydrogen, 8);
    public static Material PolyphenyleneSulfide = new Material("PolyphenyleneSulfide", 0xaa8800, DULL).asSolid(295, 0, PLATE, FOIL).addTools(3.0F, 32, 1).add(Carbon, 6, Hydrogen, 4, Sulfur, 1);
    public static Material Polystyrene = new Material("Polystyrene", 0xbeb4aa, DULL).asSolid(295, 0).add(Carbon, 8, Hydrogen, 8);
    public static Material StyreneButadieneRubber = new Material("Styrene-Butadiene Rubber", 0x211a18, SHINY).asSolid(295, 0).addTools(3.0F, 128, 1).add(Styrene, 1, Butadiene, 3);
    public static Material PolyvinylChloride = new Material("Polyvinyl Chloride", 0xd7e6e6, NONE).asSolid(295, 0, PLATE, FOIL).addTools(3.0F, 32, 1).add(Carbon, 2, Hydrogen, 3, Chlorine, 1);
    public static Material GalliumArsenide = new Material("Gallium Arsenide", 0xa0a0a0, DULL).asSolid(295, 1200).add(Arsenic, 1, Gallium, 1);
    public static Material EpoxidFiberReinforced = new Material("Fiber-Reinforced Epoxy Resin", 0xa07010, DULL).asSolid(400, 0).addTools(3.0F, 64, 1).add(Epoxid, 1);

//    public static void init() {
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
//    }

    public static void init() {
        if (Ref.ENABLE_ITEM_REPLACEMENTS) {
            Prefix.Ingot.addReplacement(Iron, new ItemStack(Items.IRON_INGOT));
            Prefix.Ingot.addReplacement(Gold, new ItemStack(Items.GOLD_INGOT));
            Prefix.Nugget.addReplacement(Iron, new ItemStack(Items.IRON_NUGGET));
            Prefix.Nugget.addReplacement(Gold, new ItemStack(Items.GOLD_NUGGET));
            Prefix.Gem.addReplacement(Diamond, new ItemStack(Items.DIAMOND));
            Prefix.Gem.addReplacement(Coal, new ItemStack(Items.COAL));
            Prefix.Gem.addReplacement(Charcoal, new ItemStack(Items.COAL, 1, 1));
            Prefix.Dust.addReplacement(Redstone, new ItemStack(Items.REDSTONE));
        }

        ELECSEPI.add(Bastnasite, Monazite);
        ELECSEPG.add(Magnetite, VanadiumMagnetite);
        ELECSEPN.add(YellowLimonite, BrownLimonite, Pyrite, BandedIron, Nickel, Glauconite, Pentlandite, Tin, Antimony, Ilmenite, Manganese, Chrome, Andradite);
        ELEC.add(Methane, CarbonDioxide, NitrogenDioxide, Toluene, VinylChloride, SulfurDioxide, SulfurTrioxide, Dimethylamine, DinitrogenTetroxide, NitricOxide, Ammonia, Chloromethane, Tetrafluoroethylene, CarbonMonoxide, Ethylene, Propane, Ethenone, Ethanol, Glyceryl, SodiumPersulfate, Dichlorobenzene, Styrene, Isoprene, Tetranitromethane, Epichlorohydrin, NitricAcid, Dimethylhydrazine, Chloramine, Dimethyldichlorosilane, HydrofluoricAcid, Chloroform, BisphenolA, AceticAcid, CalciumAcetateSolution, Acetone, Methanol, VinylAcetate, MethylAcetate, AllylChloride, HypochlorousAcid, Cumene, PhosphoricAcid, SulfuricAcid, Benzene, Phenol, Glycerol, SodiumSulfide, Almandine, Andradite, BandedIron, Calcite, Cassiterite, Chalcopyrite, Cobaltite, Galena, Garnierite, Grossular, Bauxite, Magnesite, Magnetite, Molybdenite, Obsidian, Phosphate, Polydimethylsiloxane, Pyrite, Pyrolusite, Pyrope, RockSalt, Saltpeter, SiliconDioxide, Pyrochlore, Massicot, ArsenicTrioxide, CobaltOxide, Zincite, Magnesia, Quicklime, Potash, SodaAsh, PhosphorousPentoxide, SodiumHydroxide, Spessartine, Sphalerite, Uvarovite, PotassiumFeldspar, Biotite, GraniteRed, Bastnasite, Pentlandite, Spodumene, Tantalite, Lepidolite, Glauconite, Bentonite, Malachite, Barite, Talc, Soapstone, AntimonyTrioxide, CupricOxide, Ferrosilite, Quartzite, BlueTopaz, Charcoal, Coal, Lignite, Diamond, Emerald, GreenSapphire, Lazurite, Ruby, Sapphire, Sodalite, Tanzanite, Topaz, Olivine, Opal, Amethyst, EnderPearl, Apatite, Monazite, StainlessSteel, Steel, Ultimet, IronMagnetic, SteelMagnetic, NeodymiumMagnetic, Osmiridium);
        CENT.add(NobleGases, Air, BrownLimonite, /*Cinnabar, */Clay, Sheldonite, Powellite, Stibnite, Tetrahedrite, Uraninite, Wulfenite, YellowLimonite, Blaze, Flint, Marble, GraniteBlack, VanadiumMagnetite, Pitchblende, Glass, Lapis, EnderEye, Phosphorus, GarnetRed, GarnetYellow, Redstone, Basalt, AnnealedCopper, BatteryAlloy, Brass, Bronze, Cupronickel, Electrum, Invar, Kanthal, Magnalium, Nichrome, NiobiumTitanium, PigIron, SolderingAlloy, VanadiumGallium, WroughtIron, SterlingSilver, RoseGold, BlackBronze, BismuthBronze, BlackSteel, DamascusSteel, TungstenSteel, RedAlloy, CobaltBrass, TungstenCarbide, VanadiumSteel, HSSG, HSSE, HSSS, GalliumArsenide/*, IndiumGalliumPhosphide, BorosilicateGlass*/);
        CRACK.add(RefineryGas, Naphtha, Ethane, Propane, Butane, Butene, Ethylene, Propene, LightDiesel, HeavyDiesel);
        CALCITE2X.add(Pyrite, BrownLimonite, YellowLimonite, Magnetite);
        CALCITE3X.add(Iron, PigIron, WroughtIron/*, MeteoricIron*/);
        WASHM.add(Gold, Silver, Osmium, Platinum, Sheldonite, Galena, Nickel, Tungstate, Lead, Magnetite, Iridium, Copper, Chalcopyrite);
        WASHS.add(Zinc, Nickel, Copper, Cobaltite, Tetrahedrite, Gold, Sphalerite, Garnierite, Chalcopyrite, Sheldonite, Platinum, Pentlandite, Tin, Malachite, YellowLimonite);
        NOSMELT.add(Wood, WoodSealed, Sulfur, Saltpeter, Graphite, /*Paper, */Coal, Charcoal, Lignite, Glyceryl, NitroFuel, Emerald, Amethyst, Tanzanite, Topaz, /*Amber,*/ GreenSapphire, Sapphire, Ruby, Opal, Olivine, GarnetRed, GarnetYellow, Apatite, Lapis, Sodalite, Lazurite, Monazite, Quartzite, Dilithium, NetherQuartz, CertusQuartz, Phosphorus, Phosphate, NetherStar, EnderPearl, EnderEye, Blaze);
        NOSMASH.add(Wood, WoodSealed, Sulfur, Saltpeter, Graphite, /*Paper, */Coal, Charcoal, Lignite, Rubber, StyreneButadieneRubber, Plastic, PolyvinylChloride, Polystyrene, Silicone, Glyceryl, NitroFuel, Concrete, Redstone, Glowstone, Netherrack, Stone, Brick, Endstone, Marble, Basalt, Obsidian, Flint, GraniteRed, GraniteBlack, Salt, RockSalt, Glass, Diamond, Emerald, Amethyst, Tanzanite, Topaz, /*Amber,*/ GreenSapphire, Sapphire, Ruby, Opal, Olivine, GarnetRed, GarnetYellow, Apatite, Lapis, Sodalite, Lazurite, Monazite, Quartzite, Dilithium, NetherQuartz, CertusQuartz, Phosphorus, Phosphate, NetherStar, EnderPearl, EnderEye);
        GRINDABLE.add(/*Paper, */Coal, Charcoal, Lignite, Lead, Tin, SolderingAlloy, Flint, Gold, Silver, Iron, IronMagnetic, Steel, SteelMagnetic, Zinc, Antimony, Copper, AnnealedCopper, Bronze, Nickel, Invar, Brass, WroughtIron, Electrum, Clay, Blaze);
        SMELTF.add(Concrete, Redstone, Glowstone, Glass, Blaze);
        //TODO explicit recipe SMELTG.add(Mercury, CINNABAR); //TODO Remove
        NOBBF.add(Tetrahedrite, Chalcopyrite, Sheldonite, Pyrolusite, Magnesite, Molybdenite, Galena);
        CRYSTALLIZE.add(Apatite, Lapis, Sodalite, Lazurite, Monazite, Quartzite, Dilithium, NetherQuartz, CertusQuartz);
        BRITTLEG.add(Coal, Charcoal, Lignite, BlueTopaz); //TODO Blue Topaz probably should not be brittle gem
        RUBBERTOOLS.add(Rubber, StyreneButadieneRubber, Plastic, PolyvinylChloride, Polystyrene, Silicone);
        SOLDER.add(Lead, Tin, SolderingAlloy);
        //TODO Mercury.add(METALL, SMELTG);

        WoodSealed.setMacerateInto(Wood);
        NeodymiumMagnetic.setSmeltInto(Neodymium).setMacerateInto(Neodymium).setArcSmeltInto(Neodymium);
        SteelMagnetic.setSmeltInto(Steel).setMacerateInto(Steel).setArcSmeltInto(Steel);
        Iron.setSmeltInto(Iron).setMacerateInto(Iron).setArcSmeltInto(WroughtIron);
        PigIron.setSmeltInto(Iron).setMacerateInto(Iron).setArcSmeltInto(WroughtIron);
        WroughtIron.setSmeltInto(Iron).setMacerateInto(Iron).setArcSmeltInto(WroughtIron);
        IronMagnetic.setSmeltInto(Iron).setMacerateInto(Iron).setArcSmeltInto(WroughtIron);
        Copper.setSmeltInto(Copper).setMacerateInto(Copper).setArcSmeltInto(AnnealedCopper);
        AnnealedCopper.setSmeltInto(Copper).setMacerateInto(Copper).setArcSmeltInto(AnnealedCopper);
        //Cinnabar.setDirectSmelting(Mercury);

        Tetrahedrite.setDirectSmeltInto(Copper);
        Chalcopyrite.setDirectSmeltInto(Copper);
        Malachite.setDirectSmeltInto(Copper);
        Pentlandite.setDirectSmeltInto(Nickel);
        Sphalerite.setDirectSmeltInto(Zinc);
        Pyrite.setDirectSmeltInto(Iron);
        YellowLimonite.setDirectSmeltInto(Iron);
        BrownLimonite.setDirectSmeltInto(Iron);
        BandedIron.setDirectSmeltInto(Iron);
        Magnetite.setDirectSmeltInto(Iron);
        Cassiterite.setDirectSmeltInto(Tin);
        Garnierite.setDirectSmeltInto(Nickel);
        Cobaltite.setDirectSmeltInto(Cobalt);
        Stibnite.setDirectSmeltInto(Antimony);
        Sheldonite.setDirectSmeltInto(Platinum);
        Pyrolusite.setDirectSmeltInto(Manganese);
        Magnesite.setDirectSmeltInto(Magnesium);
        Molybdenite.setDirectSmeltInto(Molybdenum);
        Galena.setDirectSmeltInto(Lead);
        Salt.setOreMulti(2).setSmeltingMulti(2);
        RockSalt.setOreMulti(2).setSmeltingMulti(2);
        Scheelite.setOreMulti(2).setSmeltingMulti(2);
        Tungstate.setOreMulti(2).setSmeltingMulti(2);
        Cassiterite.setOreMulti(2).setSmeltingMulti(2);
        NetherQuartz.setOreMulti(2).setSmeltingMulti(2);
        CertusQuartz.setOreMulti(2).setSmeltingMulti(2);
        Phosphorus.setOreMulti(3).setSmeltingMulti(3);
        Saltpeter.setOreMulti(4).setSmeltingMulti(4);
        Apatite.setOreMulti(4).setSmeltingMulti(4).setByProductMulti(2);
        Redstone.setOreMulti(5).setSmeltingMulti(5);
        Glowstone.setOreMulti(5).setSmeltingMulti(5);
        Lapis.setOreMulti(6).setSmeltingMulti(6).setByProductMulti(4);
        Sodalite.setOreMulti(6).setSmeltingMulti(6).setByProductMulti(4);
        Lazurite.setOreMulti(6).setSmeltingMulti(6).setByProductMulti(4);
        Monazite.setOreMulti(8).setSmeltingMulti(8).setByProductMulti(2);
//        Plastic.setEnchantmentForTools(Enchantment.knockback, 1);
//        PolyvinylChloride.setEnchantmentForTools(Enchantment.knockback, 1);
//        Polystyrene.setEnchantmentForTools(Enchantment.knockback, 1);
//        Rubber.setEnchantmentForTools(Enchantment.knockback, 2);
//        StyreneButadieneRubber.setEnchantmentForTools(Enchantment.knockback, 2);
//        Flint.setEnchantmentForTools(Enchantment.fireAspect, 1);
//        Blaze.setEnchantmentForTools(Enchantment.fireAspect, 3);
//        EnderPearl.setEnchantmentForTools(Enchantment.silkTouch, 1);
//        NetherStar.setEnchantmentForTools(Enchantment.silkTouch, 1);
//        BlackBronze.setEnchantmentForTools(Enchantment.smite, 2);
//        Gold.setEnchantmentForTools(Enchantment.smite, 3);
//        RoseGold.setEnchantmentForTools(Enchantment.smite, 4);
//        Platinum.setEnchantmentForTools(Enchantment.smite, 5);
//        Lead.setEnchantmentForTools(Enchantment.baneOfArthropods, 2);
//        Nickel.setEnchantmentForTools(Enchantment.baneOfArthropods, 2);
//        Invar.setEnchantmentForTools(Enchantment.baneOfArthropods, 3);
//        Antimony.setEnchantmentForTools(Enchantment.baneOfArthropods, 3);
//        BatteryAlloy.setEnchantmentForTools(Enchantment.baneOfArthropods, 4);
//        Bismuth.setEnchantmentForTools(Enchantment.baneOfArthropods, 4);
//        BismuthBronze.setEnchantmentForTools(Enchantment.baneOfArthropods, 5);
//        Iron.setEnchantmentForTools(Enchantment.sharpness, 1);
//        Bronze.setEnchantmentForTools(Enchantment.sharpness, 1);
//        Brass.setEnchantmentForTools(Enchantment.sharpness, 2);
//        Steel.setEnchantmentForTools(Enchantment.sharpness, 2);
//        WroughtIron.setEnchantmentForTools(Enchantment.sharpness, 2);
//        StainlessSteel.setEnchantmentForTools(Enchantment.sharpness, 3);
//        BlackSteel.setEnchantmentForTools(Enchantment.sharpness, 4);
//        RedSteel.setEnchantmentForTools(Enchantment.sharpness, 4);
//        BlueSteel.setEnchantmentForTools(Enchantment.sharpness, 5);
//        DamascusSteel.setEnchantmentForTools(Enchantment.sharpness, 5);
//        TungstenCarbide.setEnchantmentForTools(Enchantment.sharpness, 5);
//        HSSE.setEnchantmentForTools(Enchantment.sharpness, 5);
//        HSSG.setEnchantmentForTools(Enchantment.sharpness, 4);
//        HSSS.setEnchantmentForTools(Enchantment.sharpness, 5);
//        Lava.setTemperatureDamage(3.0F);
        Chalcopyrite.addByProduct(Pyrite, Cobalt, Cadmium, Gold);
        Sphalerite.addByProduct(GarnetYellow, Cadmium, Gallium, Zinc);
        Glauconite.addByProduct(Sodium, Aluminium, Iron);
        Bentonite.addByProduct(Aluminium, Calcium, Magnesium);
        Uraninite.addByProduct(Uranium, Thorium, Uranium235);
        Pitchblende.addByProduct(Thorium, Uranium, Lead);
        Galena.addByProduct(Sulfur, Silver, Lead);
        Lapis.addByProduct(Lazurite, Sodalite, Pyrite);
        Pyrite.addByProduct(Sulfur, Phosphorus, Iron);
        Copper.addByProduct(Cobalt, Gold, Nickel);
        Nickel.addByProduct(Cobalt, Platinum, Iron);
        GarnetRed.addByProduct(Spessartine, Pyrope, Almandine);
        GarnetYellow.addByProduct(Andradite, Grossular, Uvarovite);
        Sheldonite.addByProduct(Palladium, Nickel, Iridium);
        //Cinnabar.addByProduct(Redstone, Sulfur, Glowstone);
        Tantalite.addByProduct(Manganese, Niobium, Tantalum);
        Pentlandite.addByProduct(Iron, Sulfur, Cobalt);
        Uranium.addByProduct(Lead, Uranium235, Thorium);
        Scheelite.addByProduct(Manganese, Molybdenum, Calcium);
        Tungstate.addByProduct(Manganese, Silver, Lithium);
        Bauxite.addByProduct(Grossular, Rutile, Gallium);
        Redstone.addByProduct(/*Cinnabar, */RareEarth, Glowstone);
        Monazite.addByProduct(Thorium, Neodymium, RareEarth);
        Malachite.addByProduct(Copper, BrownLimonite, Calcite);
        YellowLimonite.addByProduct(Nickel, BrownLimonite, Cobalt);
        Lepidolite.addByProduct(Lithium, Caesium, Boron);
        Andradite.addByProduct(GarnetYellow, Iron, Boron);
        Quartzite.addByProduct(CertusQuartz, Barite);
        CertusQuartz.addByProduct(Quartzite, Barite);
        BrownLimonite.addByProduct(Malachite, YellowLimonite);
        Neodymium.addByProduct(Monazite, RareEarth);
        Bastnasite.addByProduct(Neodymium, RareEarth);
        Glowstone.addByProduct(Redstone, Gold);
        Zinc.addByProduct(Tin, Gallium);
        Tungsten.addByProduct(Manganese, Molybdenum);
        Iron.addByProduct(Nickel, Tin);
        Gold.addByProduct(Copper, Nickel);
        Tin.addByProduct(Iron, Zinc);
        Antimony.addByProduct(Zinc, Iron);
        Silver.addByProduct(Lead, Sulfur);
        Lead.addByProduct(Silver, Sulfur);
        Thorium.addByProduct(Uranium, Lead);
        Plutonium.addByProduct(Uranium, Lead);
        Electrum.addByProduct(Gold, Silver);
        Bronze.addByProduct(Copper, Tin);
        Brass.addByProduct(Copper, Zinc);
        Coal.addByProduct(Lignite, Thorium);
        Ilmenite.addByProduct(Iron, Rutile);
        Manganese.addByProduct(Chrome, Iron);
        Sapphire.addByProduct(Aluminium, GreenSapphire);
        GreenSapphire.addByProduct(Aluminium, Sapphire);
        Platinum.addByProduct(Nickel, Iridium);
        Emerald.addByProduct(Beryllium, Aluminium);
        Olivine.addByProduct(Pyrope, Magnesium);
        Chrome.addByProduct(Iron, Magnesium);
        Tetrahedrite.addByProduct(Antimony, Zinc);
        Magnetite.addByProduct(Iron, Gold);
        Basalt.addByProduct(Olivine, DarkAsh);
        VanadiumMagnetite.addByProduct(Magnetite, Vanadium);
        Lazurite.addByProduct(Sodalite, Lapis);
        Sodalite.addByProduct(Lazurite, Lapis);
        Spodumene.addByProduct(Aluminium, Lithium);
        Ruby.addByProduct(Chrome, GarnetRed);
        Phosphorus.addByProduct(Apatite, Phosphate);
        Iridium.addByProduct(Platinum, Osmium);
        Pyrope.addByProduct(GarnetRed, Magnesium);
        Almandine.addByProduct(GarnetRed, Aluminium);
        Spessartine.addByProduct(GarnetRed, Manganese);
        Grossular.addByProduct(GarnetYellow, Calcium);
        Uvarovite.addByProduct(GarnetYellow, Chrome);
        Calcite.addByProduct(Andradite, Malachite);
        NaquadahEnriched.addByProduct(Naquadah, Naquadria);
        Naquadah.addByProduct(NaquadahEnriched);
        Pyrolusite.addByProduct(Manganese);
        Molybdenite.addByProduct(Molybdenum);
        Stibnite.addByProduct(Antimony);
        Garnierite.addByProduct(Nickel);
        Lignite.addByProduct(Coal);
        Diamond.addByProduct(Graphite);
        Beryllium.addByProduct(Emerald);
        Apatite.addByProduct(Phosphorus);
        Magnesite.addByProduct(Magnesium);
        NetherQuartz.addByProduct(Netherrack);
        PigIron.addByProduct(Iron);
//        MeteoricIron.addByProduct(Iron);
        Steel.addByProduct(Iron);
        Graphite.addByProduct(Carbon);
        Netherrack.addByProduct(Sulfur);
        Flint.addByProduct(Obsidian);
        Cobaltite.addByProduct(Cobalt);
        Cobalt.addByProduct(Cobaltite);
        Sulfur.addByProduct(Sulfur);
        Saltpeter.addByProduct(Saltpeter);
        Endstone.addByProduct(Helium3);
        Osmium.addByProduct(Iridium);
        Magnesium.addByProduct(Olivine);
        Aluminium.addByProduct(Bauxite);
        Titanium.addByProduct(Almandine);
        Obsidian.addByProduct(Olivine);
        Ash.addByProduct(Carbon);
        DarkAsh.addByProduct(Carbon);
        Marble.addByProduct(Calcite);
        Clay.addByProduct(Clay);
        Cassiterite.addByProduct(Tin);
        GraniteBlack.addByProduct(Biotite);
        GraniteRed.addByProduct(PotassiumFeldspar);
        Phosphate.addByProduct(Phosphor);
        Phosphor.addByProduct(Phosphate);
        Tanzanite.addByProduct(Opal);
        Opal.addByProduct(Tanzanite);
        Amethyst.addByProduct(Amethyst);
//        Amber.addByProduct(Amber);
        Topaz.addByProduct(BlueTopaz);
        BlueTopaz.addByProduct(Topaz);
        Dilithium.addByProduct(Dilithium);
        Neutronium.addByProduct(Neutronium);
        Lithium.addByProduct(Lithium);
        Silicon.addByProduct(SiliconDioxide);
        Salt.addByProduct(RockSalt);
        RockSalt.addByProduct(Salt);

//        Glue.mChemicalFormula = "No Horses were harmed for the Production";
//        UUAmplifier.mChemicalFormula = "Accelerates the Mass Fabricator";
//        WoodSealed.mChemicalFormula = "";
//        Wood.mChemicalFormula = "";

//        Naquadah.mMoltenRGBa[0] = 0;
//        Naquadah.mMoltenRGBa[1] = 255;
//        Naquadah.mMoltenRGBa[2] = 0;
//        Naquadah.mMoltenRGBa[3] = 0;
//        NaquadahEnriched.mMoltenRGBa[0] = 64;
//        NaquadahEnriched.mMoltenRGBa[1] = 255;
//        NaquadahEnriched.mMoltenRGBa[2] = 64;
//        NaquadahEnriched.mMoltenRGBa[3] = 0;
//        Naquadria.mMoltenRGBa[0] = 128;
//        Naquadria.mMoltenRGBa[1] = 255;
//        Naquadria.mMoltenRGBa[2] = 128;
//        Naquadria.mMoltenRGBa[3] = 0;

//        NaquadahEnriched.mChemicalFormula = "Nq+";
//        Naquadah.mChemicalFormula = "Nq";
//        Naquadria.mChemicalFormula = "NqX";

        Materials.Water.setLiquid(FluidRegistry.WATER);
        Materials.Lava.setLiquid(FluidRegistry.LAVA);
        for (Material mat : MATERIAL_LOOKUP.values()) {
            if (mat.hasFlag(LIQUID) && mat.getLiquid() == null) {
                mat.setLiquid(new GTFluid(mat, LIQUID));
            }
            if (mat.hasFlag(GAS) && mat.getGas() == null) {
                mat.setGas(new GTFluid(mat, GAS));
            }
            if (mat.hasFlag(PLASMA) && mat.getPlasma() == null) {
                mat.setPlasma(new GTFluid(mat, PLASMA));
            }
        }
    }

    public static Material get(String name) {
        return MATERIAL_LOOKUP.get(name);
    }

    public static int getCount() {
        return MATERIAL_LOOKUP.size();
    }

    public static Collection<Material> getAll() {
        return MATERIAL_LOOKUP.values();
    }
}
