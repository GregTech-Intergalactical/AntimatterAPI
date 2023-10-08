package muramasa.antimatter.gui.slot;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.Data;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.item.ItemFluidIcon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;

public class SlotFluidDisplaySettable extends SlotFake {
    public SlotFluidDisplaySettable(SlotType<SlotFake> type, IGuiHandler tile, ExtendedItemContainer stackHandler, int index, int x, int y) {
        super(type, tile, stackHandler, index, x, y, true);
    }

    @Override
    public ItemStack clickSlot(int clickedButton, ClickType clickType, Player playerEntity, AbstractContainerMenu container) {
        if (container.getCarried().isEmpty() || FluidHooks.safeGetItemFluidManager(container.getCarried()).map(f -> !f.getFluidInTank(0).isEmpty()).orElse(false)){
            return super.clickSlot(clickedButton, clickType, playerEntity, container);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (!stack.isEmpty()){
            ItemStack[] stacks = new ItemStack[1];
            stacks[0] = ItemStack.EMPTY;
            FluidHooks.safeGetItemFluidManager(stack).ifPresent(f -> {
                FluidHolder fluidHolder = f.getFluidInTank(0);
                if (!fluidHolder.isEmpty()){
                    stacks[0] = Data.FLUID_ICON.fill(fluidHolder.getFluid());
                }
            });
            super.set(stacks[0]);
            return;
        }
        super.set(stack);
    }
}
