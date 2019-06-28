package muramasa.gtu.loaders;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.worldgen.WorldGenAsteroid;
import muramasa.gtu.api.worldgen.WorldGenOreLayer;
import muramasa.gtu.api.worldgen.WorldGenOreSmall;
import muramasa.gtu.api.worldgen.WorldGenStone;

import static muramasa.gtu.api.data.Materials.*;
import static muramasa.gtu.api.worldgen.DimensionType.*;

public class WorldGenLoader {

    public static WorldGenAsteroid ASTEROID_GEN = new WorldGenAsteroid();

    // This is probably not going to work.  Trying to create a fake orevein to put into hashtable when there will be no ores in a vein.
    public static WorldGenOreLayer noOresInVein = new WorldGenOreLayer( "NoOresInVein", 0, 255, 0, 255, 16, Aluminium, Aluminium, Aluminium, Aluminium);

    public static void init() {

        //TODO probably increase max generation heights for most things

        BlockStone graniteBlack = GregTechAPI.get(BlockStone.class, StoneType.GRANITE_BLACK.getId());
        BlockStone graniteRed = GregTechAPI.get(BlockStone.class, StoneType.GRANITE_RED.getId());
        BlockStone marble = GregTechAPI.get(BlockStone.class, StoneType.MARBLE.getId());
        BlockStone basalt = GregTechAPI.get(BlockStone.class, StoneType.BASALT.getId());

        new WorldGenStone("granite_black_tiny", graniteBlack, 1, 75, 5, 0, 180, false, OVERWORLD);
        new WorldGenStone("granite_black_small", graniteBlack, 1, 100, 10, 0, 180, false, OVERWORLD);
        new WorldGenStone("granite_black_medium", graniteBlack, 1, 200, 10, 0, 120, false, OVERWORLD);
        new WorldGenStone("granite_black_large", graniteBlack, 1, 300, 70, 0, 120, false, OVERWORLD);
        new WorldGenStone("granite_black_huge", graniteBlack, 1, 400, 150, 0, 120, false, OVERWORLD);

        new WorldGenStone("granite_red_tiny", graniteRed, 1, 75, 5, 0, 180, false, OVERWORLD);
        new WorldGenStone("granite_red_small", graniteRed, 1, 100, 10, 0, 180, false, OVERWORLD);
        new WorldGenStone("granite_red_medium", graniteRed, 1, 200, 10, 0, 120, false, OVERWORLD);
        new WorldGenStone("granite_red_large", graniteRed, 1, 300, 70, 0, 120, false, OVERWORLD);
        new WorldGenStone("granite_red_huge", graniteRed, 1, 400, 150, 0, 120, false, OVERWORLD);

        new WorldGenStone("marble_tiny", marble, 1, 75, 5, 0, 180, false, OVERWORLD);
        new WorldGenStone("marble_small", marble, 1, 100, 10, 0, 180, false, OVERWORLD);
        new WorldGenStone("marble_medium", marble, 1, 200, 10, 0, 120, false, OVERWORLD);
        new WorldGenStone("marble_large", marble, 1, 300, 70, 0, 120, false, OVERWORLD);
        new WorldGenStone("marble_huge", marble, 1, 400, 150, 0, 120, false, OVERWORLD);

        new WorldGenStone("basalt_tiny", basalt, 1, 75, 5, 0, 180, false, OVERWORLD);
        new WorldGenStone("basalt_small", basalt, 1, 100, 10, 0, 180, false, OVERWORLD);
        new WorldGenStone("basalt_medium", basalt, 1, 200, 10, 0, 120, false, OVERWORLD);
        new WorldGenStone("basalt_large", basalt, 1, 300, 70, 0, 120, false, OVERWORLD);
        new WorldGenStone("basalt_huge", basalt, 1, 400, 150, 0, 120, false, OVERWORLD);

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
        new WorldGenOreSmall("tin", 60, 120, 32, Tin, OVERWORLD, NETHER, END, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("bismuth", 80, 120, 8, Bismuth, OVERWORLD, NETHER, MOON, MARS);
        new WorldGenOreSmall("coal", 60, 100, 24, Coal, OVERWORLD);
        new WorldGenOreSmall("iron", 40, 80, 16, Iron, OVERWORLD, NETHER, END, MOON, MARS);
        new WorldGenOreSmall("lead", 40, 80, 16, Lead, OVERWORLD, NETHER, END, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("zinc", 30, 60, 12, Zinc, OVERWORLD, NETHER, END, MOON, MARS);
        new WorldGenOreSmall("gold", 20, 40, 8, Gold, OVERWORLD, NETHER, END, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("silver", 20, 40, 8, Silver, OVERWORLD, NETHER, END, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("nickel", 20, 40, 8, Nickel, OVERWORLD, NETHER, END, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("lapis", 20, 40, 4, Lapis, OVERWORLD, MOON, ASTEROID);
        new WorldGenOreSmall("diamond", 5, 10, 2, Diamond, OVERWORLD, NETHER, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("emerald", 5, 250, 1, Emerald, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("ruby", 5, 250, 1, Ruby, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("sapphire", 5, 250, 1, Sapphire, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("greensapphire", 5, 250, 1, GreenSapphire, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("olivine", 5, 250, 1, Olivine, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("topaz", 5, 250, 1, Topaz, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("tanzanite", 5, 250, 1, Tanzanite, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("amethyst", 5, 250, 1, Amethyst, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("opal", 5, 250, 1, Opal, OVERWORLD, NETHER, MARS, ASTEROID);
        //new WorldGenOreSmall("jasper", 5, 250, 1, Jasper, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("bluetopaz", 5, 250, 1, BlueTopaz, OVERWORLD, NETHER, MARS, ASTEROID);
        //new WorldGenOreSmall("amber", 5, 250, 1, Amber, OVERWORLD, NETHER, MARS, ASTEROID);
        //new WorldGenOreSmall("foolsruby", 5, 250, 1, FoolsRuby, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("garnetred", 5, 250, 1, GarnetRed, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("garnetyellow", 5, 250, 1, GarnetYellow, OVERWORLD, NETHER, MARS, ASTEROID);
        new WorldGenOreSmall("redstone", 5, 20, 8, Redstone, OVERWORLD, NETHER, MOON, MARS, ASTEROID);
        new WorldGenOreSmall("platinum", 20, 40, 8, Platinum, END, MARS, ASTEROID);
        new WorldGenOreSmall("iridium", 20, 40, 8, Iridium, END, MARS, ASTEROID);
        new WorldGenOreSmall("netherquartz", 30, 120, 64, NetherQuartz, NETHER);
        new WorldGenOreSmall("saltpeter", 10, 60, 8, Saltpeter, NETHER);
        new WorldGenOreSmall("sulfur_n", 10, 60, 32, Sulfur, NETHER);
        new WorldGenOreSmall("sulfur_o", 5, 15, 8, Sulfur, OVERWORLD);
    }
}
