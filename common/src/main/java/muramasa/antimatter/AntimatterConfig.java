package muramasa.antimatter;

import carbonconfiglib.api.ConfigType;
import carbonconfiglib.config.*;
import carbonconfiglib.impl.ReloadMode;
import carbonconfiglib.utils.AutomationType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import tesseract.Tesseract;

public class AntimatterConfig {


    /**
     * Client config values
     */
    public static ConfigEntry.BoolValue BASIC_MACHINE_MODELS;
    public static ConfigEntry.BoolValue SHOW_ALL_MATERIAL_ITEMS;
    public static ConfigEntry.BoolValue SHOW_ALL_FLUID_CELLS;
    public static ConfigEntry.BoolValue ADD_REI_GROUPS;
    public static ConfigEntry.BoolValue GROUP_ORES_ONLY;
    public static ConfigEntry.BoolValue SHOW_ALL_ORES;
    public static ConfigEntry.BoolValue SHOW_ROCKS;


    /**
     * Gameplay config values
     */
    public static ConfigEntry.DoubleValue PIPE_LEAK;

    public static ConfigEntry.IntValue AXE_TIMBER_MAX;

    public static ConfigEntry.BoolValue INPUT_RESET_MULTIBLOCK;
    public static ConfigEntry.BoolValue AXE_TIMBER;
    public static ConfigEntry.BoolValue SMARTER_TREE_DETECTION;
    public static ConfigEntry.BoolValue PLAY_CRAFTING_SOUNDS;
    public static ConfigEntry.BoolValue LOSSY_PART_CRAFTING;
    public static ConfigEntry.BoolValue MACHINES_EXPLODE;
    public static ConfigEntry.BoolValue EXPORT_DEFAULT_RECIPES;
    public static ConfigEntry.BoolValue RAIN_EXPLODES_MACHINES;

    /**
     * Worldgen config values
     */
    public static ConfigEntry.BoolValue VANILLA_ORE_GEN;
    public static ConfigEntry.BoolValue VANILLA_STONE_GEN;
    public static ConfigEntry.BoolValue SMALL_ORES;
    public static ConfigEntry.BoolValue SURFACE_ROCKS;
    public static ConfigEntry.BoolValue ORE_VEINS;
    public static ConfigEntry.BoolValue STONE_LAYERS;
    public static ConfigEntry.BoolValue STONE_LAYER_ORES;
    public static ConfigEntry.BoolValue ORE_VEIN_SMALL_ORE_MARKERS;
    public static ConfigEntry.BoolValue ORE_VEIN_SPECTATOR_DEBUG;
    public static ConfigEntry.BoolValue REGENERATE_DEFAULT_WORLDGEN_JSONS;

    public static ConfigEntry.IntValue ORE_VEIN_ROCK_CHANCE;
    public static ConfigEntry.IntValue STONE_LAYER_ROCK_CHANCE;
    public static ConfigEntry.IntValue ORE_VEIN_MAX_SIZE;
    public static ConfigEntry.IntValue ORE_VEIN_CHANCE;
    public static ConfigEntry.IntValue ORE_VEIN_FIND_ATTEMPTS;
    public static ConfigEntry.IntValue ORE_VEIN_PLACE_ATTEMPTS;
    public static ConfigEntry.IntValue ORE_VEIN_SMALL_ORE_MARKERS_MULTI;

    public static ConfigHandler CONFIG_COMMON;
    public static ConfigHandler CONFIG_CLIENT;

