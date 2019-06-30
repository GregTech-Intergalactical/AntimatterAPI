package muramasa.gtu.loaders;

import muramasa.gtu.api.worldgen.WorldGenAsteroid;
import muramasa.gtu.api.worldgen.WorldGenOreLayer;
import muramasa.gtu.api.worldgen.WorldGenOreSmall;
import muramasa.gtu.api.worldgen.WorldGenStone;

import static muramasa.gtu.Ref.*;
import static muramasa.gtu.api.data.Materials.*;
import static muramasa.gtu.api.data.StoneType.*;

public class WorldGenLoader {

    public static WorldGenAsteroid ASTEROID_GEN = new WorldGenAsteroid();

    public static void init() {
        //TODO probably increase max generation heights for most things
        //TODO add GC dims to all objects
        new WorldGenStone("granite_black_tiny", GRANITE_BLACK, 1, 75, 5, 0, 180, OVERWORLD);
        new WorldGenStone("granite_black_small", GRANITE_BLACK, 1, 100, 10, 0, 180, OVERWORLD);
        new WorldGenStone("granite_black_medium", GRANITE_BLACK, 1, 200, 10, 0, 120, OVERWORLD);
        new WorldGenStone("granite_black_large", GRANITE_BLACK, 1, 300, 70, 0, 120, OVERWORLD);
        new WorldGenStone("granite_black_huge", GRANITE_BLACK, 1, 400, 150, 0, 120, OVERWORLD);

        new WorldGenStone("granite_red_tiny", GRANITE_RED, 1, 75, 5, 0, 180, OVERWORLD);
        new WorldGenStone("granite_red_small", GRANITE_RED, 1, 100, 10, 0, 180, OVERWORLD);
        new WorldGenStone("granite_red_medium", GRANITE_RED, 1, 200, 10, 0, 120, OVERWORLD);
        new WorldGenStone("granite_red_large", GRANITE_RED, 1, 300, 70, 0, 120, OVERWORLD);
        new WorldGenStone("granite_red_huge", GRANITE_RED, 1, 400, 150, 0, 120, OVERWORLD);

        new WorldGenStone("marble_tiny", MARBLE, 1, 75, 5, 0, 180, OVERWORLD);
        new WorldGenStone("marble_small", MARBLE, 1, 100, 10, 0, 180, OVERWORLD);
        new WorldGenStone("marble_medium", MARBLE, 1, 200, 10, 0, 120, OVERWORLD);
        new WorldGenStone("marble_large", MARBLE, 1, 300, 70, 0, 120, OVERWORLD);
        new WorldGenStone("marble_huge", MARBLE, 1, 400, 150, 0, 120, OVERWORLD);

        new WorldGenStone("basalt_tiny", BASALT, 1, 75, 5, 0, 180, OVERWORLD);
        new WorldGenStone("basalt_small", BASALT, 1, 100, 10, 0, 180, OVERWORLD);
        new WorldGenStone("basalt_medium", BASALT, 1, 200, 10, 0, 120, OVERWORLD);
        new WorldGenStone("basalt_large", BASALT, 1, 300, 70, 0, 120, OVERWORLD);
        new WorldGenStone("basalt_huge", BASALT, 1, 400, 150, 0, 120, OVERWORLD);

        new WorldGenOreLayer("naquadah", 10, 60, 10, 5, 32, Naquadah, Naquadah, Naquadah, NaquadahEnriched, END);
        new WorldGenOreLayer("lignite", 50, 130, 160, 8, 32, Lignite, Lignite, Lignite, Coal, OVERWORLD);
        new WorldGenOreLayer("coal", 50, 80, 80, 6, 32, Coal, Coal, Coal, Lignite, OVERWORLD);
        new WorldGenOreLayer("magnetite", 50, 120, 160, 3, 32, Magnetite, Magnetite, Iron, VanadiumMagnetite, OVERWORLD, NETHER);
        new WorldGenOreLayer("gold", 60, 80, 160, 3, 32, Magnetite, Magnetite, VanadiumMagnetite, Gold, OVERWORLD);
        new WorldGenOreLayer("iron", 10, 40, 120, 4, 24, BrownLimonite, YellowLimonite, BandedIron, Malachite, OVERWORLD, NETHER);
        new WorldGenOreLayer("cassiterite", 40, 120, 50, 5, 24, Tin, Tin, Cassiterite, Tin, OVERWORLD, END);
        new WorldGenOreLayer("tetrahedrite", 80, 120, 70, 4, 24, Tetrahedrite, Tetrahedrite, Copper, Stibnite, OVERWORLD, NETHER);
        new WorldGenOreLayer("nether_quartz", 40, 80, 80, 5, 24, NetherQuartz, NetherQuartz, NetherQuartz, NetherQuartz, NETHER);
        new WorldGenOreLayer("sulfur", 5, 20, 100, 5, 24, Sulfur, Sulfur, Pyrite, Sphalerite, NETHER);
        new WorldGenOreLayer("copper", 10, 30, 80, 4, 24, Chalcopyrite, Iron, Pyrite, Copper, OVERWORLD, NETHER);
        new WorldGenOreLayer("bauxite", 50, 90, 80, 4, 24, Bauxite, Bauxite, Aluminium, Ilmenite, OVERWORLD);
        new WorldGenOreLayer("salts", 50, 60, 50, 3, 24, RockSalt, Salt, Lepidolite, Spodumene, OVERWORLD);
        new WorldGenOreLayer("redstone", 10, 40, 60, 3, 24, Redstone, Redstone, Ruby, Cinnabar, OVERWORLD, NETHER);
        new WorldGenOreLayer("soapstone", 10, 40, 40, 3, 16, Soapstone, Talc, Glauconite, Pentlandite, OVERWORLD);
        new WorldGenOreLayer("nickel", 10, 40, 40, 3, 16, Garnierite, Nickel, Cobaltite, Pentlandite, OVERWORLD, NETHER, END);
        new WorldGenOreLayer("platinum", 40, 50, 5, 3, 16, Sheldonite, Palladium, Platinum, Iridium, OVERWORLD, END);
        new WorldGenOreLayer("pitchblende", 10, 40, 40, 3, 16, Pitchblende, Pitchblende, Uraninite, Uraninite, OVERWORLD);
        new WorldGenOreLayer("uranium", 20, 30, 20, 3, 16, Uraninite, Uraninite, Uranium, Uranium, OVERWORLD);
        new WorldGenOreLayer("monazite", 20, 40, 30, 3, 16, Bastnasite, Bastnasite, Bastnasite/*Monazite*/, Neodymium, OVERWORLD);
        new WorldGenOreLayer("molybdenum", 20, 50, 5, 3, 16, Wulfenite, Molybdenite, Molybdenum, Molybdenum/*Powellite*/, OVERWORLD, END);
        new WorldGenOreLayer("tungstate", 20, 50, 10, 3, 16, Scheelite, Scheelite, Tungstate, Lithium, OVERWORLD, END);
        new WorldGenOreLayer("sapphire", 10, 40, 60, 3, 16, Almandine, Pyrope, Sapphire, GreenSapphire, OVERWORLD);
        new WorldGenOreLayer("manganese", 20, 30, 20, 3, 16, Grossular, Spessartine, Pyrolusite, Tantalite, OVERWORLD, END);
        new WorldGenOreLayer("quartz", 40, 80, 60, 3, 16, Quartzite, Barite, Barite/*CertusQuartz*/, Barite/*CertusQuartz*/, OVERWORLD);
        new WorldGenOreLayer("diamond", 5, 20, 40, 2, 16, Graphite, Graphite, Diamond, Coal, OVERWORLD);
        new WorldGenOreLayer("olivine", 10, 40, 60, 3, 16, Bentonite, Magnesite, Olivine, Glauconite, OVERWORLD, END);
        //new WorldGenLayer("apatite", 40, 60, 60, 3, 16, Apatite, Apatite, TricalciumPhosphate, Pyrochlore);
        new WorldGenOreLayer("gelena", 30, 60, 40, 5, 16, Galena, Galena, Silver, Lead, OVERWORLD);
        new WorldGenOreLayer("lapis", 20, 50, 40, 5, 16, Lapis/*Lazurite*/, Lapis/*Sodalite*/, Lapis, Calcite, OVERWORLD, END);
        new WorldGenOreLayer("beryllium", 5, 30, 30, 3, 16, Beryllium, Beryllium, Emerald, Thorium, OVERWORLD, END);
        new WorldGenOreLayer("oilsands", 50, 80, 80, 6, 32, Oilsands, Oilsands, Oilsands, Oilsands, OVERWORLD);

        new WorldGenOreSmall("copper", 60, 120, 32, Copper, OVERWORLD, NETHER, END, MOON, MARS);
        new WorldGenOreSmall("tin", 60, 120, 32, Tin, OVERWORLD, NETHER, END, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("bismuth", 80, 120, 8, Bismuth, OVERWORLD, NETHER, MOON, MARS);
        new WorldGenOreSmall("coal", 60, 100, 24, Coal, OVERWORLD);
        new WorldGenOreSmall("iron", 40, 80, 16, Iron, OVERWORLD, NETHER, END, MOON, MARS);
        new WorldGenOreSmall("lead", 40, 80, 16, Lead, OVERWORLD, NETHER, END, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("zinc", 30, 60, 12, Zinc, OVERWORLD, NETHER, END, MOON, MARS);
        new WorldGenOreSmall("gold", 20, 40, 8, Gold, OVERWORLD, NETHER, END, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("silver", 20, 40, 8, Silver, OVERWORLD, NETHER, END, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("nickel", 20, 40, 8, Nickel, OVERWORLD, NETHER, END, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("lapis", 20, 40, 4, Lapis, OVERWORLD, MOON, ASTEROIDS);
        new WorldGenOreSmall("diamond", 5, 10, 2, Diamond, OVERWORLD, NETHER, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("emerald", 5, 250, 1, Emerald, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("ruby", 5, 250, 1, Ruby, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("sapphire", 5, 250, 1, Sapphire, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("green_sapphire", 5, 250, 1, GreenSapphire, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("olivine", 5, 250, 1, Olivine, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("topaz", 5, 250, 1, Topaz, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("tanzanite", 5, 250, 1, Tanzanite, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("amethyst", 5, 250, 1, Amethyst, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("opal", 5, 250, 1, Opal, OVERWORLD, NETHER, MARS, ASTEROIDS);
        //new WorldGenOreSmall("jasper", 5, 250, 1, Jasper, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("blue_topaz", 5, 250, 1, BlueTopaz, OVERWORLD, NETHER, MARS, ASTEROIDS);
        //new WorldGenOreSmall("amber", 5, 250, 1, Amber, OVERWORLD, NETHER, MARS, ASTEROIDS);
        //new WorldGenOreSmall("foolsruby", 5, 250, 1, FoolsRuby, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("red_garnet", 5, 250, 1, GarnetRed, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("yellow_garnet", 5, 250, 1, GarnetYellow, OVERWORLD, NETHER, MARS, ASTEROIDS);
        new WorldGenOreSmall("redstone", 5, 20, 8, Redstone, OVERWORLD, NETHER, MOON, MARS, ASTEROIDS);
        new WorldGenOreSmall("platinum", 20, 40, 8, Platinum, END, MARS, ASTEROIDS);
        new WorldGenOreSmall("iridium", 20, 40, 8, Iridium, END, MARS, ASTEROIDS);
        new WorldGenOreSmall("nether_quartz", 30, 120, 64, NetherQuartz, NETHER);
        new WorldGenOreSmall("saltpeter", 10, 60, 8, Saltpeter, NETHER);
        new WorldGenOreSmall("sulfur_n", 10, 60, 32, Sulfur, NETHER);
        new WorldGenOreSmall("sulfur_o", 5, 15, 8, Sulfur, OVERWORLD);
    }
}
