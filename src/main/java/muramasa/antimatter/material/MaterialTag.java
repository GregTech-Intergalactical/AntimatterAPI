package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MaterialTag implements IMaterialTag {

    //TODO move to GTI
    public static MaterialTag ELEC = new MaterialTag("elec"); //Add Electrolyzer Recipes - SHOULD NOT SHARE MATS WITH CENT
    public static MaterialTag CENT = new MaterialTag("cent"); //Add Centrifuging Recipes - SHOULD NOT SHARE MATS WITH ELEC
    public static MaterialTag CRACK = new MaterialTag("crack"); //Add Cracking Recipes
    public static MaterialTag SMELTG = new MaterialTag("smelt_g"); //Add Smelting to Gem Recipes
    public static MaterialTag SMELTF = new MaterialTag("smelt_f"); //Add Smelting to Fluid Recipes
    public static MaterialTag GRINDABLE = new MaterialTag("grindable"); //Is Grindable with the Mortar
    public static MaterialTag CRYSTALLIZE = new MaterialTag("crystallize"); //Allows Dust > Gem Recipes
    public static MaterialTag CALCITE2X = new MaterialTag("calcite_2x"); //Blast Furnace Calcite 2x Multiplier
    public static MaterialTag CALCITE3X = new MaterialTag("calcite_3x"); //Blast Furnace Calcite 3x Multiplier
    public static MaterialTag NOSMASH = new MaterialTag("no_smash"); //Material is not able to be smashed
    public static MaterialTag NOSMELT = new MaterialTag("no_smelt"); //Material is not able to be smelted
    public static MaterialTag WASHM = new MaterialTag("wash_m"); //Adds Crushed > ByProducts with Mercury
    public static MaterialTag WASHS = new MaterialTag("wash_s"); //Adds Crushed > ByProducts with Sodium
    public static MaterialTag NOBBF = new MaterialTag("no_ebf"); //Stops Dust > Ingot in BBF
    public static MaterialTag ELECSEPI = new MaterialTag("elec_sep_i");
    public static MaterialTag ELECSEPG = new MaterialTag("elec_sep_g");
    public static MaterialTag ELECSEPN = new MaterialTag("elec_sep_n");
    public static MaterialTag SOLDER = new MaterialTag("solder"); //Can be used in Soldering Recipes
    public static MaterialTag BRITTLEG = new MaterialTag("brittle_g"); //This is for Gems that cannot be used in recipes such as Gem > Rod in Lathe
    public static MaterialTag RUBBERTOOLS = new MaterialTag("rubber_tools");
    public static MaterialTag METAL = new MaterialTag("metal");
    public static MaterialTag ELEMENTAL = new MaterialTag("elemental");
    public static MaterialTag FLINT = new MaterialTag("flint");

    /**
     * ETC
     **/
    public static MaterialTag HANDLE = new MaterialTag("handle");

    /**
     * PIPES
     **/
    public static MaterialTag ITEMPIPE = new MaterialTag("itempipe");
    public static MaterialTag FLUIDPIPE = new MaterialTag("fluidpipe");
    public static MaterialTag WIRE = new MaterialTag("wire");
    public static MaterialTag CABLE = new MaterialTag("cable");

    //TODO get alloy flag for adding mixer and dust crafting recipes automatically

    private final String id;
    private final Set<Material> materials = new ObjectLinkedOpenHashSet<>();
    private final Map<SubTag, Set<Material>> TAG_MAP = new Object2ObjectOpenHashMap<>();

    public MaterialTag(String id) {
        this.id = id + "_tag";
        register(MaterialTag.class, id + "_tag");
    }

    public MaterialTag subTag(SubTag tag, Material... mats) {
        Set<Material> set = TAG_MAP.computeIfAbsent(tag, k -> new ObjectOpenHashSet<>());
        set.addAll(Arrays.asList(mats));
        return this;
    }

    public Set<Material> allSub(SubTag sub) {
        return TAG_MAP.getOrDefault(sub, Collections.emptySet());
    }

    public boolean has(SubTag tag, Material mat) {
        return TAG_MAP.getOrDefault(tag, Collections.emptySet()).contains(mat);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<Material> all() {
        return materials;
    }
}