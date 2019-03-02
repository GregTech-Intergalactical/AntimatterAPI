package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.interfaces.IMaterialFlag;

import java.util.ArrayList;
import java.util.Locale;

public enum RecipeFlag implements IMaterialFlag {

    ELEC(), //Add Electrolyzer Recipes - SHOULD NOT SHARE MATS WITH CENT
    CENT(), //Add Centrifuging Recipes - SHOULD NOT SHARE MATS WITH ELEC
    CRACK(), //Add Cracking Recipes
//    SMELTG(), //Add Smelting to Gem Recipes
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

    //TODO get alloy flag for adding mixer and dust crafting recipes automatically

    private long bit;
    private ArrayList<Material> materials = new ArrayList<>();

    RecipeFlag() {
        bit = 1 << ordinal();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void add(Material... mats) {
        for (Material material : mats) {
            if (!materials.contains(material)) {
                materials.add(material);
            }
        }
    }

    @Override
    public long getBit() {
        return this.bit;
    }

    @Override
    public ArrayList<Material> getMats() {
        return materials;
    }
}