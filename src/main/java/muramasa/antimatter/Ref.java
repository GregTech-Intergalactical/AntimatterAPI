package muramasa.antimatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import muramasa.antimatter.client.itemgroup.AntimatterItemGroup;
import muramasa.antimatter.util.XSTR;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.io.File;

public class Ref {

    /** Global Objects **/
    public static File CONFIG = null;
    public static XSTR RNG = new XSTR();
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Mod Data **/
    public static final String ID = "antimatter";
    public static final String NAME = "AntimatterAPI";
    public static final String VERSION = "0.0.1";
    public static final String DEPENDS = "";

    /** Creative Tabs **/
    public static final AntimatterItemGroup TAB_ITEMS = new AntimatterItemGroup(ID, "items", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final AntimatterItemGroup TAB_TOOLS = new AntimatterItemGroup(ID, "tools", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final AntimatterItemGroup TAB_BLOCKS = new AntimatterItemGroup(ID, "blocks", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final AntimatterItemGroup TAB_MATERIALS = new AntimatterItemGroup(ID, "materials", () -> new ItemStack(Data.DEBUG_SCANNER));
    public static final AntimatterItemGroup TAB_MACHINES = new AntimatterItemGroup(ID, "machines", () -> new ItemStack(Data.DEBUG_SCANNER));

    /** Sound Events **/
    public static final SoundEvent DRILL = new SoundEvent(new ResourceLocation(ID, "drill")).setRegistryName(ID, "drill");
    public static final SoundEvent WRENCH = new SoundEvent(new ResourceLocation(ID, "wrench")).setRegistryName(ID, "wrench");

    /** Global Data **/
    public static final int U = 3628800, U2 = U/2, U3 = U/3, U4 = U/4, U5 = U/5, U6 = U/6, U7 = U/7, U8 = U/8, U9 = U/9, U10 = U/10, U11 = U/11, U12 = U/12, U13 = U/13, U14 = U/14, U15 = U/15, U16 = U/16, U17 = U/17, U18 = U/18, U20 = U/20, U24 = U/24, U25 = U/25, U32 = U/32, U36 = U/36, U40 = U/40, U48 = U/48, U50 = U/50, U64 = U/64, U72 = U/72, U80 = U/80, U96 = U/96, U100 = U/100, U128 = U/128, U144 = U/144, U192 = U/192, U200 = U/200, U240 = U/240, U256 = U/256, U288 = U/288, U480 = U/480, U500 = U/500, U512 = U/512, U1000 = U/1000, U1440 = U/1440;
    public static final int[] V = new int[]{8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    public static final String[] VN = new String[]{"ULV", "LV", "MV", "HV", "EV", "IV", "LuV", "ZPM", "UV", "MAX", "", "", "", "", "", ""}; //TODO: Schedule to change? ZPM rename? Tier decisions?
    public static final Direction[] DIRECTIONS = Direction.values();

    /** Internal Options **/
    public static boolean INJECT_RESOURCES = true; //Attach and inject data with a custom resource pack
    public static boolean GENERATE_RESOURCES = false; //Generate resources with providers

    /** Debug Options **/
    public static boolean GENERAL_DEBUG = System.getenv("devEnvironment") != null;
    public static boolean SHOW_ITEM_TAGS = true;
    public static boolean DATA_EXCEPTIONS = false; //TODO re-enable

    //TODO maybe use these later
    public static boolean debugWorldGen = false;
    public static boolean debugOreVein = false;
    public static boolean debugSmallOres = false;
    public static boolean debugStones = false;

    /** NBT Tags & Keys **/
    public static final String KEY_STACK_NO_CONSUME = "noconsume";

    public static final String KEY_MACHINE_TILE_STATE = "ms";
    public static final String KEY_MACHINE_TILE_TINT = "mc";
    public static final String KEY_MACHINE_TILE_TEXTURE = "mt";
    public static final String KEY_MACHINE_TILE_ITEMS = "mit";
    public static final String KEY_MACHINE_TILE_FLUIDS = "mfl";
    public static final String KEY_MACHINE_TILE_ENERGY = "men";
    public static final String KEY_MACHINE_TILE_RECIPE = "mre";
    public static final String KEY_MACHINE_TILE_COVER = "mco";

    public static final String KEY_PIPE_TILE_CONNECTIVITY = "pc";
    public static final String KEY_PIPE_TILE_COVER = "pco";

    public static final String TAG_TOOL_DATA = "tooldata";
    public static final String KEY_TOOL_DATA_PRIMARY_MATERIAL = "m";
    public static final String KEY_TOOL_DATA_SECONDARY_MATERIAL = "sm";
    public static final String KEY_TOOL_DATA_SECONDARY_COLOUR = "sc";
    public static final String KEY_TOOL_DATA_TIER = "t";
    public static final String KEY_TOOL_DATA_ENERGY = "e";
    public static final String KEY_TOOL_DATA_MAX_ENERGY = "me";

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
