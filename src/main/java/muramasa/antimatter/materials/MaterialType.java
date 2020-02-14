package muramasa.antimatter.materials;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.items.MaterialItem;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MaterialType implements IMaterialTag, IAntimatterObject {

    //Item Types
    public static MaterialType DUST = new MaterialType("dust", 2, true, Ref.U);
    public static MaterialType DUST_SMALL = new MaterialType("dust_small", 2, true, Ref.U4);
    public static MaterialType DUST_TINY = new MaterialType("dust_tiny", 2, true, Ref.U9);
    public static MaterialType DUST_IMPURE = new MaterialType("dust_impure", 2, true, Ref.U);
    public static MaterialType DUST_PURE = new MaterialType("dust_pure", 2, true, Ref.U);
    public static MaterialType ROCK = new MaterialType("rock", 2, true, Ref.U9);
    public static MaterialType CRUSHED = new MaterialType("crushed", 2, true, Ref.U);
    public static MaterialType CRUSHED_CENTRIFUGED = new MaterialType("crushed_centrifuged", 2, true, Ref.U);
    public static MaterialType CRUSHED_PURIFIED = new MaterialType("crushed_purified", 2, true, Ref.U);
    public static MaterialType INGOT = new MaterialType("ingot", 2, true, Ref.U);
    public static MaterialType INGOT_HOT = new MaterialType("ingot_hot", 2, true, Ref.U);
    public static MaterialType NUGGET = new MaterialType("nugget", 2, true, Ref.U9);
    public static MaterialType GEM = new MaterialType("gem", 2, true, Ref.U);
    public static MaterialType GEM_BRITTLE = new MaterialType("gem_brittle", 2, true, Ref.U);
    public static MaterialType GEM_POLISHED = new MaterialType("gem_polished", 2, true, Ref.U);
    public static MaterialType LENS = new MaterialType("lens", 2, true, Ref.U * 3 / 4);
    public static MaterialType PLATE = new MaterialType("plate", 2, true, Ref.U);
    public static MaterialType PLATE_DENSE = new MaterialType("plate_dense", 2, true, Ref.U * 9);
    public static MaterialType ROD = new MaterialType("rod", 2, true, Ref.U2);
    public static MaterialType ROD_LONG = new MaterialType("rod_long", 2, true, Ref.U);
    public static MaterialType RING = new MaterialType("ring", 2, true, Ref.U4);
    public static MaterialType FOIL = new MaterialType("foil", 2, true, Ref.U);
    public static MaterialType BOLT = new MaterialType("bolt", 2, true, Ref.U8);
    public static MaterialType SCREW = new MaterialType("screw", 2, true, Ref.U9);
    public static MaterialType GEAR = new MaterialType("gear", 2, true, Ref.U * 4);
    public static MaterialType GEAR_SMALL = new MaterialType("gear_small", 2, true, Ref.U);
    public static MaterialType WIRE_FINE = new MaterialType("wire_fine", 2, true, Ref.U8);
    public static MaterialType SPRING = new MaterialType("spring", 2, true, Ref.U);
    public static MaterialType ROTOR = new MaterialType("rotor", 2, true, Ref.U * 4 + Ref.U4);

    //Dummy Types
    public static MaterialType ORE = new MaterialType("ore", 1, true, -1, false, true);
    public static MaterialType ORE_SMALL = new MaterialType("ore_small", 1, false, -1, false, true);
    public static MaterialType ORE_STONE = new MaterialType("ore_stone", 1, true, -1, false, true);
    public static MaterialType BLOCK = new MaterialType("block", 1, false, -1, false, true);
    public static MaterialType FRAME = new MaterialType("frame", 1, true, -1, false, true);
    public static MaterialType TOOLS = new MaterialType("tools", 1, false, -1, false);
    public static MaterialType LIQUID = new MaterialType("liquid", 1, true, -1, false);
    public static MaterialType GAS = new MaterialType("gas", 1, true, -1, false);
    public static MaterialType PLASMA = new MaterialType("plasma", 1, true, -1, false);

    private String id;
    private ITextComponent namePre, namePost;
    private int unitValue, layers;
    private boolean active, visible, hasLocName, hasBlock;
    private Set<Material> materials = new LinkedHashSet<>(); //Linked to preserve insertion order for JEI
    private Map<MaterialType, Tag> tagMap = new HashMap<>();

    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        active = true;
        this.tagMap.put(this, Utils.getForgeItemTag(Utils.getConventionalMaterialType(this)));
        register(MaterialType.class, this);
    }

    public MaterialType(String id, int layers, boolean visible, int unitValue, boolean active) {
        this(id, layers, visible, unitValue);
        this.active = active;
    }

    public MaterialType(String id, int layers, boolean visible, int unitValue, boolean active, boolean hasBlock) {
        this(id, layers, visible, unitValue, active);
        this.hasBlock = hasBlock;
        this.tagMap.put(this, Utils.getForgeBlockTag(Utils.getConventionalMaterialType(this)));
    }

    @Override
    public String getId() {
        return id;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public int getLayers() {
        return layers;
    }

    public <T> Tag<T> getTag() {
        return tagMap.get(this);
    }

    public Item get(Material material) {
        if (!allowGeneration(material)) Utils.onInvalidData("GET ERROR - DOES NOT GENERATE: T(" + id + ") M(" + material.getId() + ")");
        ItemStack replacement = AntimatterAPI.getReplacement(this, material);
        if (!replacement.isEmpty()) return replacement.getItem();
        MaterialItem item = AntimatterAPI.get(MaterialItem.class, id + "_" + material.getId());
        if (item == null) Utils.onInvalidData("GET ERROR - MAT ITEM NULL: T(" + id + ") M(" + material.getId() + ")");
        return item;
    }

    public ItemStack get(Material material, int count) {
        Item item = get(material);
        if (item == null) Utils.onInvalidData("GET ERROR - MAT ITEM NULL: T(" + id + ") M(" + material.getId() + ")");
        ItemStack stack = new ItemStack(item, count);
        if (stack.isEmpty()) Utils.onInvalidData("GET ERROR - MAT STACK EMPTY: T(" + id + ") M(" + material.getId() + ")");
        return stack;
    }

    @Nullable
    public static BlockOreStone getOreStone(Material material) {
        return AntimatterAPI.get(BlockOreStone.class, "stone_" + material.getId());
    }

    @Override
    public Set<Material> all() {
        return materials;
    }

    public boolean isVisible() {
        return visible || Configs.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public boolean allowGeneration(Material material) {
        return active && material.has(this) && AntimatterAPI.getReplacement(this, material).isEmpty();
    }

    @Override
    public String toString() {
        return getId();
    }
}
