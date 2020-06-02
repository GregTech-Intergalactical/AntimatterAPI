package muramasa.antimatter.item;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CapabilityWrapper;
import muramasa.antimatter.capability.impl.ItemEnergyHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemBattery extends ItemBasic<ItemBattery> {
    protected Tier tier;
    final long cap;


    public ItemBattery(String domain, String id, Tier tier, long cap) {
        super(domain, id);
        this.tier = tier;
        this.cap = cap;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (nbt == null) {
            nbt = new CompoundNBT();
        }
        nbt.putLong(ItemEnergyHandler.TAG_CHARGE, 0);
        stack.setTag(nbt);
        return new CapabilityWrapper(stack, new ItemEnergyHandler(stack, 0,cap,tier.getVoltage(),tier.getVoltage(),1,1));
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong(ItemEnergyHandler.TAG_CHARGE,0);
        stack.setTag(nbt);
        super.onCreated(stack, worldIn, playerIn);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null) {
            return 0;
        }
        return (double)getItemEnergy(stack) / (double)cap;
    }

    private long getItemEnergy(ItemStack stack) {
        return stack.getTag().getLong(ItemEnergyHandler.TAG_CHARGE);
    }

    private void setItemEnergy(ItemStack stack, long energy) {
        CompoundNBT nb = stack.getTag();
        nb.putLong(ItemEnergyHandler.TAG_CHARGE, energy);
    }
}
