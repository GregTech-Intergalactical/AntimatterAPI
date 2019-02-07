package muramasa.gregtech.api.machines;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.enums.MachineFlag;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.loaders.ContentLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.gregtech.api.enums.MachineFlag.*;

public class SteamMachine extends Machine {

    public SteamMachine(String name, MachineFlag... extraFlags) {
        super(name, ContentLoader.blockMachines, TileEntitySteamMachine.class);
        setTiers(Tier.getSteam());
        addFlags(BASIC, STEAM, FLUID);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MACHINE_ID);
    }

    @Override
    public ResourceLocation getGUITexture(String tier) {
        return new ResourceLocation(Ref.MODID, "textures/gui/machines/" + name + tier + ".png");
    }

    @Override
    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeItem(recipeMap, inputs);
    }
}
