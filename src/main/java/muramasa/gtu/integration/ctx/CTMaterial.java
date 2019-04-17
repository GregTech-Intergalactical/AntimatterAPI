package muramasa.gtu.integration.ctx;

import muramasa.gtu.api.interfaces.IMaterialFlag;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialSet;
import muramasa.gtu.api.materials.RecipeFlag;
import org.apache.commons.lang3.EnumUtils;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

public class CTMaterial {

    private Material material;

    public CTMaterial(Material material) {
        this.material = material;
    }

    public CTMaterial(String name, int rgb, String textureSet) {
        MaterialSet set = MaterialSet.valueOf(textureSet.toUpperCase());
        material = new Material(name, rgb, set);
    }

    @ZenMethod
    public CTMaterial asMetal(int meltingPoint, int blastFurnaceTemp) {
        material.asMetal(meltingPoint, blastFurnaceTemp);
        return this;
    }

    @ZenMethod
    public CTMaterial addFlag(String... flagNames) {
        ArrayList<IMaterialFlag> flags = new ArrayList<>();
        for (String name : flagNames) {
            if (EnumUtils.isValidEnum(ItemFlag.class, name.toUpperCase())) flags.add(ItemFlag.valueOf(name.toUpperCase()));
            else if (EnumUtils.isValidEnum(RecipeFlag.class, name.toUpperCase())) flags.add(RecipeFlag.valueOf(name.toUpperCase()));
            else throw new IllegalArgumentException("flag name invalid");
        }
        material.add(flags.toArray(new IMaterialFlag[0]));
        return this;
    }
}
