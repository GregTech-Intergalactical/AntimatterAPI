package muramasa.gtu.api.materials;

import com.google.common.base.CaseFormat;
import muramasa.gtu.Configs;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.util.Utils;

import java.util.HashSet;
import java.util.Set;

public class MaterialType implements IMaterialTag, IGregTechObject {

    //Item Types
    public static MaterialType DUST = new MaterialType("dust", true);
    public static MaterialType DUST_SMALL = new MaterialType("dust_small", false);
    public static MaterialType DUST_TINY = new MaterialType("dust_tiny", false);
    public static MaterialType DUST_IMPURE = new MaterialType("dust_impure", false);
    public static MaterialType DUST_PURE = new MaterialType("dust_pure", false);
    public static MaterialType CRUSHED = new MaterialType("crushed", false);
    public static MaterialType CRUSHED_CENTRIFUGED = new MaterialType("crushed_centrifuged", false);
    public static MaterialType CRUSHED_PURIFIED = new MaterialType("crushed_purified", false);
    public static MaterialType INGOT = new MaterialType("ingot", true);
    public static MaterialType INGOT_HOT = new MaterialType("ingot_hot", true);
    public static MaterialType NUGGET = new MaterialType("nugget", false);
    public static MaterialType GEM = new MaterialType("gem", true);
    public static MaterialType GEM_CHIPPED = new MaterialType("gem_chipped", true);
    public static MaterialType GEM_FLAWED = new MaterialType("gem_flawed", true);
    public static MaterialType GEM_FLAWLESS = new MaterialType("gem_flawless", true);
    public static MaterialType GEM_EXQUISITE = new MaterialType("gem_exquisite", true);
    public static MaterialType LENS = new MaterialType("lens", true);
    public static MaterialType PLATE = new MaterialType("plate", true);
    public static MaterialType PLATE_DENSE = new MaterialType("plate_dense", true);
    public static MaterialType ROD = new MaterialType("rod", true);
    public static MaterialType ROD_LONG = new MaterialType("rog_long", true);
    public static MaterialType RING = new MaterialType("ring", true);
    public static MaterialType FOIL = new MaterialType("foil", true);
    public static MaterialType BOLT = new MaterialType("bolt", true);
    public static MaterialType SCREW = new MaterialType("screw", true);
    public static MaterialType GEAR = new MaterialType("gear", true);
    public static MaterialType GEAR_SMALL = new MaterialType("gear_small", true);
    public static MaterialType WIRE_FINE = new MaterialType("wire_fine", true);
    public static MaterialType SPRING = new MaterialType("spring", true);
    public static MaterialType ROTOR = new MaterialType("rotor", true);

    //Dummy Types
    public static MaterialType ORE = new MaterialType("ore", true, false); //TODO: dimensional ores, stone types, need separate prefix?
    public static MaterialType ORE_SMALL = new MaterialType("ore_small", false, false);
    public static MaterialType BLOCK = new MaterialType("block", true, false);
    public static MaterialType FRAME = new MaterialType("frame", true, false);
    public static MaterialType GEM_VARIANTS = new MaterialType("gem_variants", false, false);
    public static MaterialType TOOLS = new MaterialType("tools", false, false);
    public static MaterialType LIQUID = new MaterialType("liquid", true, false);
    public static MaterialType GAS = new MaterialType("gas", true, false);
    public static MaterialType PLASMA = new MaterialType("plasma", true, false);

    private String id, namePre, namePost;
    private boolean doesGenerate, visible, hasLocName;
    private Set<Material> materials = new HashSet<>();

    public MaterialType(String id, boolean visible) {
        this.id = id;
        this.visible = visible;
        doesGenerate = true;
        register(MaterialType.class, this);
    }

    public MaterialType(String id, boolean visible, boolean generatesItems) {
        this(id, visible);
        this.doesGenerate = generatesItems;
    }

    public String getId() {
        return id;
    }

    @Override
    public Set<Material> getMats() {
        return materials;
    }

    public boolean isVisible() {
        return visible || Configs.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public String getDisplayName(Material material) { //TODO cache
        if (!hasLocName) {
            namePre = Utils.trans("material_type.pre." + getId() + ".name");
            namePre = namePre.equals("") ? "" : namePre + " ";
            namePost = Utils.trans("material_type.post." + getId() + ".name");
            namePost = namePost.equals("") ? "" : " " + namePost;
            hasLocName = true;
        }
        return namePre + material.getDisplayName() + namePost;
    }

    public String oreName(Material material) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, getId().concat("_").concat(material.getId()));
    }

    public boolean allowGeneration(Material material) {
        return doesGenerate && material.has(this) && GregTechAPI.getReplacement(this, material) == null;
    }
}
