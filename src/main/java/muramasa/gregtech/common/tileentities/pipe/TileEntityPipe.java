package muramasa.gregtech.common.tileentities.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Pipe;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class TileEntityPipe extends TileEntityBase {

    protected Pipe type;
    protected PipeSize size;

    public final void init(Pipe type, PipeSize size) {
        this.type = type;
        this.size = size;
//        markForRenderUpdate();
    }

    public Pipe getType() {
        return type;
    }

    public PipeSize getSize() {
        return size;
    }

    public void refreshConnections() {

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
        if (size != null) compound.setInteger(Ref.KEY_PIPE_SIZE, size.ordinal());
        return compound;
    }
}
