package muramasa.gtu.api.tileentities;

import muramasa.gtu.Ref;
import muramasa.gtu.client.render.bakedmodels.BakedRock;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityRock extends TileEntityMaterial {

    private byte model;

    public TileEntityRock() {
        model = (byte) Ref.RNG.nextInt(BakedRock.BAKED.length);
    }

    public byte getModel() {
        return model;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Ref.KEY_ROCK_TILE)) model = tag.getByte(Ref.KEY_ROCK_TILE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte(Ref.KEY_ROCK_TILE, model);
        return tag;
    }
}
