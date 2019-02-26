package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;

public class SteamMachine extends ItemFluidMachine {

    public SteamMachine(String name) {
        super(name);
        setTiers(Tier.getSteam());
    }

    public SteamMachine(String name, Tier tier) {
        this(name);
        setTiers(tier);
    }

    @Override
    public ResourceLocation getGUITexture(Tier tier) {
        return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + tier.getName() + ".png");
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return RecipeMap.findRecipeItemFluid(recipeMap, stackHandler.getInputs(), tankHandler.getInputs());
    }
}
