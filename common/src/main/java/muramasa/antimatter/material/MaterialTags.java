package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.material.data.ArmorData;
import muramasa.antimatter.material.data.HandleData;
import muramasa.antimatter.material.data.ToolData;
import muramasa.antimatter.material.tags.*;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import net.minecraft.util.valueproviders.UniformInt;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MaterialTags {

    public static final MaterialTag RUBBERTOOLS = new MaterialTag("rubber_tools");
    public static final MaterialTag HAS_CUSTOM_SMELTING = new MaterialTag("has_custom_smelting");
    public static final MaterialTag TRANSPARENT = new MaterialTag("transparent");
    public static final MaterialTag METAL = new MaterialTag("metal");
    public static final MaterialTag ELEMENTAL = new MaterialTag("elemental");
    public static final MaterialTag FLINT = new MaterialTag("flint");
    public static final MaterialTag MOLTEN = new MaterialTag("molten");
    public static final MaterialTag QUARTZ_LIKE_BLOCKS = new MaterialTag("quartz_like_blocks");

    public static final TypeMaterialTag<UniformInt> EXP_RANGE = new TypeMaterialTag<>("exp_range");
    /**
     * ETC
     **/
    public static final TypeMaterialTag<HandleData> HANDLE = new TypeMaterialTag<>("handle"){
        @Override
        public HandleData get(Material mat) {
            if (mat == Material.NULL) return mapping.computeIfAbsent(mat, m -> new HandleData(0, 0, ImmutableMap.of()));
            return super.get(mat);
        }
    };
    /**
     * PIPES
     **/
    public static final MaterialTag ITEMPIPE = new MaterialTag("itempipe");
    public static final MaterialTag FLUIDPIPE = new MaterialTag("fluidpipe");
    public static final MaterialTag WIRE = new MaterialTag("wire");
    public static final MaterialTag CABLE = new MaterialTag("cable");

    public static final DoubleMaterialTag SMELT_INTO = new DoubleMaterialTag("smelt_into");
    public static final DoubleMaterialTag DIRECT_SMELT_INTO = new DoubleMaterialTag("direct_smelt_into");
    public static final DoubleMaterialTag ARC_SMELT_INTO = new DoubleMaterialTag("arc_smelt_into");
    public static final DoubleMaterialTag MACERATE_INTO = new DoubleMaterialTag("macerate_into");
    public static final NumberMaterialTag MELTING_POINT = new NumberMaterialTag("melting_point");
    public static final NumberMaterialTag MINING_LEVEL = new NumberMaterialTag("mining_level");
    public static final NumberMaterialTag FUEL_POWER = new NumberMaterialTag("fuel_power");
    public static final NumberMaterialTag LIQUID_TEMPERATURE = new NumberMaterialTag("liquid_temperature");
    public static final NumberMaterialTag GAS_TEMPERATURE = new NumberMaterialTag("gas_temperature");
    public static final NumberMaterialTag ORE_MULTI = new NumberMaterialTag("ore_multi");
    public static final NumberMaterialTag SMELTING_MULTI = new NumberMaterialTag("smelting_multi");
    public static final NumberMaterialTag BY_PRODUCT_MULTI = new NumberMaterialTag("by_product_multi");


    public static final TypeMaterialTag<Pair<List<MaterialStack>, Integer>> PROCESS_INTO = new TypeMaterialTag<>("process_into");

    public static final ListMaterialTag<Material> BYPRODUCTS = new ListMaterialTag<>("byproducts");


    public static final BlockDropMaterialTag<BlockOre> CUSTOM_ORE_DROPS = new BlockDropMaterialTag<>("custom_ore_drops");

    public static final BlockDropMaterialTag<BlockOreStone> CUSTOM_ORE_STONE_DROPS = new BlockDropMaterialTag<>("custom_ore_stone_drops");

    public static final MapMaterialTag<MaterialType<?>, Integer> FURNACE_FUELS = new MapMaterialTag<>("furnace_fuels");
    public static final MaterialTag NOSMASH = new MaterialTag("no_smash"); //Material is not able to be smashed

    //Dummy Types
    public static TypeMaterialTag<ToolData> TOOLS = new TypeMaterialTag<>("tools"){
        @Override
        public ToolData get(Material mat) {
            if (mat == Material.NULL) return mapping.computeIfAbsent(mat, m -> new ToolData(5.0f, 5.0f, Integer.MAX_VALUE, 3, ImmutableMap.of(), List.of()));
            return super.get(mat);
        }
    };
    public static TypeMaterialTag<ArmorData> ARMOR = new TypeMaterialTag<>("armor"){
        @Override
        public ArmorData get(Material mat) {
            if (mat == Material.NULL) return mapping.computeIfAbsent(mat, m -> new ArmorData(new int[]{1, 1, 1, 1}, 0.0f, 0.0f, 23, ImmutableMap.of()));
            return super.get(mat);
        }
    };
}
