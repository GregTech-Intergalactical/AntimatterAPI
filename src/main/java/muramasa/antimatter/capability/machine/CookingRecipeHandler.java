package muramasa.antimatter.capability.machine;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.ingredient.impl.Ingredients;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeHooks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static muramasa.antimatter.machine.MachineState.ACTIVE;

public class CookingRecipeHandler<T extends TileEntityMachine> extends MachineRecipeHandler<T> {

    protected int burnDuration = 0;
    protected static final Supplier<List<RecipeIngredient>> BURNABLE = () -> Collections.singletonList(RecipeIngredient.of(Ingredients.BURNABLES, 1));

    public CookingRecipeHandler(T tile) {
        super(tile);
    }

    private void consume() {
        List<ItemStack> stack;
        if (!(stack = tile.itemHandler.map(t -> t.consumeInputs(BURNABLE.get(), false)).orElse(Collections.emptyList())).isEmpty()) {
            burnDuration += ForgeHooks.getBurnTime(stack.get(0)) / 10;
        }
    }

    @Override
    public boolean consumeResourceForRecipe(boolean simulate) {
        if (simulate) return true;
        if (burnDuration == 0) {
            consume();
        } else {
            burnDuration--;
            return burnDuration >= 0;
        }
        return burnDuration > 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("burn", burnDuration);
        return nbt;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        return super.accepts(stack) || Ingredients.BURNABLES.test(stack);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.burnDuration = nbt.getInt("burn");
    }
}
