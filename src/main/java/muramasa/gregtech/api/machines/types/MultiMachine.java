package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.common.blocks.BlockMultiMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;

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
    public ResourceLocation getBaseTexture(Tier tier) {
        return new ResourceLocation(Ref.MODID, "blocks/machine/base/" + name);
    }
}
