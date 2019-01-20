package muramasa.itech.api.machines.types;

import muramasa.itech.api.machines.objects.SlotData;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.recipe.RecipeMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidMachine extends BasicMachine {

    private boolean hasFluidTanks;

    public FluidMachine(String name, boolean hasFluidTanks, Tier[] tiers, SlotData... slots) {
        super(name, tiers, slots);
        this.hasFluidTanks = hasFluidTanks;
    }

    public FluidMachine(String name, boolean hasFluidTanks, Tier[] tiers, Machine machine) {
        this(name, hasFluidTanks, tiers, ((BasicMachine)machine).getSlotData());
    }

    @Override
    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeBoth(getName(), inputs, fluidInputs);
    }

    public boolean getHasFluidTanks() {
        return hasFluidTanks;
    }
}
