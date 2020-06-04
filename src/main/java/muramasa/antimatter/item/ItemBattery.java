package muramasa.antimatter.item;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CapabilityWrapper;
import muramasa.antimatter.capability.impl.ItemEnergyHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBattery extends ItemBasic<ItemBattery> {

    protected Tier tier;
    final long cap;
    final boolean reusable;

    public ItemBattery(String domain, String id, Tier tier, long cap, boolean reusable) {
        super(domain, id);
        this.tier = tier;
        this.cap = cap;
        this.reusable = reusable;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        stack.setTag(ItemEnergyHandler.initNBT(nbt));
        return new ItemEnergyHandler(stack, ItemEnergyHandler.getEnergyFromStack(stack),cap,tier.getVoltage(),tier.getVoltage(),1,1,reusable);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong(ItemEnergyHandler.TAG_CHARGE,0);
        stack.setTag(nbt);
        super.onCreated(stack, worldIn, playerIn);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return ItemEnergyHandler.getEnergyFromStack(stack) > 0 ? 0x00BFFF : super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null) {
            return 1D;
        }
        return 1D - (double)ItemEnergyHandler.getEnergyFromStack(stack) / (double)cap;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        //TODO: Translateable
        tooltip.add(new StringTextComponent("Charge: " + ItemEnergyHandler.getEnergyFromStack(stack)+ "/" + cap + " (" + this.tier.getId() + ")"));
        tooltip.add(new StringTextComponent("Amperage out: " + 1));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
