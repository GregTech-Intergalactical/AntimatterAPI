package muramasa.antimatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import muramasa.antimatter.datagen.BackgroundDataGenerator;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.XSTR;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class Ref {

    /**
     * Global Objects
     **/
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final XSTR RNG = new XSTR();
    public static final BackgroundDataGenerator BACKGROUND_GEN = new BackgroundDataGenerator();
    //public static final DynamicResourcePackFinder PACK_FINDER = new DynamicResourcePackFinder("antimatter_pack", "Antimatter - Dynamic Assets", "Dynamic Resource Pack", true);
    //public static final DynamicDataPackFinder SERVER_PACK_FINDER = new DynamicDataPackFinder("antimatter_pack", "Antimatter - Dynamic Data");

    /**
     * Mod Data
     **/
    public static final String ID = "antimatter";
    public static final String NAME = "AntimatterAPI";
    public static final String SHARED_ID = "antimatter_shared";

    /**
     * Creative Tabs
     **/
    public static final CreativeModeTab TAB_ITEMS = AntimatterPlatformUtils.createTab(ID, "items", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_TOOLS = AntimatterPlatformUtils.createTab(ID, "tools", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_BLOCKS = AntimatterPlatformUtils.createTab(ID, "blocks", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_MATERIALS = AntimatterPlatformUtils.createTab(ID, "materials", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_MACHINES = AntimatterPlatformUtils.createTab(ID, "machines", () -> new ItemStack(Data.DEBUG_SCANNER));

    /**
     * Sound Events
     **/
    public static final SoundEvent DRILL = AntimatterAPI.register(SoundEvent.class, ID, "drill",  new SoundEvent(new ResourceLocation(ID, "drill")));
    public static final SoundEvent WRENCH = AntimatterAPI.register(SoundEvent.class, ID, "wrench",  new SoundEvent(new ResourceLocation(ID, "wrench")));
    public static final SoundEvent INTERRUPT = AntimatterAPI.register(SoundEvent.class, ID, "interrupt",  new SoundEvent(new ResourceLocation(ID, "interrupt")));

    /**
     * Global Data
     **/
    public static final int U = 3628800, U2 = U / 2, U3 = U / 3, U4 = U / 4, U5 = U / 5, U6 = U / 6, U7 = U / 7, U8 = U / 8, U9 = U / 9, U10 = U / 10, U11 = U / 11, U12 = U / 12, U13 = U / 13, U14 = U / 14, U15 = U / 15, U16 = U / 16, U17 = U / 17, U18 = U / 18, U20 = U / 20, U24 = U / 24, U25 = U / 25, U32 = U / 32, U36 = U / 36, U40 = U / 40, U48 = U / 48, U50 = U / 50, U64 = U / 64, U72 = U / 72, U80 = U / 80, U96 = U / 96, U100 = U / 100, U128 = U / 128, U144 = U / 144, U192 = U / 192, U200 = U / 200, U240 = U / 240, U256 = U / 256, U288 = U / 288, U480 = U / 480, U500 = U / 500, U512 = U / 512, U1000 = U / 1000, U1440 = U / 1440;
    public static final int[] V = new int[]{8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    public static final String[] VN = new String[]{"ULV", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "MAX", "", "", "", "", "", ""}; //TODO: Schedule to change? ZPM rename? Tier decisions?
    public static final Direction[] DIRS = Direction.values();

    /**
     * Debug Options
     **/
    public static boolean SHOW_ITEM_TAGS = true;
    public static boolean DATA_EXCEPTIONS = false; //TODO re-enable + config option

    //TODO maybe use these later
    public static boolean debugWorldGen = false;
    public static boolean debugOreVein = false;
    public static boolean debugSmallOres = false;
    public static boolean debugStones = false;
    /**
     * Texture related keys.
     **/

    public static final String KEY_MULTI_TEXTURE = "kmt";

    /**
     * NBT Tags & Keys
     **/
    public static final String KEY_STACK_NO_CONSUME = "nc";
    public static final String KEY_STACK_IGNORE_NBT = "inb";

    public static final String KEY_MACHINE_TIER = "t";
    public static final String KEY_MACHINE_ITEMS = "it";
    public static final String KEY_MACHINE_STATE = "s";
    public static final String KEY_MACHINE_STATE_D = "sd";
    public static final String KEY_MACHINE_EJECT_FLUID = "mef";
    public static final String KEY_MACHINE_EJECT_ITEM = "mei";

    public static final String KEY_MACHINE_TINT = "mc";
    public static final String KEY_MACHINE_TEXTURE = "mt";
    public static final String KEY_MACHINE_FLUIDS = "fl";
    public static final String KEY_MACHINE_ENERGY = "en";
    public static final String KEY_MACHINE_RECIPE = "re";
    public static final String KEY_MACHINE_COVER = "co";
    public static final String KEY_MACHINE_INTERACT = "ci";
    public static final String TAG_MACHINE_STATE = "ms";
    public static final String TAG_MACHINE_HEAT = "mh";
    public static final String TAG_MACHINE_ENERGY = "me";
    public static final String TAG_MACHINE_CAPACITY = "cp";
    public static final String TAG_MACHINE_VOLTAGE_IN = "vi";
    public static final String TAG_MACHINE_VOLTAGE_OUT = "vo";
    public static final String TAG_MACHINE_AMPERAGE_IN = "ai";
    public static final String TAG_MACHINE_AMPERAGE_OUT = "ao";
    public static final String TAG_MACHINE_INPUT_FLUID = "if";
    public static final String TAG_MACHINE_OUTPUT_FLUID = "of";
    public static final String TAG_MACHINE_SLOT_SIZE = "sz";
    public static final String TAG_MACHINE_INPUT_ITEM = "ii";
    public static final String TAG_MACHINE_INPUT_SIZE = "is";
    public static final String TAG_MACHINE_OUTPUT_ITEM = "oi";
    public static final String TAG_MACHINE_OUTPUT_SIZE = "os";
    public static final String TAG_MACHINE_CELL_ITEM = "ci";
    public static final String TAG_MACHINE_CELL_SIZE = "cs";
    public static final String TAG_MACHINE_CHARGE_ITEM = "cg";
    public static final String TAG_MACHINE_CHARGE_SIZE = "gs";
    public static final String TAG_MACHINE_COVER_ID = "cv";
    public static final String TAG_MACHINE_COVER_DOMAIN = "cd";
    public static final String TAG_MACHINE_COVER_NAME = "cn";
    public static final String TAG_MACHINE_COVER_SIDE = "vs";

    public static final String TAG_PIPE_TILE_CONNECTIVITY = "c";
    public static final String TAG_PIPE_TILE_INTERACT = "i";
    public static final String KEY_PIPE_TILE_COVER = "v";
    public static final String KEY_PIPE_TILE_CONFIG = "f";

    public static final String TAG_TOOL_DATA = "td";
    public static final String KEY_TOOL_DATA_PRIMARY_MATERIAL = "m";
    public static final String KEY_TOOL_DATA_SECONDARY_MATERIAL = "sm";
    public static final String KEY_TOOL_DATA_SECONDARY_COLOUR = "sc";
    public static final String KEY_TOOL_DATA_TIER = "t";
    public static final String KEY_TOOL_DATA_ENERGY = "e";
    public static final String KEY_TOOL_DATA_MAX_ENERGY = "me";

    public static final String KEY_ITEM_ENERGY = "ie";
    public static final String KEY_ITEM_DISCHARGE_MODE = "idm";

    /**
     * Model Cache IDs
     **/
    public static final int CACHE_ID_MACHINE = 1;
    public static final int CACHE_ID_PIPE = 2;
    public static final int CACHE_ID_FLUID_CELL = 3;

    /**
     * Mod IDs
     **/
    public static final String MOD_JEI = "jei";
    public static final String MOD_REI = "roughlyenoughitems";
    public static final String MOD_TOP = "theoneprobe";
    public static final String MOD_CT = "crafttweaker";
    public static final String MOD_FR = "forestry";
    public static final String MOD_IC2 = "ic2";
    public static final String MOD_IC2C = "ic2-classic-spmod";
    public static final String MOD_AE = "appliedenergistics2";
    public static final String MOD_GC = "GalacticraftCore";
    public static final String MOD_GC_PLANETS = "GalacticraftPlanets";
    public static final String MOD_TE = "thermalexpansion";
    public static final String MOD_TF = "thermalfoundation";
    public static final String MOD_UB = "undergroundbiomes";
    public static final String MOD_KJS = "kubejs";

    /**
     * Dimension IDs
     **/
    public static final int OVERWORLD = 0;
    public static final int NETHER = -1;
    public static final int END = 1;
    public static final int MOON = -99; //TODO, Find ID
    public static final int MARS = -99; //TODO, Find ID
    public static final int ASTEROIDS = -30; //TODO, Validate ID
}
