package muramasa.antimatter.recipe.ingredient;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import tesseract.FluidPlatformUtils;

public class MapFluidIngredient extends AbstractMapIngredient {

    public FluidHolder stack;

    public MapFluidIngredient(FluidHolder stack, boolean insideMap) {
        super(insideMap);
        this.stack = stack;
    }

    @Override
    protected int hash() {
        return FluidPlatformUtils.getFluidId(stack.getFluid()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof MapFluidIngredient) {
            MapFluidIngredient fluid = (MapFluidIngredient) o;
            return this.hashCode() == fluid.hashCode() && stack.getFluid() == fluid.stack.getFluid(); //&& stack.isFluidEqual(fluid.stack);
        }
        if (o instanceof MapTagIngredient tag) {
            return stack.getFluid().builtInRegistryHolder().is(tag.floc);
        }
        return false;
    }

    @Override
    public String toString() {
        return FluidPlatformUtils.getFluidId(stack.getFluid()).toString();
    }
}
