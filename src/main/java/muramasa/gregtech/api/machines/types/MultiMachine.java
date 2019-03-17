package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.common.blocks.BlockMachine;

import java.util.List;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Class tileClass, MachineFlag... extraFlags) {
        super(name, new BlockMachine(name), tileClass);
        setTiers(Tier.getMulti());
        addFlags(MULTI, CONFIGURABLE, COVERABLE);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MULTI_MACHINE_ID);
        baseTexture = new Texture("blocks/machine/base/" + name);
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return super.findRecipe(stackHandler, tankHandler);
    }

    @Override
    public List<Texture> getTextures() {
        List<Texture> textures = super.getTextures();
        textures.add(getBaseTexture(Tier.MULTI));
        return textures;
    }

    @Override
    public Texture getBaseTexture(Tier tier) {
        return baseTexture;
    }
}
