package muramasa.gtu.api.machines.types;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.common.blocks.BlockMachine;

import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.*;

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
