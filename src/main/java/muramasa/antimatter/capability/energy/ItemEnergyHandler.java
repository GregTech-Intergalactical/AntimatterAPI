package muramasa.antimatter.capability.energy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ItemEnergyHandler represents the Antimatter Energy capability implementation for items.
 * It wraps an item and provides the ability to charge it & remove it, depending on if the item supports it.
 */
public class ItemEnergyHandler extends EnergyHandler implements ICapabilityProvider {

    private final LazyOptional<ItemEnergyHandler> handler = LazyOptional.of(() -> this);
    protected final ItemStack stack;

    protected boolean discharge;

    public ItemEnergyHandler(ItemStack stack, long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(energy, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.stack = stack;
    }

    @Override
    public boolean canInput() {
        return voltageIn > 0 /*&& getTagEnergy() != capacity*/ ;
    }

    @Override
    public boolean canOutput() {
        return canDischarge() && voltageOut > 0 /*&& getTagEnergy() >= voltageOut*/ ;
    }

    private boolean canDischarge() {
        return stack.getOrCreateTag().contains(Ref.KEY_ITEM_DISCHARGE_MODE) && stack.getTag().getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    public static long getEnergyFromStack(ItemStack stack) {
        return stack.getOrCreateTag().getLong(Ref.KEY_ITEM_ENERGY);
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        long energy = super.insert(maxReceive, simulate);
        if (!simulate) {
            stack.getOrCreateTag().putLong(Ref.KEY_ITEM_ENERGY, this.energy);
        }
        return energy;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        long energy = super.extract(maxExtract, simulate);
        if (!simulate) {
            stack.getOrCreateTag().putLong(Ref.KEY_ITEM_ENERGY, this.energy);
        }
        return energy;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong(Ref.KEY_ITEM_ENERGY, this.energy);
        nbt.putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, this.discharge);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.energy = nbt.getLong(Ref.KEY_ITEM_ENERGY);
        this.discharge = nbt.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == AntimatterCaps.ENERGY_HANDLER_CAPABILITY ? handler.cast() : LazyOptional.empty();
    }
}
