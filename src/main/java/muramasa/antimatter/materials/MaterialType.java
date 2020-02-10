package muramasa.antimatter.materials;

import com.google.common.base.CaseFormat;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.Configs;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialType implements IMaterialTag, IAntimatterObject {

    //Item Types
    public static MaterialType DUST = new MaterialType("dust", 2, true, Ref.U);
    public static MaterialType DUST_SMALL = new MaterialType("dust_small", 2, false, Ref.U4);
    public static MaterialType DUST_TINY = new MaterialType("dust_tiny", 2, false, Ref.U9);
    public static MaterialType DUST_IMPURE = new MaterialType("dust_impure", 2, false, Ref.U);
    public static MaterialType DUST_PURE = new MaterialType("dust_pure", 2, false, Ref.U);
    public static MaterialType ROCK = new MaterialType("rock", 2, false, Ref.U9);
    public static MaterialType CRUSHED = new MaterialType("crushed", 2, false, Ref.U);
    public static MaterialType CRUSHED_CENTRIFUGED = new MaterialType("crushed_centrifuged", 2, false, Ref.U);
    public static MaterialType CRUSHED_PURIFIED = new MaterialType("crushed_purified", 2, false, Ref.U);
    public static MaterialType INGOT = new MaterialType("ingot", 2, true, Ref.U);
    public static MaterialType INGOT_HOT = new MaterialType("ingot_hot", 2, true, Ref.U);
    public static MaterialType NUGGET = new MaterialType("nugget", 2, false, Ref.U9);
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
    public static MaterialType ORE = new MaterialType("ore", 1, true, -1, false);
    public static MaterialType ORE_SMALL = new MaterialType("ore_small", 1, false, -1, false);
    public static MaterialType BLOCK = new MaterialType("block", 1, false, -1, false);
    public static MaterialType FRAME = new MaterialType("frame", 1, true, -1, false);
    public static MaterialType TOOLS = new MaterialType("tools", 1, false, -1, false);
    public static MaterialType LIQUID = new MaterialType("liquid", 1, true, -1, false);
    public static MaterialType GAS = new MaterialType("gas", 1, true, -1, false);
    public static MaterialType PLASMA = new MaterialType("plasma", 1, true, -1, false);

    private String id;
    private ITextComponent namePre, namePost;
    private int unitValue, layers;
    private boolean active, visible, hasLocName;
    private Set<Material> materials = new LinkedHashSet<>(); //Linked to preserve insertion order for JEI

    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        active = true;
        register(MaterialType.class, this);
    }

    public MaterialType(String id, int layers, boolean visible, int unitValue, boolean active) {
        this(id, layers, visible, unitValue);
        this.active = active;
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

    //TODO
    public ItemStack get(Material material, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public Set<Material> all() {
        return materials;
    }

    public boolean isVisible() {
        return visible || Configs.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public ITextComponent getDisplayName(Material material) {
        //if (namePre == null) { //TODO cache
            namePre = new TranslationTextComponent("material_type.pre." + getId());
            //if (!namePre.getFormattedText().isEmpty()) namePre.appendText(" ");
        //}
        //if (namePost == null) {
            namePost = new TranslationTextComponent("material_type.post." + getId());
            if (!namePost.getFormattedText().isEmpty()) namePost = new StringTextComponent(" ").appendSibling(namePost);
        //}
        return new TranslationTextComponent("").appendSibling(namePre).appendSibling(material.getDisplayName()).appendSibling(namePost);
    }

    @Deprecated //TODO remove
    public String oreName(Material material) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, getId().concat("_").concat(material.getId()));
    }

    public boolean allowGeneration(Material material) {
        return active && material.has(this) && AntimatterAPI.getReplacement(this, material).isEmpty();
    }

    @Override
    public String toString() {
        return getId();
    }
}
