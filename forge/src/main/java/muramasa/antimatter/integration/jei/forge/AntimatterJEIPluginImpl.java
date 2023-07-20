package muramasa.antimatter.integration.jei.forge;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.forge.fluid.ForgeFluidHolder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class AntimatterJEIPluginImpl {
    public static void uses(FluidHolder val, boolean USE) {
        FluidStack v = ForgeFluidHolder.toStack(val);
        AntimatterJEIPlugin.getRuntime().getRecipesGui().show(new IFocus<FluidStack>() {
            @Override
            public ITypedIngredient<FluidStack> getTypedValue() {
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientType<FluidStack> getType() {
                        return VanillaTypes.FLUID;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return v;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == VanillaTypes.FLUID) return ((Optional<V>) Optional.of(v));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return USE ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
                return Optional.empty();
            }

            @Override
            public Mode getMode() {
                return USE ? Mode.INPUT : Mode.OUTPUT;
            }

        });
    }

    public static void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidHolder> stacks){
        builder.addIngredients(VanillaTypes.FLUID, stacks.stream().map(ForgeFluidHolder::toStack).toList());
    }

    public static FluidHolder getIngredient(ITypedIngredient<?> ingredient){
        return new ForgeFluidHolder(ingredient.getIngredient(VanillaTypes.FLUID).get());
    }
}
