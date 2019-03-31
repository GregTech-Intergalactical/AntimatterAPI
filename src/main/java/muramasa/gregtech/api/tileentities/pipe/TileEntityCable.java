package muramasa.gregtech.api.tileentities.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Cable;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class TileEntityCable extends TileEntityPipe {

    protected boolean insulated;

    public final void init(Cable type, PipeSize size, boolean insulated) {
        super.init(type, size);
        this.insulated = insulated;
    }

    public Cable getType() {
        return (Cable) type;
    }

    public boolean isInsulated() {
        return insulated;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_CABLE_INSULATED)) insulated = compound.getBoolean(Ref.KEY_CABLE_INSULATED);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(Ref.KEY_CABLE_INSULATED, insulated);
        return compound;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Insulated: " + isInsulated());
        return info;
    }
}
