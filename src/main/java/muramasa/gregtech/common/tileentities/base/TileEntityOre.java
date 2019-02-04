package muramasa.gregtech.common.tileentities.base;

import muramasa.gregtech.api.materials.Material;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOre extends TileEntityBase {

    private int materialId, stoneId;

    public void init(int materialId, int textureId) {
        this.materialId = materialId;
        this.stoneId = textureId;
    }

    public Material getMaterial() {
        return Material.get(materialId);
    }

    public int getMaterialId() {
        return materialId;
    }

    public int getStoneId() {
        return stoneId;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        materialId = compound.getInteger("material");
        stoneId = compound.getInteger("stone");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("material", materialId);
        compound.setInteger("stone", stoneId);
        return compound;
    }
}
