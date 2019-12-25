package muramasa.gtu.api.materials;

import com.google.common.base.CaseFormat;
import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialType implements IMaterialTag, IGregTechObject {

    //Item Types
    public static MaterialType DUST = new MaterialType("dust", true, Ref.M);
    public static MaterialType DUST_SMALL = new MaterialType("dust_small", false, Ref.M / 4);
    public static MaterialType DUST_TINY = new MaterialType("dust_tiny", false, Ref.M / 9);
    public static MaterialType DUST_IMPURE = new MaterialType("dust_impure", false, Ref.M);
    public static MaterialType DUST_PURE = new MaterialType("dust_pure", false, Ref.M);
    public static MaterialType ROCK = new MaterialType("rock", false, Ref.M / 9);
    public static MaterialType CRUSHED = new MaterialType("crushed", false, Ref.M);
    public static MaterialType CRUSHED_CENTRIFUGED = new MaterialType("crushed_centrifuged", false, Ref.M);
    public static MaterialType CRUSHED_PURIFIED = new MaterialType("crushed_purified", false, Ref.M);
    public static MaterialType INGOT = new MaterialType("ingot", true, Ref.M);
    public static MaterialType INGOT_HOT = new MaterialType("ingot_hot", true, Ref.M);
    public static MaterialType NUGGET = new MaterialType("nugget", false, Ref.M / 9);
    public static MaterialType GEM = new MaterialType("gem", true, Ref.M);
    public static MaterialType GEM_BRITTLE = new MaterialType("gem_brittle", true, Ref.M);
    public static MaterialType GEM_POLISHED = new MaterialType("gem_polished", true, Ref.M);
    public static MaterialType LENS = new MaterialType("lens", true, Ref.M * 3 / 4);
    public static MaterialType PLATE = new MaterialType("plate", true, Ref.M);
    public static MaterialType PLATE_DENSE = new MaterialType("plate_dense", true, Ref.M * 9);
    public static MaterialType ROD = new MaterialType("rod", true, Ref.M / 2);
    public static MaterialType ROD_LONG = new MaterialType("rod_long", true, Ref.M);
    public static MaterialType RING = new MaterialType("ring", true, Ref.M / 4);
    public static MaterialType FOIL = new MaterialType("foil", true, Ref.M);
    public static MaterialType BOLT = new MaterialType("bolt", true, Ref.M / 8);
    public static MaterialType SCREW = new MaterialType("screw", true, Ref.M / 9);
    public static MaterialType GEAR = new MaterialType("gear", true, Ref.M * 4);
    public static MaterialType GEAR_SMALL = new MaterialType("gear_small", true, Ref.M);
    public static MaterialType WIRE_FINE = new MaterialType("wire_fine", true, Ref.M / 8);
    public static MaterialType SPRING = new MaterialType("spring", true, Ref.M);
    public static MaterialType ROTOR = new MaterialType("rotor", true, Ref.M * 4 + Ref.M / 4);

    //Dummy Types
    public static MaterialType ORE = new MaterialType("ore", true, -1, false);
    public static MaterialType ORE_SMALL = new MaterialType("ore_small", false, -1, false);
    public static MaterialType BLOCK = new MaterialType("block", false, -1, false);
    public static MaterialType FRAME = new MaterialType("frame", true, -1, false);
    public static MaterialType TOOLS = new MaterialType("tools", false, -1, false);
    public static MaterialType LIQUID = new MaterialType("liquid", true, -1, false);
    public static MaterialType GAS = new MaterialType("gas", true, -1, false);
    public static MaterialType PLASMA = new MaterialType("plasma", true, -1, false);

    private String id;
    private ITextComponent namePre, namePost;
    private int unitValue;
    private boolean doesGenerate, visible, hasLocName;
    private Set<Material> materials = new LinkedHashSet<>(); //Linked to preserve insertion order for JEI

    public MaterialType(String id, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        doesGenerate = true;
        register(MaterialType.class, this);
    }

    public MaterialType(String id, boolean visible, int unitValue, boolean generatesItems) {
        this(id, visible, unitValue);
        this.doesGenerate = generatesItems;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getUnitValue() {
        return unitValue;
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
        return doesGenerate && material.has(this) && GregTechAPI.getReplacement(this, material).isEmpty();
    }
}
