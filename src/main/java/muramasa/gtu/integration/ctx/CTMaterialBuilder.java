package muramasa.gtu.integration.ctx;

import muramasa.gtu.api.interfaces.IMaterialFlag;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialSet;
import muramasa.gtu.api.materials.RecipeFlag;
import org.apache.commons.lang3.EnumUtils;
import stanhebben.zenscript.annotations.ZenMethod;

public class CTMaterialBuilder {

    private Material material;

    public CTMaterialBuilder(String displayName, int rgb, String textureSet) {
        //TODO check mat does not exist
        MaterialSet set = MaterialSet.valueOf(textureSet.toUpperCase());
        material = new Material(displayName, rgb, set);
    }

    @ZenMethod
    public CTMaterialBuilder asMetal(int meltingPoint, int blastFurnaceTemp) {
        material.asMetal(meltingPoint, blastFurnaceTemp);
        return this;
    }

    @ZenMethod
    public CTMaterialBuilder addFlag(String flagName) {
        IMaterialFlag flag;
        if (EnumUtils.isValidEnum(ItemFlag.class, flagName.toUpperCase())) flag = ItemFlag.valueOf(flagName.toUpperCase());
        else if (EnumUtils.isValidEnum(RecipeFlag.class, flagName.toUpperCase())) flag = RecipeFlag.valueOf(flagName.toUpperCase());
        else throw new IllegalArgumentException("flag name invalid");
        material.add(flag);
        return this;
    }
}
