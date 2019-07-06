package muramasa.gtu.api.tileentities;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.ore.StoneType;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOre extends TileEntityBase {

    private Material material;
    private StoneType stoneType;
    private MaterialType oreType;

    public TileEntityOre() {

    }

    public TileEntityOre(Material material, StoneType stoneType, MaterialType oreType) {
        this.material = material;
        this.stoneType = stoneType;
        this.oreType = oreType;
    }

    public void init(Material material, StoneType stoneType, MaterialType oreType) {
        this.material = material;
        this.stoneType = stoneType;
        this.oreType = oreType;
    }

    public Material getMaterial() {
        return material;
    }

    public StoneType getStoneType() {
        return stoneType;
    }

    public MaterialType getType() {
        return oreType;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Ref.KEY_ORE_TILE)) {
            int data = tag.getInteger(Ref.KEY_ORE_TILE);
            oreType = MaterialType.ORE_TYPES.get(data / 10000000);
            stoneType = StoneType.get((data % 10000000) / 10000);
            material = Material.get((data % 10000000) % 10000);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (material == null || stoneType == null || oreType == null) return tag;
        int data = material.getInternalId() + (stoneType.getInternalId() * 10000) + (oreType.getInternalId() * 10000000);
        tag.setInteger(Ref.KEY_ORE_TILE, data);
        return tag;
    }
}
