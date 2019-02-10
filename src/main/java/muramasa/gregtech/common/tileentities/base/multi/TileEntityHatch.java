package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.capability.impl.HatchComponentHandler;
import muramasa.gregtech.api.capability.impl.MachineStackHandler;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityHatch extends TileEntityMachine {

    private ResourceLocation texture;
    private MachineStackHandler stackHandler;
    private ComponentHandler componentHandler;

    @Override
    public void init(String type, String tier, int facing) {
        super.init(type, tier, facing);
        stackHandler = new MachineStackHandler(this, 0);
        componentHandler = new HatchComponentHandler(type, this);
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
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        } else if (capability == ITechCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stackHandler.getOutputHandler());
        } else if (capability == ITechCapabilities.COMPONENT) {
            return ITechCapabilities.COMPONENT.cast(componentHandler);
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
        compound.setString(Ref.KEY_MACHINE_TILE_TEXTURE, texture.toString());
        return compound;
    }
}
