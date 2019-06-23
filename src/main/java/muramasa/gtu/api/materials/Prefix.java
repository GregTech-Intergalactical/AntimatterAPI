package muramasa.gtu.api.materials;

import com.google.common.base.CaseFormat;
import muramasa.gtu.Configs;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;

import static muramasa.gtu.api.materials.GenerationFlag.*;

public class Prefix implements IGregTechObject {

    private static LinkedHashMap<String, ItemStack> ITEM_REPLACEMENT = new LinkedHashMap<>();

    public static Prefix Ore = new Prefix("ore", true, false, ORE).setType(1);
    public static Prefix Block = new Prefix("block", true, false, BLOCK).setType(1);

//    public static Prefix Chunk = new Prefix("chunk", true, ORE);
    public static Prefix Crushed = new Prefix("crushed", false, CRUSHED);
    public static Prefix CrushedPurified = new Prefix("crushed_purified", false, PURIFIED_CRUSHED);
    public static Prefix CrushedCentrifuged = new Prefix("crushed_centrifuged", false, CENTRIFUGED_CRUSHED);
    public static Prefix DustPure = new Prefix("dust_pure", false, PURE_DUST);
    public static Prefix DustImpure = new Prefix("dust_impure", false, IMPURE_DUST);
    public static Prefix Dust = new Prefix("dust", true, DUST);
    public static Prefix DustSmall = new Prefix("dust_small", false, SMALL_DUST);
    public static Prefix DustTiny = new Prefix("dust_tiny", false, TINY_DUST);
    public static Prefix Nugget = new Prefix("nugget", false, NUGGET);
    public static Prefix Ingot = new Prefix("ingot", true, INGOT);
    public static Prefix IngotHot = new Prefix("ingot_hot", false, HOT_INGOT);
    public static Prefix Plate = new Prefix("plate", true, PLATE);
    public static Prefix PlateDense = new Prefix("plate_dense", true, DENSE_PLATE);
    public static Prefix Gem = new Prefix("gem", true, BASIC_GEM);
    public static Prefix GemChipped = new Prefix("gem_chipped", true, GEM_VARIANTS);
    public static Prefix GemFlawed = new Prefix("gem_flawed", true, GEM_VARIANTS);
    public static Prefix GemFlawless = new Prefix("gem_flawless", true, GEM_VARIANTS);
    public static Prefix GemExquisite = new Prefix("gem_exquisite", true, GEM_VARIANTS);
    public static Prefix Foil = new Prefix("foil", true, FOIL);
    public static Prefix Rod = new Prefix("rod", true, ROD);
    public static Prefix RodLong = new Prefix("rod_long", true, LONG_ROD);
    public static Prefix Bolt = new Prefix("bolt", true, BOLT);
    public static Prefix Screw = new Prefix("screw", true, SCREW);
    public static Prefix Ring = new Prefix("ring", true, RING);
    public static Prefix Spring = new Prefix("spring", true, SPRING);
    public static Prefix WireFine = new Prefix("wire_fine", true, FINE_WIRE);
    public static Prefix TurbineRotor = new Prefix("rotor", true, TURBINE_ROTOR);
    public static Prefix Gear = new Prefix("gear", true, GEAR);
    public static Prefix GearSmall = new Prefix("gear_small", true, SMALL_GEAR);
    public static Prefix Lens = new Prefix("lens", true, LENS);
//    public static Prefix TurbineBlade = new Prefix("turbine_blade", true, TOOLS);

    private String id, namePre, namePost;
    private int type = 0; //0 = item, 1 = block //TODO, maybe find a better way to differentiate types of prefixes?
    private boolean doesGenerate, hasLocName, visible;
    private long generationBits;

    public Prefix(String id, boolean visible, GenerationFlag flag) {
        this.id = id;
        this.visible = visible;
//        for (GenerationFlag flag : flags) {
//            generationBits |= flag.getBit();
//        }
        generationBits |= flag.getBit();
        this.doesGenerate = true;
        GregTechAPI.register(Prefix.class, this);
    }

    public Prefix(String id, boolean visible, boolean generatesItems, GenerationFlag flag) {
        this(id, visible, flag);
        this.doesGenerate = generatesItems;
    }

    public Prefix setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getDisplayName(Material material) { //TODO cache
        if (!hasLocName) {
            namePre = Utils.trans("prefix.pre." + getId() + ".name");
            namePre = namePre.equals("") ? "" : namePre + " ";
            namePost = Utils.trans("prefix.post." + getId() + ".name");
            namePost = namePost.equals("") ? "" : " " + namePost;
            hasLocName = true;
        }
        return namePre + material.getDisplayName() + namePost;
    }

    public String oreName(Material material) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, getId().concat("_").concat(material.getId()));
    }

    public boolean isVisible() {
        return visible || Configs.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public boolean allowGeneration(Material material) {
        return doesGenerate && (material.getItemMask() & generationBits) != 0 && !ITEM_REPLACEMENT.containsKey(getId() + material.getId());
    }

    public void addReplacement(Material material, ItemStack stack) {
        ITEM_REPLACEMENT.put(getId() + material.getId(), stack);
    }

    public ItemStack getReplacement(Material material) {
        return ITEM_REPLACEMENT.get(getId() + material.getId());
    }

    @Override
    public String toString() {
        return getId();
    }
}
