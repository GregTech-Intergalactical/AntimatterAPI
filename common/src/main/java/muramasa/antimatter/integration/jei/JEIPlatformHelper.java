package muramasa.antimatter.integration.jei;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import muramasa.antimatter.util.ImplLoader;

import java.util.List;

public interface JEIPlatformHelper {
    JEIPlatformHelper INSTANCE = ImplLoader.load(JEIPlatformHelper.class);

    void uses(FluidHolder val, boolean USE);

    void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidHolder> stacks);

    Object getFluidObject(FluidHolder fluidHolder);

    IIngredientType<?> getFluidIngredientObjectType();

    FluidHolder getIngredient(ITypedIngredient<?> ingredient);
}
