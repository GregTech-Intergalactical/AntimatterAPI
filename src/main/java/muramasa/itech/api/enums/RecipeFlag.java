package muramasa.itech.api.enums;

import muramasa.itech.api.interfaces.IMaterialFlag;
import muramasa.itech.api.materials.Material;

import java.util.ArrayList;

public enum RecipeFlag implements IMaterialFlag {

    ELEC(), //Add Electrolyzer Recipes - SHOULD NOT SHARE MATS WITH CENT
    CENT(), //Add Centrifuging Recipes - SHOULD NOT SHARE MATS WITH ELEC
    CRACK(), //Add Cracking Recipes
    //SMELTG(), //Add Smelting to Gem Recipes
    SMELTF(), //Add Smelting to Fluid Recipes
    GRINDABLE(), //Is Grindable with the Mortar
    CRYSTALLIZE(), //Allows Dust > Gem Recipes
    CALCITE2X(), //Blast Furnace Calcite 2x Multiplier
    CALCITE3X(), //Blast Furnace Calcite 3x Multiplier
    NOSMASH(), //Material is not able to be smashed
    NOSMELT(), //Material is not able to be smelted
    WASHM(), //Adds Crushed > ByProducts with Mercury
    WASHS(), //Adds Crushed > ByProducts with Sodium
    NOBBF(), //Stops Dust > Ingot in BBF
    ELECSEPI(),
    ELECSEPG(),
    ELECSEPN(),
    SOLDER(), //Can be used in Soldering Recipes
    BRITTLEG(), //This is for Gems that cannot be used in recipes such as Gem > Rod in Lathe
    RUBBERTOOLS(),
    METAL(),
    CABLE(),
    ELEMENTAL();

    //TODO add alloy flag for adding mixer and dust crafting recipes automatically

    private int bit;
    private ArrayList<Material> materialsList;
    private Material[] materials;

    RecipeFlag() {
        bit = 1 << ordinal();
        materialsList = new ArrayList<>();
    }

    public static void finish() {
        for (RecipeFlag flag : RecipeFlag.values()) {
            flag.materials = flag.materialsList.toArray(new Material[0]);
            flag.materialsList = null;
        }
    }

    public void add(Material... mats) {
        for (Material material : mats) {
            materialsList.add(material);
        }
    }

    public int getMask() {
        return bit;
    }

    public Material[] getMats() {
        return materials;
    }
}