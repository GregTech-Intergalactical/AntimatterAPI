package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.capability.impl.HatchComponentHandler;
import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

import static muramasa.gregtech.api.machines.MachineFlag.FLUID;
import static muramasa.gregtech.api.machines.MachineFlag.ITEM;

public class TileEntityHatch extends TileEntityMachine {

    private ResourceLocation texture;
    private MachineItemHandler itemHandler;
    private MachineFluidHandler fluidHandler;
    private ComponentHandler componentHandler;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (getType().hasFlag(ITEM)) {
            itemHandler = new MachineItemHandler(this);
        }
        if (getType().hasFlag(FLUID)) {
            fluidHandler = new MachineFluidHandler(this, 8000 * getTierId());
        }
        componentHandler = new HatchComponentHandler(getType(), this);
        texture = super.getTexture();
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public void setTexture(ResourceLocation loc) {
        texture = loc;
    }

    @Override
    public MachineItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        } else if (capability == GTCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler.getOutputHandler());
        } else if (capability == GTCapabilities.COMPONENT) {
            return GTCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_TEXTURE)) {
            texture = new ResourceLocation(compound.getString(Ref.KEY_MACHINE_TILE_TEXTURE));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (texture != null) {
            compound.setString(Ref.KEY_MACHINE_TILE_TEXTURE, texture.toString());
        }
        return compound;
    }
}
