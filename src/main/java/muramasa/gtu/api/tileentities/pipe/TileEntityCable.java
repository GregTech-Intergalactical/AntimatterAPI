package muramasa.gtu.api.tileentities.pipe;

import muramasa.gtu.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class TileEntityCable extends TileEntityPipe {

    protected boolean insulated;

    public final void init(boolean insulated) {
        this.insulated = insulated;
    }

    public boolean isInsulated() {
        return insulated;
    }

    @Override
    public boolean canConnect(TileEntity tile) {
        return tile instanceof TileEntityCable;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Ref.KEY_CABLE_INSULATED)) insulated = tag.getBoolean(Ref.KEY_CABLE_INSULATED);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(Ref.KEY_CABLE_INSULATED, insulated);
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Insulated: " + isInsulated());
        return info;
    }
}
