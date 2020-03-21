package muramasa.antimatter.material;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.HashSet;
import java.util.Set;

public class MaterialTag implements IAntimatterObject, IMaterialTag {

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
    public static MaterialTag CABLE = new MaterialTag("cable");
    public static MaterialTag ELEMENTAL = new MaterialTag("elemental");

    //TODO get alloy flag for adding mixer and dust crafting recipes automatically

    private String id;
    private Set<Material> materials = new HashSet<>();

    public MaterialTag(String id) {
        this.id = id;
        register(MaterialTag.class, this);
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