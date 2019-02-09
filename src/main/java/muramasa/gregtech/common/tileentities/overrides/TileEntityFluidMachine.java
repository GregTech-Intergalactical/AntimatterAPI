package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityFluidMachine extends TileEntityBasicMachine {

    @Override
    public Recipe findRecipe() {
        return getMachineType().findRecipe(stackHandler.getInputs(), inputTank.getFluid());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        fluidData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_FLUIDS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (inputTank != null) {
            compound.setTag(Ref.KEY_MACHINE_TILE_FLUIDS, inputTank.serializeNBT());
        }
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return facing == null || coverHandler.hasCover(facing, CoverType.FLUID_PORT);
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank);
        }
        return super.getCapability(capability, facing);
    }
}