    public static void createConfig(){
        Config config = new Config("antimatter/common");
        ConfigSection world = config.add("world");
        VANILLA_ORE_GEN = world.addBool("disable_vanilla_ore_gen", true, "Disable Vanilla ore generation (Iron Ore, Diamond Ore etc) - Default: true")
                .setRequiredReload(ReloadMode.WORLD);
        VANILLA_STONE_GEN = world.addBool("disable_vanilla_stone_gen", true, "Disable vanilla stone generation (Granite, Diorite etc) - Default: true")
                .setRequiredReload(ReloadMode.WORLD);
        SMALL_ORES = world.addBool("small_ores", true, "Enable small ores - Default: true");
        SURFACE_ROCKS = world.addBool("surface_rocks", true, "Enable surface rocks - Default: true");
        REGENERATE_DEFAULT_WORLDGEN_JSONS = world.addBool("regenerate_default_world_jsons", false, "Regenerates the default jsons for antimatter's ore generation systems. will self reset after generating configs - Default: false");
        ORE_VEIN_SPECTATOR_DEBUG = world.addBool("ore_vein_spectator_debug", false, "Shows ore veins very clearly in spectator mode - Default: false");

        ConfigSection oreVeins = world.addSubSection("ore_veins");
        ORE_VEINS = oreVeins.addBool("ore_veins", true, "Enable ore veins - Default: true");
        ORE_VEIN_MAX_SIZE = oreVeins.addInt("ore_vein_max_size", 32, "Maximum size of an ore vein - Default: 32").setMin(1);
        ORE_VEIN_CHANCE = oreVeins.addInt("ore_vein_chance", 100, "Control percentage of filled 3x3 chunks. Lower number means less ore veins would spawn - Default : 100").setMin(1);
        ORE_VEIN_ROCK_CHANCE = oreVeins.addInt("ore_vein_rock_chance", 16, "Chance of ore veins having surface rocks. chance is 1/(the number) - Default: 16").setMin(1);
        ORE_VEIN_FIND_ATTEMPTS = oreVeins.addInt("ore_vein_find_attempts", 64, "Control number of attempts to find a valid ore vein,",
                        "Generally this maximum limit isn't hit, as selecting a vein is performant - Default : 64")
                .setMin(1).setRequiredReload(ReloadMode.WORLD);
        ORE_VEIN_PLACE_ATTEMPTS = oreVeins.addInt("ore_vein_place_attempts", 8, "Control number of attempts to place a valid ore vein,",
                "If a vein wasn't placed due to height restrictions, completely in the water, or other criterion, another attempt is tried - Default : 8")
                .setMin(1).setRequiredReload(ReloadMode.WORLD);
        ORE_VEIN_SMALL_ORE_MARKERS = oreVeins.addBool("ore_vein_small_ore_markers", true, "Enable ore vein's having small ores as markers/indicators - Default: true")
                .setRequiredReload(ReloadMode.WORLD);
        ORE_VEIN_SMALL_ORE_MARKERS_MULTI = oreVeins.addInt("ore_vein_small_ore_markers_multi", 2, "Multiplier to control how many small ore markers get generated per vein - Default : 2")
                .setMin(1).setRequiredReload(ReloadMode.WORLD);

        ConfigSection stoneLayers = world.addSubSection("stone_layers");
        STONE_LAYERS = stoneLayers.addBool("stone_layers", true, "Enable stone layers - Default: true");
        STONE_LAYER_ORES = stoneLayers.addBool("stone_layer_ores", true, "Enable stone layers having ores - Default: true");
        STONE_LAYER_ROCK_CHANCE = stoneLayers.addInt("stone_layer_rock_chance", 20, "Chance of stone layers having surface rocks. chance is 1/(the number) - Default: 8");

        ConfigSection gameplay = config.add("gameplay");
        INPUT_RESET_MULTIBLOCK = gameplay.addBool("input_reset_mulitblock", false, "Whether or not to reconsume recipe inputs on multiblock failure - Default : false");
        PIPE_LEAK = gameplay.addDouble("pipe_leak", 0.9, "Amount of gas retained passing through a leaky pipe - Default: 90%")
                .setMin(Double.MIN_VALUE).setMax(1.0);
        MACHINES_EXPLODE = gameplay.addBool("machines_explode", true, "Enable machines exploding on overvoltage - Default: true");
        EXPORT_DEFAULT_RECIPES = gameplay.addBool("export_default_recipes", false, "Exports default crafting and machine recipes to exported in the root minecraft folder. - Default: false");
        RAIN_EXPLODES_MACHINES = gameplay.addBool("rain_explodes_machines", true, "Enable machines exploding when it's raining - Default: true");
        PLAY_CRAFTING_SOUNDS = gameplay.addBool("play_crafting_sounds", true, "Hear various crafting sounds when you craft with any of Antimatter's tools that has a custom SoundType. Default: true");

        ConfigSection treefelling = gameplay.addSubSection("treefelling");
        SMARTER_TREE_DETECTION = treefelling.addBool("smarter_tree_detection", false,"Smart tree detection, instead of just going up in a column, it searches surrounding connected blocks too. Default: false",
                "Note: may have issues discerning between trees and placed down wood, use at your own risk.");
        AXE_TIMBER = treefelling.addBool("axe_timber", true, "Allow Antimatter Axe types to fell trees - Default: true");
        AXE_TIMBER_MAX = treefelling.addInt("axe_timber_max", 150, "Max height of a column of logs an Antimatter Axe type can fell - Default: 150")
                .setMin(1).setMax(2304);
        CONFIG_COMMON = AntimatterPlatformUtils.createConfig(Ref.ID, config);
        CONFIG_COMMON.register();
        if (AntimatterAPI.getSIDE().isClient()){
            Config client = new Config("antimatter/client");
            ConfigSection general = client.add("general");
            BASIC_MACHINE_MODELS = general.addBool("basic_machine_models", false, "Enable flat machine related models (5U Style) - Default: false");
            SHOW_ALL_MATERIAL_ITEMS = general.addBool("show_all_material_items", false, "Show all items in JEI, even ones that are unobtainable - Default: false");
            SHOW_ALL_FLUID_CELLS = general.addBool("show_all_fluid_cells", false, "Show all fluid cells in JEI - Default: false");
            ADD_REI_GROUPS = general.addBool("add_rei_groups", false, "Add collapsable groups for various antimatter material items to rei - Default: false");
            GROUP_ORES_ONLY = general.addBool("group_ores_only", true, "Only adds collapsable groups for ores and rocks, requires ADD_REI_GROUPS to be true - Default: true");
            SHOW_ALL_ORES = general.addBool("show_all_ores", false, "Show all ore variants in jei/rei, not just stone variants - Default: false");
            SHOW_ROCKS = general.addBool("show_rocks", false, "Show all block versions of rocks in jei/rei - Default: false");
            CONFIG_CLIENT = AntimatterPlatformUtils.createConfig(Ref.ID, client, ConfigSettings.withConfigType(ConfigType.CLIENT).withAutomations(AutomationType.AUTO_LOAD));
            CONFIG_CLIENT.register();
        }
    }


    public static class Data {
        public boolean ALL_MATERIAL_ITEMS, ITEM_REPLACEMENTS;

    }

    public static class ModCompatibility {
        public boolean ENABLE_ALL_REGISTRARS;
    }
}
