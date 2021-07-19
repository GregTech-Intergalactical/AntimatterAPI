package muramasa.antimatter.tool;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import net.minecraft.nbt.CompoundNBT;

public class ToolEnergyHandler extends ItemEnergyHandler {
    long maxEnergy;
    public ToolEnergyHandler(long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        maxEnergy = capacity;
    }

    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, this.energy);
        nbt.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, this.maxEnergy);
        nbt.putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, this.discharge);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.energy = nbt.getLong(Ref.KEY_TOOL_DATA_ENERGY);
        this.maxEnergy = nbt.getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
        this.discharge = nbt.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    @Override
    public long getCapacity() {
        return maxEnergy;
    }
}
