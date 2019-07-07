package muramasa.gtu.api.tileentities;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Material;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOre extends TileEntityBase {

    private Material material = Materials.NULL;

    public void init(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Ref.KEY_ORE_TILE)) material = Material.get(tag.getInteger(Ref.KEY_ORE_TILE));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger(Ref.KEY_ORE_TILE, material.getInternalId());
        return tag;
    }
}
