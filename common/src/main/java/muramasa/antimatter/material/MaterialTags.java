package muramasa.antimatter.material;

import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import net.minecraft.world.level.block.OreBlock;

public class MaterialTags {

    //TODO move to GTI
    public static final MaterialTag ELEC = new MaterialTag("elec"); //Add Electrolyzer Recipes - SHOULD NOT SHARE MATS WITH CENT
    public static final MaterialTag CENT = new MaterialTag("cent"); //Add Centrifuging Recipes - SHOULD NOT SHARE MATS WITH ELEC
    public static final MaterialTag CRACK = new MaterialTag("crack"); //Add Cracking Recipes
    public static final MaterialTag SMELTG = new MaterialTag("smelt_g"); //Add Smelting to Gem Recipes
    public static final MaterialTag SMELTF = new MaterialTag("smelt_f"); //Add Smelting to Fluid Recipes
    public static final MaterialTag GRINDABLE = new MaterialTag("grindable"); //Is Grindable with the Mortar
    public static final MaterialTag CRYSTALLIZE = new MaterialTag("crystallize"); //Allows Dust > Gem Recipes
    public static final MaterialTag CALCITE2X = new MaterialTag("calcite_2x"); //Blast Furnace Calcite 2x Multiplier
    public static final MaterialTag CALCITE3X = new MaterialTag("calcite_3x"); //Blast Furnace Calcite 3x Multiplier
    public static final MaterialTag NOSMASH = new MaterialTag("no_smash"); //Material is not able to be smashed
    public static final MaterialTag NOSMELT = new MaterialTag("no_smelt"); //Material is not able to be smelted
    public static final MaterialTag WASHM = new MaterialTag("wash_m"); //Adds Crushed > ByProducts with Mercury
    public static final MaterialTag WASHS = new MaterialTag("wash_s"); //Adds Crushed > ByProducts with Sodium
    public static final MaterialTag NOBBF = new MaterialTag("no_ebf"); //Stops Dust > Ingot in BBF
    public static final MaterialTag ELECSEPI = new MaterialTag("elec_sep_i");
    public static final MaterialTag ELECSEPG = new MaterialTag("elec_sep_g");
    public static final MaterialTag ELECSEPN = new MaterialTag("elec_sep_n");
    public static final MaterialTag SOLDER = new MaterialTag("solder"); //Can be used in Soldering Recipes
    public static final MaterialTag BRITTLEG = new MaterialTag("brittle_g"); //This is for Gems that cannot be used in recipes such as Gem > Rod in Lathe
    public static final MaterialTag RUBBERTOOLS = new MaterialTag("rubber_tools");
    public static final MaterialTag NEEDS_BLAST_FURNACE = new MaterialTag("needs_blast_furnace");
    public static final MaterialTag HAS_CUSTOM_SMELTING = new MaterialTag("has_custom_smelting");
    public static final MaterialTag TRANSPARENT = new MaterialTag("transparent");
    public static final MaterialTag METAL = new MaterialTag("metal");
    public static final MaterialTag ELEMENTAL = new MaterialTag("elemental");
    public static final MaterialTag FLINT = new MaterialTag("flint");

    public static final IntRangeMaterialTag EXP_RANGE = new IntRangeMaterialTag("exp_range");
    /**
     * ETC
     **/
    public static final HandleMaterialTag HANDLE = new HandleMaterialTag();
    /**
     * PIPES
     **/
    public static final MaterialTag ITEMPIPE = new MaterialTag("itempipe");
    public static final MaterialTag FLUIDPIPE = new MaterialTag("fluidpipe");
    public static final MaterialTag WIRE = new MaterialTag("wire");
    public static final MaterialTag CABLE = new MaterialTag("cable");

    public static final DoubleMaterialTag CHEMBATH_MERCURY = new DoubleMaterialTag("chembath_mercury");
    public static final DoubleMaterialTag CHEMBATH_PERSULFATE = new DoubleMaterialTag("chembath_persulfate");
    public static final DoubleMaterialTag SMELT_INTO = new DoubleMaterialTag("smelt_into");
    public static final DoubleMaterialTag DIRECT_SMELT_INTO = new DoubleMaterialTag("direct_smelt_into");
    public static final DoubleMaterialTag ARC_SMELT_INTO = new DoubleMaterialTag("arc_smelt_into");
    public static final DoubleMaterialTag MACERATE_INTO = new DoubleMaterialTag("macerate_into");
    public static final NumberMaterialTag MELTING_POINT = new NumberMaterialTag("melting_point");
    public static final NumberMaterialTag BLAST_FURNACE_TEMP = new NumberMaterialTag("blast_furnace_temp");
    public static final NumberMaterialTag MINING_LEVEL = new NumberMaterialTag("mining_level");
    public static final NumberMaterialTag FUEL_POWER = new NumberMaterialTag("fuel_power");
    public static final NumberMaterialTag LIQUID_TEMPERATURE = new NumberMaterialTag("liquid_temperature");
    public static final NumberMaterialTag GAS_TEMPERATURE = new NumberMaterialTag("gas_temperature");
    public static final NumberMaterialTag ORE_MULTI = new NumberMaterialTag("ore_multi");
    public static final NumberMaterialTag SMELTING_MULTI = new NumberMaterialTag("smelting_multi");
    public static final NumberMaterialTag BY_PRODUCT_MULTI = new NumberMaterialTag("by_product_multi");

    public static final ListMaterialTag<MaterialStack> PROCESS_INTO = new ListMaterialTag<>("process_into");

    public static final ListMaterialTag<Material> BYPRODUCTS = new ListMaterialTag<>("byproducts");

    public static final BlockDropMaterialTag<BlockOre> CUSTOM_ORE_DROPS = new BlockDropMaterialTag<>("custom_ore_drops");

    public static final BlockDropMaterialTag<BlockOreStone> CUSTOM_ORE_STONE_DROPS = new BlockDropMaterialTag<>("custom_ore_stone_drops");

    //Dummy Types
    public static ToolMaterialTag TOOLS = new ToolMaterialTag();
    public static ArmorMaterialTag ARMOR = new ArmorMaterialTag();
}
