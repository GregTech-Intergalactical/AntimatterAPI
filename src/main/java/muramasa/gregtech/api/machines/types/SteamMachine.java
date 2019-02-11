package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.capability.impl.MachineStackHandler;
import muramasa.gregtech.api.capability.impl.MachineTankHandler;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class SteamMachine extends Machine {

    public SteamMachine(String name, MachineFlag... extraFlags) {
        super(name, new BlockMachine(name), TileEntitySteamMachine.class);
        setTiers(Tier.getSteam());
        addFlags(BASIC, STEAM, ITEM, FLUID);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MACHINE_ID);
    }

    @Override
    public ResourceLocation getGUITexture(String tier) {
        return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + tier + ".png");
    }

    @Override
    public Recipe findRecipe(MachineStackHandler stackHandler, MachineTankHandler tankHandler) {
        return RecipeMap.findRecipeItemFluid(recipeMap, stackHandler.getInputs(), tankHandler.getInputs());
    }
}
