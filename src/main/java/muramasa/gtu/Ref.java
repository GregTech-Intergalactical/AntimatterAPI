package muramasa.gtu;

import muramasa.gtu.client.creativetab.GregTechTab;

import java.util.Random;

public class Ref {

    /** Global Objects **/
    public static Random RNG = new Random();

    /** Mod Data **/
    public static final String MODID = "gtu";
    public static final String NAME = "GregTech";
    public static final String VERSION = "0.1.0";
    public static final String DEPENDS = "required-after:crafttweaker";
    public static final String CLIENT = "muramasa.gtu.proxy.ClientProxy";
    public static final String SERVER = "muramasa.gtu.proxy.ServerProxy";

    /** Creative Tabs **/
    public static GregTechTab TAB_ITEMS = new GregTechTab("items");
    public static GregTechTab TAB_BLOCKS = new GregTechTab("blocks");
    public static GregTechTab TAB_MATERIALS = new GregTechTab("materials");
    public static GregTechTab TAB_MACHINES = new GregTechTab("machines");

    /** GUI IDs **/
    public static final int GUI_ID_MACHINE = 0;
    public static final int GUI_ID_MULTI_MACHINE = 1;
    public static final int GUI_ID_HATCH = 2;

    /** Global Data **/
    public static final int[] V = new int[]{8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    public static final String[] VN = new String[]{"ULV", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "MAX", "", "", "", "", "", ""}; //TODO: Schedule to change? ZPM rename? Tier decisions?

    /** Debug Options **/
    public static boolean SHOW_STACK_ORE_DICT = true;
    public static boolean DATA_EXCEPTIONS = true;

    /** Config Values **/
    public static boolean mixedOreYieldsTwoThirdsPureOre = false; //TODO 5U remnant, determine if needed

    public static boolean debugWorldGen = false;
    public static boolean debugOreVein = true;
    public static boolean debugStones = true;

    public static int oreveinPercentage = 100; //Control percentage of filled 3x3 chunks. Lower number means less oreveins spawn
    public static int oreveinAttempts = 64; //Control number of attempts to find a valid orevein. Generally this maximum limit isn't hit, selecting a vein is cheap
    public static int oreveinMaxPlacementAttempts = 8; //Control number of attempts to place a valid orevein.  If a vein wasn't placed due to height restrictions, completely in the water, etc, another attempt is tried.
    public static boolean oreveinPlacerOres = true; //Whether or not to place small ores as placer ores for an orevein
    public static int oreveinPlacerOresMultiplier = 2; //Multiplier to control how many placer ores get generated.

    /** NBT Tags & Keys **/
    public static final String KEY_STACK_CHANCE = "chance";
    public static final String KEY_STACK_NO_CONSUME = "noconsume";

    public static final String KEY_MACHINE_STACK_TIER = "st2";

    public static final String KEY_PIPE_STACK_SIZE = "pss";
    public static final String KEY_CABLE_STACK_INSULATED = "csi";
    public static final String KEY_ITEM_PIPE_STACK_RESTRICTIVE = "ipsr";

    public static final String KEY_MACHINE_TILE_FACING = "mf";
    public static final String KEY_MACHINE_TILE_STATE = "ms";
    public static final String KEY_MACHINE_TILE_TINT = "mc";
    public static final String KEY_MACHINE_TILE_TEXTURE = "mt";
    public static final String KEY_MACHINE_TILE_ITEMS = "mit";
    public static final String KEY_MACHINE_TILE_FLUIDS = "mfl";

    public static final String KEY_PIPE_CONNECTIONS = "pc";
    public static final String KEY_CABLE_INSULATED = "ci";

    public static final String TAG_TOOL_DATA = "tooldata";
    public static final String KEY_TOOL_DATA_PRIMARY_MAT = "pm";
    public static final String KEY_TOOL_DATA_SECONDARY_MAT = "sm";
    public static final String KEY_TOOL_DATA_ENERGY = "e";
    public static final String KEY_TOOL_DATA_MAX_ENERGY = "me";
    public static final String KEY_TOOL_DATA_DURABILITY = "d";

    /** Model Cache IDs **/
    public static final int CACHE_ID_MACHINE = 1;
    public static final int CACHE_ID_PIPE = 2;
    public static final int CACHE_ID_FLUID_CELL = 3;

    /** Mod IDs **/
    public static final String MOD_JEI = "jei";
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
}
