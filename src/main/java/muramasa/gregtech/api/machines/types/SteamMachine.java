package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.machines.Slot;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;

public class SteamMachine extends ItemFluidMachine {

    public SteamMachine(String name, Machine machine, Slot... slots) {
        super(name, machine, slots);
    }

    public SteamMachine(String name, Slot... slots) {
        super(name, slots);
        setTiers(Tier.getSteam());
    }

    @Override
    public ResourceLocation getGUITexture(String tier) {
        return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + tier + ".png");
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return RecipeMap.findRecipeItemFluid(recipeMap, stackHandler.getInputs(), tankHandler.getInputs());
    }
}
