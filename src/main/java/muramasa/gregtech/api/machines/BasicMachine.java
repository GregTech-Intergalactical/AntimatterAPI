package muramasa.gregtech.api.machines;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.enums.MachineFlag;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.loaders.ContentLoader;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.gregtech.api.enums.MachineFlag.*;

public class BasicMachine extends Machine {

    public BasicMachine(String name, MachineFlag... extraFlags) {
        super(name, ContentLoader.blockMachines, TileEntityBasicMachine.class);
        setTiers(Tier.getStandard());
        addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MACHINE_ID);
    }

    @Override
    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeItem(recipeMap, inputs);
    }
}
