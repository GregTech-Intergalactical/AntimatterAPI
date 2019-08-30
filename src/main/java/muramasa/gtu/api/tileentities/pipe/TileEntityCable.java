package muramasa.gtu.api.tileentities.pipe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class TileEntityCable extends TileEntityPipe {

    @Override
    public boolean canConnect(TileEntity tile) {
        return tile instanceof TileEntityCable;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        return info;
    }
}
