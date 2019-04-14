package muramasa.gtu;

import muramasa.gtu.client.creativetab.GregTechTab;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class Ref {

    /** Global Objects **/
    public static Minecraft MC = Minecraft.getMinecraft();
    public static Random RNG = new Random();

    /** Mod Data **/
    public static final String MODID = "gtu";
    public static final String NAME = "GregTech";
    public static final String VERSION = "0.00.01";
    public static final String CLIENT = "muramasa.gtu.proxy.ClientProxy";
    public static final String SERVER = "muramasa.gtu.proxy.ServerProxy";

    /** Creative Tabs **/
    public static GregTechTab TAB_MATERIALS = new GregTechTab("materials", new ItemStack(Items.IRON_INGOT));
    public static GregTechTab TAB_ITEMS = new GregTechTab("items", new ItemStack(Items.BLAZE_ROD));
    public static GregTechTab TAB_BLOCKS = new GregTechTab("blocks", new ItemStack(Blocks.IRON_BLOCK));
    public static GregTechTab TAB_MACHINES = new GregTechTab("machines", new ItemStack(Blocks.FURNACE));

    /** GUI IDs **/
    public static final int GUI_ID_MACHINE = 0;
    public static final int GUI_ID_MULTI_MACHINE = 1;
    public static final int GUI_ID_HATCH = 2;

    /** Global Data **/
    public static final int[] V = new int[]{8, 32, 128, 512, 2048, 8192, 32768, 131072, 524288, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    public static final int MB_INGOT = 144;

    /** Config Values **/
    public static boolean HARDCORE_CABLES = true;
    public static boolean showAllItems = true;
    public static boolean enableAllModItem = true;
    public static boolean ENABLE_ALL_REGISTRARS = true;
    public static boolean BASIC_MACHINE_MODELS = false;
    public static boolean RECIPE_EXCEPTIONS = true;
    public static boolean mMixedOreOnlyYieldsTwoThirdsOfPureOre = false;
    public static boolean mDisableOldChemicalRecipes = false;
    public static boolean ENABLE_ITEM_REPLACEMENTS = true;
    public static boolean DISABLE_VANILLA_ORE_GENERATION = true;
    public static boolean DISABLE_VANILLA_STONE_GENERATION = false;

    /** Tools **/
    public static boolean AXE_TIMBER = true;
    public static int MAX_AXE_TIMBER = 20;

    /** NBT Tags & Keys **/
    public static final String KEY_STACK_CHANCE = "chance";
    public static final String KEY_STACK_NO_CONSUME = "noconsume";

    public static final String KEY_MACHINE_STACK_TIER = "st2";

    public static final String KEY_PIPE_STACK_TYPE = "pst";
    public static final String KEY_PIPE_STACK_SIZE = "pss";
    public static final String KEY_CABLE_STACK_INSULATED = "csi";
    public static final String KEY_ITEM_PIPE_STACK_RESTRICTIVE = "ipsr";

    public static final String KEY_MACHINE_TILE_TIER = "m2";
    public static final String KEY_MACHINE_TILE_FACING = "mf";
    public static final String KEY_MACHINE_TILE_STATE = "ms";
    public static final String KEY_MACHINE_TILE_TINT = "MC";
    public static final String KEY_MACHINE_TILE_TEXTURE = "mt";
    public static final String KEY_MACHINE_TILE_ITEMS = "mit";
    public static final String KEY_MACHINE_TILE_FLUIDS = "mfl";

    public static final String KEY_PIPE_SIZE = "ps";
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
    public static final String MOD_FR = "forestry";
    public static final String MOD_IC2 = "ic2";
    public static final String MOD_IC2C = "ic2-classic-spmod";
    public static final String MOD_AE = "appliedenergistics2";
    public static final String MOD_GC = "GalacticraftCore";
    public static final String MOD_GC_PLANETS = "GalacticraftPlanets";
}
