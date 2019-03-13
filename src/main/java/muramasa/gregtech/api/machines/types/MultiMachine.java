package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.common.blocks.BlockMultiMachine;

import java.util.Arrays;

import static muramasa.gregtech.api.machines.MachineFlag.MULTI;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Class tileClass, MachineFlag... extraFlags) {
        super(name, new BlockMultiMachine(name), tileClass);
        setTiers(Tier.getMulti());
        addFlags(MULTI);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MULTI_MACHINE_ID);
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return super.findRecipe(stackHandler, tankHandler);
    }

    @Override
    public Texture[] getTextures() {
        Texture[] textures = super.getTextures();
        textures = Arrays.copyOf(textures, textures.length);
        textures[textures.length - 1] = getBaseTexture(Tier.MULTI);
        return textures;
    }

    @Override
    public Texture getBaseTexture(Tier tier) {
        return new Texture("blocks/machine/base/" + name);
    }
}
