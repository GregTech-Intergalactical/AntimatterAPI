package muramasa.itech.common.tileentities;

import muramasa.itech.api.materials.Material;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOre extends TileEntityBase {

    private String material = "NULL";

    public void init(String material) {
        this.material = material;
    }

    public Material getMaterial() {
        return Material.get(material);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        material = compound.getString("material");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("material", material);
        return compound;
    }
}
