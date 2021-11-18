package muramasa.antimatter;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AntimatterConfig {

    //TODO add ResourceMethod enum values

    public static final Client CLIENT = new Client();
    public static final Data DATA = new Data();
    public static final Jei JEI = new Jei();
    public static final Gameplay GAMEPLAY = new Gameplay();
    public static final World WORLD = new World();
    public static final ModCompatibility MOD_COMPAT = new ModCompatibility();

    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final CommonConfig COMMON_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {

        final Pair<CommonConfig, ForgeConfigSpec> COMMON_PAIR = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        final Pair<ClientConfig, ForgeConfigSpec> CLIENT_PAIR = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_CONFIG = CLIENT_PAIR.getLeft();
        CLIENT_SPEC = CLIENT_PAIR.getRight();
        COMMON_CONFIG = COMMON_PAIR.getLeft();
        COMMON_SPEC = COMMON_PAIR.getRight();

    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent e) {

        if (e.getConfig().getSpec() == CLIENT_SPEC) bakeClientConfig();
        else if (e.getConfig().getSpec() == COMMON_SPEC) bakeCommonConfig();

    }

    public static class Client {

        /**
         * @see ClientConfig
         **/

        public boolean BASIC_MACHINE_MODELS, SHOW_ALL_MATERIAL_ITEMS, SHOW_ALL_FLUID_CELLS;

    }

    public static class Jei {

        /**
         * @see ClientConfig
         **/

        public boolean SHOW_ALL_MATERIAL_ITEMS, SHOW_ALL_FLUID_CELLS;

    }

    public static class Data {

        /**
         * @see CommonConfig
         **/

        public boolean ALL_MATERIAL_ITEMS, ITEM_REPLACEMENTS;

    }

    public static class Gameplay {

        /**
         * @see CommonConfig
         **/

        public double EU_TO_FE_RATIO, PIPE_LEAK;

        public int AXE_TIMBER_MAX;

        public boolean HARDCORE_CABLES, HARDCORE_PIPES, AXE_TIMBER, SMARTER_TREE_DETECTION, PLAY_CRAFTING_SOUNDS;

    }

    public static class World {

        /**
         * @see CommonConfig
         **/

        public boolean VANILLA_ORE_GEN, VANILLA_STONE_GEN, SMALL_ORES, SURFACE_ROCKS, ORE_VEINS, STONE_LAYERS, STONE_LAYER_ORES, ORE_VEIN_SMALL_ORE_MARKERS, ORE_VEIN_SPECTATOR_DEBUG;

        public int ORE_VEIN_ROCK_CHANCE, STONE_LAYER_ROCK_CHANCE, ORE_VEIN_MAX_SIZE, ORE_VEIN_CHANCE, ORE_VEIN_FIND_ATTEMPTS, ORE_VEIN_PLACE_ATTEMPTS, ORE_VEIN_SMALL_ORE_MARKERS_MULTI;

    }

    public static class ModCompatibility {

        public boolean ENABLE_ALL_REGISTRARS;

        // @Comment("Have Underground Biomes Stones interchangeable with GregTech native ones (Such as marble, red granite etc), this may break balance - Default: false")
        // public boolean ENABLE_UB_CROSS_OREDICT = false;

    }

    public static class ClientConfig {

        public final BooleanValue BASIC_MACHINE_MODELS, SHOW_ALL_MATERIAL_ITEMS, SHOW_ALL_FLUID_CELLS;

        public ClientConfig(Builder builder) {

            // @RequiresWorldRestart
            // public boolean ORE_JSON_RELOADING = true;

            BASIC_MACHINE_MODELS = builder.comment("Enable flat machine related models (5U Style) - Default: false")
                    .translation(Ref.ID + ".config.basic_machine_models")
                    .define("BASIC_MACHINE_MODELS", false);

            SHOW_ALL_MATERIAL_ITEMS = builder.comment("Show all items in JEI, even ones that are unobtainable - Default: false")
                    .translation(Ref.ID + ".config.show_all_material_items")
                    .define("SHOW_ALL_MATERIAL_ITEMS", false);

            SHOW_ALL_FLUID_CELLS = builder.comment("Show all fluid cells in JEI - Default: false")
                    .translation(Ref.ID + ".config.show_all_fluid_cells")
                    .define("SHOW_ALL_FLUID_CELLS", false);

        }

    }

    public static class CommonConfig {

        public final DoubleValue EU_TO_FE_RATIO, PIPE_LEAK;

        public final IntValue AXE_TIMBER_MAX, ORE_VEIN_ROCK_CHANCE, STONE_LAYER_ROCK_CHANCE, ORE_VEIN_MAX_SIZE, ORE_VEIN_CHANCE, ORE_VEIN_FIND_ATTEMPTS, ORE_VEIN_PLACE_ATTEMPTS,
                ORE_VEIN_SMALL_ORE_MARKERS_MULTI;

        public final BooleanValue ALL_MATERIAL_ITEMS, VANILLA_ORE_GEN, VANILLA_STONE_GEN, SMALL_ORES, SURFACE_ROCKS, ORE_VEINS, STONE_LAYERS, STONE_LAYER_ORES, ORE_VEIN_SMALL_ORE_MARKERS,
                ORE_VEIN_SPECTATOR_DEBUG, HARDCORE_CABLES, INPUT_RESET_MULTIBLOCK, HARDCORE_PIPES, AXE_TIMBER, SMARTER_TREE_DETECTION, PLAY_CRAFTING_SOUNDS, ENABLE_ALL_REGISTRARS;

        public CommonConfig(Builder builder) {

            builder.push("Data");

            ALL_MATERIAL_ITEMS = builder.comment("Generate all the potential Material Items, even if they're unused - Default: false")
                    .translation(Ref.ID + ".config.all_material_items")
                    .define("ALL_MATERIAL_ITEMS", false);

            builder.pop();

            builder.push("World");

            VANILLA_ORE_GEN = builder.comment("Disable Vanilla ore generation (Iron Ore, Diamond Ore etc) - Default: true")
                    .translation(Ref.ID + ".config.vanilla_ore_gen")
                    .worldRestart()
                    .define("DISABLE_VANILLA_ORE_GEN", true);

            VANILLA_STONE_GEN = builder.comment("Disable vanilla stone generation (Granite, Diorite etc) - Default: false")
                    .translation(Ref.ID + ".config.vanilla_stone_gen")
                    .worldRestart()
                    .define("DISABLE_VANILLA_STONE_GEN", false);

            SMALL_ORES = builder.comment("Enable small ores - Default: true")
                    .translation(Ref.ID + ".config.small_ores")
                    .define("SMALL_ORES", true);

            SURFACE_ROCKS = builder.comment("Enable surface rocks - Default: true")
                    .translation(Ref.ID + ".config.surface_rocks")
                    .define("SURFACE_ROCKS", true);

            builder.push("Ore Veins");

            ORE_VEINS = builder.comment("Enable ore veins - Default: true")
                    .translation(Ref.ID + ".config.ore_veins")
                    .define("ORE_VEINS", true);

            ORE_VEIN_MAX_SIZE = builder.comment("Maximum size of an ore vein - Default: 32")
                    .translation(Ref.ID + ".config.ore_vein_max_size")
                    .worldRestart()
                    .defineInRange("ORE_VEIN_MAX_SIZE", 32, 1, Integer.MAX_VALUE);

            ORE_VEIN_CHANCE = builder.comment("Control percentage of filled 3x3 chunks. Lower number means less ore veins would spawn - Default : 100")
                    .translation(Ref.ID + ".config.ore_vein_chance")
                    .worldRestart()
                    .defineInRange("ORE_VEIN_CHANCE", 100, 1, Integer.MAX_VALUE);

            ORE_VEIN_ROCK_CHANCE = builder.comment("Chance of ore veins having surface rocks - Default: 128")
                    .translation(Ref.ID + ".config.ore_vein_rock_chance")
                    .defineInRange("ORE_VEIN_ROCK_CHANCE", 128, 1, Integer.MAX_VALUE);

            ORE_VEIN_FIND_ATTEMPTS = builder.comment("Control number of attempts to find a valid ore vein,",
                    "Generally this maximum limit isn't hit, as selecting a vein is performant - Default : 64")
                    .translation(Ref.ID + ".config.ore_vein_find_attempts")
                    .worldRestart()
                    .defineInRange("ORE_VEIN_FIND_ATTEMPTS", 64, 1, Integer.MAX_VALUE);

            ORE_VEIN_PLACE_ATTEMPTS = builder.comment("Control number of attempts to place a valid ore vein,",
                    "If a vein wasn't placed due to height restrictions, completely in the water, or other criterion, another attempt is tried - Default : 8")
                    .translation(Ref.ID + ".config.ore_vein_place_attempts")
                    .worldRestart()
                    .defineInRange("ORE_VEIN_PLACE_ATTEMPTS", 8, 1, Integer.MAX_VALUE);

            ORE_VEIN_SMALL_ORE_MARKERS = builder.comment("Enable ore vein's having small ores as markers/indicators - Default: true")
                    .worldRestart()
                    .translation(Ref.ID + ".config.ore_vein_small_ore_markers")
                    .define("ORE_VEIN_SMALL_ORE_MARKERS", true);

            ORE_VEIN_SMALL_ORE_MARKERS_MULTI = builder.comment("Multiplier to control how many small ore markers get generated per vein - Default : 2")
                    .translation(Ref.ID + ".config.ore_vein_small_ore_markers_multi")
                    .worldRestart()
                    .defineInRange("ORE_VEIN_SMALL_ORE_MARKERS_MULTI", 2, 1, Integer.MAX_VALUE);

            builder.pop();

            builder.push("Stone Layers");

            STONE_LAYERS = builder.comment("Enable stone layers - Default: true")
                    .translation(Ref.ID + ".config.stone_layers")
                    .define("STONE_LAYERS", true);

            STONE_LAYER_ORES = builder.comment("Enable stone layers having ores - Default: true")
                    .translation(Ref.ID + ".config.stone_layers_ores")
                    .define("STONE_LAYER_ORES", true);

            STONE_LAYER_ROCK_CHANCE = builder.comment("Chance of stone layers having surface rocks - Default: 128")
                    .translation(Ref.ID + ".config.stone_layer_rock_chance")
                    .defineInRange("STONE_LAYER_ROCK_CHANCE", 128, 1, Integer.MAX_VALUE);

            builder.pop();

            ORE_VEIN_SPECTATOR_DEBUG = builder.comment("Shows ore veins very clearly in spectator mode - Default: false")
                    .translation(Ref.ID + ".config.ore_vein_debug")
                    .define("ORE_VEIN_SPECTATOR_DEBUG", true);  // todo change before release

            builder.pop();

            builder.push("Gameplay");

            INPUT_RESET_MULTIBLOCK = builder.comment("Whether or not to reconsume recipe inputs on multiblock failure - Default : false")
                    .translation(Ref.ID + ".config.input_reset")
                    .define("INPUT_RESET_MULTIBLOCK", false);

            EU_TO_FE_RATIO = builder.comment("The ratio of the eu to the fe energy converting - Default: (1.0 EU = 4.0 FE)")
                    .translation(Ref.ID + ".config.eu_to_rf_ratio")
                    .defineInRange("EU_TO_FE_RATIO", 1.0D, 0.1D, (Double.MAX_VALUE));

            PIPE_LEAK = builder.comment("Amount of gas retained passing through a leaky pipe - Default: 90%")
                    .translation(Ref.ID + ".config.pipe_leak")
                    .defineInRange("PIPE_LEAK", 0.9D, 0.0D, 1.0D);

            HARDCORE_CABLES = builder.comment("Enable hardcore cable loss and voltage - Default: true")
                    .translation(Ref.ID + ".config.hardcore_cables")
                    .define("HARDCORE_CABLES", true);

            HARDCORE_PIPES = builder.comment("Enable pipe blowing on overpressure - Default: false")
                    .translation(Ref.ID + ".config.hardcore_pipes")
                    .define("HARDCORE_PIPES", false);

            builder.push("Treefelling");

            SMARTER_TREE_DETECTION = builder.comment("Smart tree detection, instead of just going up in a column, it searches surrounding connected blocks too. Default: true")
                    .translation(Ref.ID + ".config.tree_detection")
                    .define("SMARTER_TREE_DETECTION", true);

            AXE_TIMBER = builder.comment("Allow Antimatter Axe types to fell trees - Default: true")
                    .translation(Ref.ID + ".config.axe_timber")
                    .define("AXE_TIMBER", true);

            AXE_TIMBER_MAX = builder.comment("Max height of a column of logs an Antimatter Axe type can fell - Default: 100")
                    .translation(Ref.ID + ".config.axe_timber_max")
                    .defineInRange("AXE_TIMBER_MAX", 100, 1, 2304);

            builder.pop();

            PLAY_CRAFTING_SOUNDS = builder.comment("Hear various crafting sounds when you craft with any of Antimatter's tools that has a custom SoundType. Default: true")
                    .translation(Ref.ID + ".config.play_crafting_sounds")
                    .define("PLAY_CRAFTING_SOUNDS", true);

            builder.pop();

            builder.push("Mod Compatibility");

            //@Comment("Enable all mod support registrars - Default: true")
            ENABLE_ALL_REGISTRARS = builder.comment("Enable all mod support registrars - Default: true")
                    .translation(Ref.ID + ".config.enable_all_resgistrars")
                    .define("ENABLE_ALL_REGISTRARS", true);

            builder.pop();

        }

    }

    private static void bakeClientConfig() {

        CLIENT.BASIC_MACHINE_MODELS = CLIENT_CONFIG.BASIC_MACHINE_MODELS.get();
        CLIENT.SHOW_ALL_MATERIAL_ITEMS = CLIENT_CONFIG.SHOW_ALL_MATERIAL_ITEMS.get();
        CLIENT.SHOW_ALL_FLUID_CELLS = CLIENT_CONFIG.SHOW_ALL_FLUID_CELLS.get();

    }

    private static void bakeCommonConfig() {

        WORLD.VANILLA_ORE_GEN = COMMON_CONFIG.VANILLA_ORE_GEN.get();
        WORLD.VANILLA_STONE_GEN = COMMON_CONFIG.VANILLA_STONE_GEN.get();
        WORLD.SMALL_ORES = COMMON_CONFIG.SMALL_ORES.get();
        WORLD.SURFACE_ROCKS = COMMON_CONFIG.SURFACE_ROCKS.get();
        WORLD.ORE_VEINS = COMMON_CONFIG.ORE_VEINS.get();
        WORLD.ORE_VEIN_MAX_SIZE = COMMON_CONFIG.ORE_VEIN_MAX_SIZE.get();
        WORLD.ORE_VEIN_CHANCE = COMMON_CONFIG.ORE_VEIN_CHANCE.get();
        WORLD.ORE_VEIN_ROCK_CHANCE = COMMON_CONFIG.ORE_VEIN_ROCK_CHANCE.get();
        WORLD.ORE_VEIN_FIND_ATTEMPTS = COMMON_CONFIG.ORE_VEIN_FIND_ATTEMPTS.get();
        WORLD.ORE_VEIN_PLACE_ATTEMPTS = COMMON_CONFIG.ORE_VEIN_PLACE_ATTEMPTS.get();
        WORLD.ORE_VEIN_SMALL_ORE_MARKERS = COMMON_CONFIG.ORE_VEIN_SMALL_ORE_MARKERS.get();
        WORLD.ORE_VEIN_SMALL_ORE_MARKERS_MULTI = COMMON_CONFIG.ORE_VEIN_SMALL_ORE_MARKERS_MULTI.get();
        WORLD.STONE_LAYERS = COMMON_CONFIG.STONE_LAYERS.get();
        WORLD.STONE_LAYER_ORES = COMMON_CONFIG.STONE_LAYER_ORES.get();
        WORLD.STONE_LAYER_ROCK_CHANCE = COMMON_CONFIG.STONE_LAYER_ROCK_CHANCE.get();
        WORLD.ORE_VEIN_SPECTATOR_DEBUG = COMMON_CONFIG.ORE_VEIN_SPECTATOR_DEBUG.get();

        GAMEPLAY.EU_TO_FE_RATIO = COMMON_CONFIG.EU_TO_FE_RATIO.get();
        GAMEPLAY.PIPE_LEAK = COMMON_CONFIG.PIPE_LEAK.get();
        GAMEPLAY.HARDCORE_CABLES = COMMON_CONFIG.HARDCORE_CABLES.get();
        GAMEPLAY.HARDCORE_PIPES = COMMON_CONFIG.HARDCORE_PIPES.get();
        GAMEPLAY.SMARTER_TREE_DETECTION = COMMON_CONFIG.SMARTER_TREE_DETECTION.get();
        GAMEPLAY.AXE_TIMBER = COMMON_CONFIG.AXE_TIMBER.get();
        GAMEPLAY.AXE_TIMBER_MAX = COMMON_CONFIG.AXE_TIMBER_MAX.get();
        GAMEPLAY.PLAY_CRAFTING_SOUNDS = COMMON_CONFIG.PLAY_CRAFTING_SOUNDS.get();

        MOD_COMPAT.ENABLE_ALL_REGISTRARS = COMMON_CONFIG.ENABLE_ALL_REGISTRARS.get();

    }
}
