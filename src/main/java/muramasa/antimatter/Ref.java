package muramasa.antimatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import muramasa.antimatter.client.itemgroup.AntimatterItemGroup;
import muramasa.antimatter.util.XSTR;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;

import java.io.File;

public class Ref {

    /** Global Objects **/
    public static File CONFIG = null;
    public static XSTR RNG = new XSTR();
    public static Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    /** Mod Data **/
    public static final String ID = "antimatter";
    public static final String NAME = "AntimatterAPI";
    public static final String VERSION = "0.0.1";
    public static final String DEPENDS = "";

    /** Creative Tabs **/
    public static AntimatterItemGroup TAB_ITEMS = new AntimatterItemGroup(Ref.ID, "items", () -> new ItemStack(Items.NETHER_BRICK));
    public static AntimatterItemGroup TAB_BLOCKS = new AntimatterItemGroup(Ref.ID, "blocks", () -> new ItemStack(Items.NETHER_BRICK));
    public static AntimatterItemGroup TAB_MATERIALS = new AntimatterItemGroup(Ref.ID, "materials", () -> new ItemStack(Items.NETHER_BRICK));
    public static AntimatterItemGroup TAB_MACHINES = new AntimatterItemGroup(Ref.ID, "machines", () -> new ItemStack(Items.NETHER_BRICK));

    /** Global Data **/
    public static final int M = 3628800;
    public static final int[] V = new int[]{8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    public static final String[] VN = new String[]{"ULV", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "MAX", "", "", "", "", "", ""}; //TODO: Schedule to change? ZPM rename? Tier decisions?
    public static final Direction[] DIRECTIONS = Direction.values();

    /** Internal Options **/
    public static boolean INJECT_RESOURCES = true; //Attach and inject data with a custom resource pack
    public static boolean GENERATE_RESOURCES = false; //Generate resources with providers

    /** Debug Options **/
    public static boolean GENERAL_DEBUG = System.getenv("devEnvironment") != null;
    public static boolean SHOW_STACK_ORE_DICT = true;
    public static boolean DATA_EXCEPTIONS = false; //TODO re-enable

    //TODO maybe use these later
    public static boolean debugWorldGen = false;
    public static boolean debugOreVein = true;
    public static boolean debugSmallOres = true;
    public static boolean debugStones = true;

    /** NBT Tags & Keys **/
    public static final String KEY_STACK_NO_CONSUME = "noconsume";

    public static final String KEY_MACHINE_TILE_FACING = "mf";
    public static final String KEY_MACHINE_TILE_STATE = "ms";
    public static final String KEY_MACHINE_TILE_TINT = "mc";
    public static final String KEY_MACHINE_TILE_TEXTURE = "mt";
    public static final String KEY_MACHINE_TILE_ITEMS = "mit";
    public static final String KEY_MACHINE_TILE_FLUIDS = "mfl";

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
    public static final String MOD_UB = "undergroundbiomes";

    /** Dimension IDs **/
    public static final int OVERWORLD = 0;
    public static final int NETHER = -1;
    public static final int END = 1;
    public static final int MOON = -99; //TODO, Find ID
    public static final int MARS = -99; //TODO, Find ID
    public static final int ASTEROIDS = -30; //TODO, Validate ID
}
