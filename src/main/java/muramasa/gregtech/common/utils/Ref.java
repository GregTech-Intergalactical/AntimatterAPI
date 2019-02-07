package muramasa.gregtech.common.utils;

import muramasa.gregtech.client.creativetab.GregTechTab;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class Ref {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static Random rng = new Random();

    public static boolean showAllItems = true;

    //Mod Data
    public static final String MODID = "gregtech";
    public static final String NAME = "GregTech Unofficial";
    public static final String VERSION = "0.1";

    //Creative Tabs
    public static final GregTechTab TAB_MATERIALS = new GregTechTab("materials", new ItemStack(Items.IRON_INGOT)); //TODO move to Ref
    public static final GregTechTab TAB_MACHINES = new GregTechTab("machines", new ItemStack(Blocks.FURNACE));
    public static final GregTechTab TAB_ORES = new GregTechTab("ores", new ItemStack(Blocks.DIAMOND_ORE));

    //GUI IDs
    public static final int MACHINE_ID = 0;
    public static final int MULTI_MACHINE_ID = 1;
    public static final int HATCH_ID = 2;

    //Config Values //TODO maybe move?
    public static final boolean mMixedOreOnlyYieldsTwoThirdsOfPureOre = false;

    //NBT Tags & Keys
    public static final String TAG_MACHINE_STACK_DATA = "machinestack";
    public static final String KEY_MACHINE_STACK_TYPE = "st1";
    public static final String KEY_MACHINE_STACK_TIER = "st2";

    public static final String TAG_MACHINE_TILE_DATA = "machinetile";
    public static final String KEY_MACHINE_TILE_TYPE = "m1";
    public static final String KEY_MACHINE_TILE_TIER = "m2";
    public static final String KEY_MACHINE_TILE_FACING = "mf";
    public static final String KEY_MACHINE_TILE_STATE = "ms";
    public static final String KEY_MACHINE_TILE_TINT = "mc";
    public static final String KEY_MACHINE_TILE_TEXTURE = "mt";
    public static final String KEY_MACHINE_TILE_ITEMS = "mi";

    public static final String TAG_MULTIMACHINE_TILE_DATA = "multimachinetile";
    public static final String KEY_MULTIMACHINE_TILE_TYPE = "m1";

    public static final String TAG_COMPONENT_TILE_DATA = "componenttile";
    public static final String KEY_COMPONENT_TILE_TYPE = "c1";

    public static final String KEY_FLUID_NAME_1 = "fn1";
    public static final String KEY_FLUID_NAME_2 = "fn2";
    public static final String KEY_FLUID_AMOUNT_1 = "fa1";
    public static final String KEY_FLUID_AMOUNT_2 = "fa2";

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
