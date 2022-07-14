package muramasa.antimatter.tool;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.gt.IEnergyHandlerItem;

public class ToolEnergyHandler extends ItemEnergyHandler implements IEnergyHandlerItem {
    final ItemStack stack;

    public ToolEnergyHandler(ItemStack stack, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.stack = stack;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        return nbt;
    }

    @Override
    public long getCapacity() {
        if (stack.getTag() != null) {
            CompoundTag nbt = stack.getTag();
            if (nbt.contains(Ref.TAG_TOOL_DATA) && nbt.getCompound(Ref.TAG_TOOL_DATA).contains(Ref.KEY_ITEM_MAX_ENERGY)) {
                return nbt.getCompound(Ref.TAG_TOOL_DATA).getLong(Ref.KEY_ITEM_MAX_ENERGY);
            }
        }
        return maxEnergy;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return stack;
    }
}
