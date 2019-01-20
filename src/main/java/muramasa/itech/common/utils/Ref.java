package muramasa.itech.common.utils;

import net.minecraft.client.Minecraft;

import java.util.Random;

public class Ref {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static Random rng = new Random();

    public static boolean showAllItemsInCreative = true;

    //GUI IDs
    public static final int MACHINE_ID = 0;
    public static final int MULTI_MACHINE_ID = 1;
    public static final int HATCH_ID = 2;

    //NBT Keys
    public static final String TAG_MACHINE_STACK_DATA = "machinestack";
    public static final String KEY_MACHINE_STACK_TYPE = "st1";
    public static final String KEY_MACHINE_STACK_TIER = "st2";

    public static final String TAG_MACHINE_TILE_DATA = "machinetile";
    public static final String KEY_MACHINE_TILE_TYPE = "m1";
    public static final String KEY_MACHINE_TILE_TIER = "m2";
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
