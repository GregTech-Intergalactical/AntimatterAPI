package muramasa.antimatter.tool;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ToolEnergyHandler extends ItemEnergyHandler {
    long maxEnergy;
    final ItemStack stack;

    public ToolEnergyHandler(ItemStack stack, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        maxEnergy = capacity;
        this.stack = stack;
    }

    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, this.energy);
        nbt.putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, this.discharge);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.energy = nbt.getLong(Ref.KEY_TOOL_DATA_ENERGY);
        this.discharge = nbt.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    @Override
    public long getCapacity() {
        if (stack.getTag() != null) {
            CompoundTag nbt = stack.getTag();
            if (nbt.contains(Ref.TAG_TOOL_DATA) && nbt.getCompound(Ref.TAG_TOOL_DATA).contains(Ref.KEY_TOOL_DATA_MAX_ENERGY)) {
                return nbt.getCompound(Ref.TAG_TOOL_DATA).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
            }
        }
        return maxEnergy;
    }
}
