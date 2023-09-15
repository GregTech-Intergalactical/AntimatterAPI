package muramasa.antimatter.integration.fabric.megane.provider;

import lol.bai.megane.api.provider.ProgressProvider;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MachineProgressProvider extends ProgressProvider<TileEntityMachine> {
    @Override
    public int getInputSlotCount() {
        return getCastedObject().itemHandler.map(i -> i.getInputHandler().getSlots()).orElse(0);
    }

    @Override
    public int getOutputSlotCount() {
        return getCastedObject().itemHandler.map(i -> i.getOutputHandler().getSlots()).orElse(0);
    }

    @Override
    public @NotNull ItemStack getInputStack(int slot) {
        return getCastedObject().itemHandler.map(i -> i.getInputHandler().getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getOutputStack(int slot) {
        return getCastedObject().itemHandler.map(i -> i.getOutputHandler().getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public int getPercentage() {
        return getCastedObject().recipeHandler.map(r -> ((float)r.getCurrentProgress() / (float) r.getMaxProgress()) * 100).orElse(0f).intValue();
    }

    TileEntityMachine<?> getCastedObject(){
        return (TileEntityMachine<?>) getObject();
    }
}
