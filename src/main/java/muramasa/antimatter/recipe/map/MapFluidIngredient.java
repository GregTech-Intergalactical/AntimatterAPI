package muramasa.antimatter.recipe.map;

import net.minecraftforge.fluids.FluidStack;

public class MapFluidIngredient extends AbstractMapIngredient {

    public final FluidStack stack;

    public MapFluidIngredient(FluidStack stack, int id) {
        super(id);
        this.stack = stack;
    }

    @Override
    protected int hash() {
        return stack.getFluid().getRegistryName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MapFluidIngredient)) return false;
        MapFluidIngredient fluid = (MapFluidIngredient) o;
        return this.hashCode() == fluid.hashCode() && stack.getFluid() == fluid.stack.getFluid(); //&& stack.isFluidEqual(fluid.stack);
    }

    @Override
    public String toString() {
        return stack.getFluid().getRegistryName().toString();
    }
}
