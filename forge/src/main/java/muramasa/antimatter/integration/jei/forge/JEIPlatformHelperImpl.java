package muramasa.antimatter.integration.jei.forge;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.forge.fluid.ForgeFluidHolder;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.integration.jei.JEIPlatformHelper;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class JEIPlatformHelperImpl implements JEIPlatformHelper {
    @Override
    public void uses(FluidHolder val, boolean USE) {
        FluidStack v = ForgeFluidHolder.toStack(val);
        AntimatterJEIPlugin.getRuntime().getRecipesGui().show(new IFocus<FluidStack>() {
            @Override
            public ITypedIngredient<FluidStack> getTypedValue() {
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientType<FluidStack> getType() {
                        return ForgeTypes.FLUID_STACK;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return v;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == ForgeTypes.FLUID_STACK) return ((Optional<V>) Optional.of(v));
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

    @Override
    public Object getFluidObject(FluidHolder fluidHolder){
        return ForgeFluidHolder.toStack(fluidHolder);
    }

    @Override
    public IIngredientType<?> getFluidIngredientObjectType(){
        return ForgeTypes.FLUID;
    }

    @Override
    public void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidHolder> stacks){
        builder.addIngredients(ForgeTypes.FLUID_STACK, stacks.stream().map(ForgeFluidHolder::toStack).toList());
    }

    @Override
    public FluidHolder getIngredient(ITypedIngredient<?> ingredient){
        return new ForgeFluidHolder(ingredient.getIngredient(ForgeTypes.FLUID_STACK).get());
    }
}
