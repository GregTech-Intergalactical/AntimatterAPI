package muramasa.antimatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    /**
     * Mod Data
     **/
    public static final String ID = "antimatter";
    public static final String NAME = "AntimatterAPI";
    public static final String SHARED_ID = "antimatter_shared";

    /**
     * Creative Tabs
     **/
    public static final CreativeModeTab TAB_ITEMS = AntimatterPlatformUtils.INSTANCE.createTab(ID, "items", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_TOOLS = AntimatterPlatformUtils.INSTANCE.createTab(ID, "tools", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_BLOCKS = AntimatterPlatformUtils.INSTANCE.createTab(ID, "blocks", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_MATERIALS = AntimatterPlatformUtils.INSTANCE.createTab(ID, "materials", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final CreativeModeTab TAB_MACHINES = AntimatterPlatformUtils.INSTANCE.createTab(ID, "machines", () -> new ItemStack(Data.DEBUG_SCANNER));

    /**
     * Sound Events
     **/
    public static final SoundEvent DRILL = AntimatterAPI.register(SoundEvent.class, "drill", ID,  new SoundEvent(new ResourceLocation(ID, "drill")));
    public static final SoundEvent WRENCH = AntimatterAPI.register(SoundEvent.class, "wrench", ID,  new SoundEvent(new ResourceLocation(ID, "wrench")));
    public static final SoundEvent JOHN_CENA = AntimatterAPI.register(SoundEvent.class, "john-cena", ID,  new SoundEvent(new ResourceLocation(ID, "john-cena")));
    public static final SoundEvent INTERRUPT = AntimatterAPI.register(SoundEvent.class, "interrupt", ID,  new SoundEvent(new ResourceLocation(ID, "interrupt")));

    /**
     * Global Data
     **/
    public static final long U = 18144000, U2 = U / 2, U3 = U / 3, U4 = U / 4, U5 = U / 5, U6 = U / 6, U7 = U / 7, U8 = U / 8, U9 = U / 9, U10 = U / 10, U11 = U / 11, U12 = U / 12, U13 = U / 13, U14 = U / 14, U15 = U / 15, U16 = U / 16, U17 = U / 17, U18 = U / 18, U20 = U / 20, U24 = U / 24, U25 = U / 25, U32 = U / 32, U36 = U / 36, U40 = U / 40, U48 = U / 48, U50 = U / 50, U64 = U / 64, U72 = U / 72, U80 = U / 80, U96 = U / 96, U100 = U / 100, U128 = U / 128, U144 = U / 144, U192 = U / 192, U200 = U / 200, U240 = U / 240, U256 = U / 256, U288 = U / 288, U480 = U / 480, U500 = U / 500, U512 = U / 512, U1000 = U / 1000, U9000 = U/9000, U1440 = U / 1440, U81000 = U/81000;
    //TODO change this to long and add values up to tier 15(not for gti itself, but for modpacks and compat)
    public static final long[] V = new long[]{8, 32, 128, 512, 2048, 8192, 32768, 131_072, 524_288, 2_097_152, 8_388_608, 35_544_432, 134_217_728, 536_870_912, 2_147_483_648L, 8_589_934_592L};
    public static final String[] VN = new String[]{"ULV", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "UMV", "UXV", "MAX", "∞"}; //TODO: Schedule to change? ZPM rename? Tier decisions?
    public static final Direction[] DIRS = Direction.values();
    /** The first 32 Bits */
    public static final int[] B = {1<<0,1<<1,1<<2,1<<3,1<<4,1<<5,1<<6,1<<7,1<<8,1<<9,1<<10,1<<11,1<<12,1<<13,1<<14,1<<15,1<<16,1<<17,1<<18,1<<19,1<<20,1<<21,1<<22,1<<23,1<<24,1<<25,1<<26,1<<27,1<<28,1<<29,1<<30,1<<31};
    /**
     * Fluid per Material Unit (Prime Factors: Forge: 3 * 3 * 2 * 2 * 2 * 2 Fabric: 5 * 5 * 5 * 3 * 3 * 2 * 2 * 2)
     */
    public static final long L = AntimatterPlatformUtils.isForge() ? 144L : 9000L, L9 = L / 9;


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

    public static final String KEY_MACHINE_ITEMS = "it";
    public static final String KEY_MACHINE_STATE = "s";
    public static final String KEY_MACHINE_STATE_D = "sd";
    public static final String KEY_MACHINE_MUFFLED = "muf";
    public static final String KEY_MACHINE_EJECT_FLUID = "mef";
    public static final String KEY_MACHINE_EJECT_ITEM = "mei";

    public static final String KEY_MACHINE_TINT = "mc";
    public static final String KEY_MACHINE_TEXTURE = "mt";
    public static final String KEY_MACHINE_FLUIDS = "fl";
    public static final String KEY_MACHINE_ENERGY = "en";
    public static final String KEY_MACHINE_RF = "rf";
    public static final String KEY_MACHINE_HEAT = "he";
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
    public static final String TAG_PIPE_TILE_VIRTUAL_CONNECTIVITY = "vc";
    public static final String TAG_PIPE_TILE_INTERACT = "i";
    public static final String KEY_PIPE_TILE_COVER = "v";
    public static final String KEY_PIPE_TILE_CONFIG = "f";
    public static final String KEY_PIPE_TILE_COLOR = "co";

    public static final String TAG_TOOL_DATA = "td";
    public static final String KEY_TOOL_DATA_PRIMARY_MATERIAL = "m";
    public static final String KEY_TOOL_DATA_SECONDARY_MATERIAL = "sm";
    public static final String KEY_TOOL_DATA_SECONDARY_COLOUR = "sc";
    public static final String KEY_TOOL_DATA_TIER = "t";

    public static final String KEY_TOOL_BEHAVIOUR_AOE_BREAK = "aoe";

    public static final String TAG_ITEM_ENERGY_DATA = "ied";
    public static final String KEY_ITEM_ENERGY = "e";
    public static final String KEY_ITEM_ENERGY_OLD = "ie";
    public static final String KEY_ITEM_MAX_ENERGY = "me";
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
    public static final String MOD_CREATE = "create";
    public static final String MOD_JEI = "jei";
    public static final String MOD_REI = "roughlyenoughitems";
    public static final String MOD_TOP = "theoneprobe";
    public static final String MOD_CT = "crafttweaker";
    public static final String MOD_FR = "forestry";
    public static final String MOD_IC2 = "ic2";
    public static final String MOD_IC2C = "ic2c";
    public static final String MOD_AE = "ae2";
    public static final String MOD_GC = "galacticraft";
    public static final String MOD_GC_PLANETS = "GalacticraftPlanets";
    public static final String MOD_TE = "thermalexpansion";
    public static final String MOD_TF = "thermalfoundation";
    public static final String MOD_UB = "undergroundbiomes";
    public static final String MOD_TWILIGHT = "twilightforest";
    public static final String MOD_TFC = "tfc";
    public static final String MOD_KJS = "kubejs";

    /**
     * Dimension IDs
     **/
    public static final String OVERWORLD = "minecraft:overworld";
    public static final String NETHER = "minecraft:the_nether";
    public static final String END = "minecraft:the_end";
    public static final String TWILIGHT_FOREST = "twilightforest:twilight_forest";
}
