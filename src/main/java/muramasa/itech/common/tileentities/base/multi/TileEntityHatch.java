package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.capability.impl.ComponentHandler;
import muramasa.itech.api.capability.impl.HatchComponentHandler;
import muramasa.itech.api.capability.impl.MachineStackHandler;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityHatch extends TileEntityMachine {

    private int textureId;
    private MachineStackHandler stackHandler;
    private ComponentHandler componentHandler;

    @Override
    public void init(String type, String tier, int facing) {
        super.init(type, tier, facing);
        stackHandler = new MachineStackHandler(this);
        componentHandler = new HatchComponentHandler(type, this);
        textureId = getTierId();
    }

    @Override
    public int getTextureId() {
        return textureId;
    }

    @Override
    public void setTextureId(int newId) {
        textureId = newId;
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stackHandler);
        } else if (capability == ITechCapabilities.COMPONENT) {
            return ITechCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_TEXTURE)) {
            textureId = compound.getInteger(Ref.KEY_MACHINE_TILE_TEXTURE);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(Ref.KEY_MACHINE_TILE_TEXTURE, textureId);
        return compound;
    }
}
