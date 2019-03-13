package muramasa.gregtech;

import muramasa.gregtech.client.creativetab.GregTechTab;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class Ref {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static Random RNG = new Random();

    public static boolean showAllItems = true;

    //Mod Data
    public static final String MODID = "gregtech";
    public static final String NAME = "GregTech";
    public static final String VERSION = "1.00.01";

    //Creative Tabs
    public static GregTechTab TAB_MATERIALS = new GregTechTab("materials", new ItemStack(Items.IRON_INGOT));
    public static GregTechTab TAB_ITEMS = new GregTechTab("items", new ItemStack(Items.BLAZE_ROD));
    public static GregTechTab TAB_BLOCKS = new GregTechTab("blocks", new ItemStack(Blocks.IRON_BLOCK));
    public static GregTechTab TAB_MACHINES = new GregTechTab("machines", new ItemStack(Blocks.FURNACE));

    //GUI IDs
    public static final int MACHINE_ID = 0;
    public static final int MULTI_MACHINE_ID = 1;
    public static final int HATCH_ID = 2;

    //Config Values //TODO maybe move?
    public static boolean BASIC_MACHINE_MODELS = false;

    public static boolean ENABLE_RECIPE_DEBUG_EXCEPTIONS = true;

    public static boolean mMixedOreOnlyYieldsTwoThirdsOfPureOre = false;
    public static boolean mDisableOldChemicalRecipes = false;
    public static boolean ENABLE_ITEM_REPLACEMENTS = true;

    //NBT Tags & Keys
    public static final String KEY_STACK_CHANCE = "chance";
    public static final String KEY_STACK_NO_CONSUME = "noconsume";

    public static final String KEY_MACHINE_STACK_TIER = "st2";

    public static final String KEY_MACHINE_TILE_TIER = "m2";
    public static final String KEY_MACHINE_TILE_FACING = "mf";
    public static final String KEY_MACHINE_TILE_STATE = "ms";
    public static final String KEY_MACHINE_TILE_TINT = "mc";
    public static final String KEY_MACHINE_TILE_TEXTURE = "mt";
    public static final String KEY_MACHINE_TILE_ITEMS = "mit";
    public static final String KEY_MACHINE_TILE_FLUIDS = "mfl";

    public static final String TAG_TOOL_DATA = "toolstats";
    public static final String KEY_TOOL_DATA_TYPE = "t";
    public static final String KEY_TOOL_DATA_QUALITY = "q";
    public static final String KEY_TOOL_DATA_PRIMARY_MAT = "pm";
    public static final String KEY_TOOL_DATA_SECONDARY_MAT = "sm";
    public static final String KEY_TOOL_DATA_ENERGY = "e";
    public static final String KEY_TOOL_DATA_MAX_ENERGY = "me";
    public static final String KEY_TOOL_DATA_DURABILITY = "d";
    public static final String KEY_TOOL_DATA_MAX_DURABILITY = "md";
    public static final String KEY_TOOL_DATA_ATTACK_SPEED = "as";
    public static final String KEY_TOOL_DATA_ATTACK_DAMAGE = "ad";
    public static final String KEY_TOOL_DATA_MINING_SPEED = "ms";
}
