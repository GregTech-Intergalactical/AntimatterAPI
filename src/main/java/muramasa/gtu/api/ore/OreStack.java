package muramasa.gtu.api.ore;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.common.Data;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class OreStack implements IGregTechObject {

    private Material material;
    private StoneType stoneType;
    private MaterialType oreType;

    public OreStack(Material material, StoneType stoneType, MaterialType oreType) {
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

    public MaterialType getOreType() {
        return oreType;
    }

    @Override
    public String getId() {
        return material.getId() + "_" + stoneType.getId() + "_" + oreType.getId();
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(Data.ORE);
        stack.setTagCompound(new NBTTagCompound());
        if (stoneType == null || material == null || oreType == null) return stack;
        stack.getTagCompound().setInteger(Ref.KEY_ORE_STACK_STONE, stoneType.getInternalId());
        stack.getTagCompound().setInteger(Ref.KEY_ORE_STACK_MATERIAL, material.getInternalId());
        stack.getTagCompound().setInteger(Ref.KEY_ORE_STACK_TYPE, oreType.getInternalId());
        return stack;
    }
}
