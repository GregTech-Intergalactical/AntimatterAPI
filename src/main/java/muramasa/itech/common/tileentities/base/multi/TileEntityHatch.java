package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.api.enums.HatchTexture;
import muramasa.itech.common.blocks.BlockHatches;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityHatch extends TileEntityComponent {

    private String typeFromNBT = "", tierFromNBT = "";
    private ItemStackHandler stackHandler;

    public void init(String type, String tier) {
        typeFromNBT = type;
        tierFromNBT = tier;
        stackHandler = new ItemStackHandler(1);
        setState(getState().withProperty(BlockHatches.TEXTURE, HatchTexture.get(tier)));
    }

    public String getType() {
        return typeFromNBT;
    }

    public String getTier() {
        return tierFromNBT;
    }

    public ItemStackHandler getStackHandler() {
        return stackHandler;
    }

    @Override
    public String getId() {
        return typeFromNBT;
    }

    @Override
    public void linkController(TileEntityMultiMachine tile) {
        super.linkController(tile);
        setState(getState().withProperty(BlockHatches.TEXTURE, HatchTexture.get(tile.getType())));
        markDirty();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine tile) {
        super.unlinkController(tile);
        setState(getState().withProperty(BlockHatches.TEXTURE, HatchTexture.get(tierFromNBT)));
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_MACHINE_STACK_TYPE) && compound.hasKey(Ref.KEY_MACHINE_STACK_TIER)) {
            typeFromNBT = compound.getString(Ref.KEY_MACHINE_STACK_TYPE);
            tierFromNBT = compound.getString(Ref.KEY_MACHINE_STACK_TIER);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString(Ref.KEY_MACHINE_STACK_TYPE, getType());
        compound.setString(Ref.KEY_MACHINE_STACK_TIER, getTier());
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return super.getCapability(capability, facing);
    }
}
