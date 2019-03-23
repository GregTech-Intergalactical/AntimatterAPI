package muramasa.gregtech.common.tileentities.base;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.PipeSize;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityPipe extends TileEntityBase {

    private PipeSize size;

    public final void init(PipeSize size) {
        this.size = size;
//        markForRenderUpdate();
    }

    public PipeSize getSize() {
        return size;
    }

    public void toggleConnection(EnumFacing side) {
        
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_PIPE_SIZE)) size = PipeSize.VALUES[compound.getInteger(Ref.KEY_PIPE_SIZE)];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(Ref.KEY_PIPE_SIZE, size.ordinal());
        return compound;
    }
}
