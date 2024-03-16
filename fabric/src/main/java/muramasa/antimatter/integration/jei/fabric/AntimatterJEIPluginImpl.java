package muramasa.antimatter.integration.jei.fabric;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AntimatterJEIPluginImpl {
    public static void uses(FluidHolder val, boolean USE) {
        //TODO uncomment when https://github.com/mezz/JustEnoughItems/issues/2891
        IJeiFluidIngredient v = new JeiFLuidWrapper(val);
        AntimatterJEIPlugin.getRuntime().getRecipesGui().show(new IFocus<IJeiFluidIngredient>() {
            @Override
            public ITypedIngredient<IJeiFluidIngredient> getTypedValue() {
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientType<IJeiFluidIngredient> getType() {
                        return FabricTypes.FLUID_STACK;
                    }

                    @Override
                    public IJeiFluidIngredient getIngredient() {
                        return v;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == FabricTypes.FLUID_STACK) return ((Optional<V>) Optional.of(v));
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
                return null;
            }

        });
    }

    public static Object getFluidObject(FluidHolder fluidHolder){
        return new JeiFLuidWrapper(fluidHolder);
    }

    public static IIngredientType<?> getFluidIngredientObjectType(){
        return FabricTypes.FLUID_STACK;
    }

    public static void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidHolder> stacks){
        builder.addIngredients(FabricTypes.FLUID_STACK, stacks.stream().map(JeiFLuidWrapper::new).collect(Collectors.toList()));
    }

    public static FluidHolder getIngredient(ITypedIngredient<?> ingredient){
        IJeiFluidIngredient fluidIngredient = ingredient.getIngredient(FabricTypes.FLUID_STACK).get();
        return FluidHooks.newFluidHolder(fluidIngredient.getFluid(), fluidIngredient.getAmount(), fluidIngredient.getTag().get());
    }
    record JeiFLuidWrapper(FluidHolder stack) implements IJeiFluidIngredient{

        @Override
        public Fluid getFluid() {
            return stack.getFluid();
        }

        @Override
        public long getAmount() {
            return stack.getFluidAmount();
        }

        @Override
        public Optional<CompoundTag> getTag() {
            return Optional.of(stack.getCompound());
        }
    }
}
