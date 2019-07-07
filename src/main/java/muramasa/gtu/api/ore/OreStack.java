package muramasa.gtu.api.ore;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IGregTechObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class OreStack implements IGregTechObject {

    private BlockOre block;
    private Material material;
    private StoneType stoneType;
    private OreType oreType;

    public OreStack(BlockOre block, Material material, StoneType stoneType, OreType oreType) {
        this.block = block;
        this.material = material;
        this.stoneType = stoneType;
        this.oreType = oreType;
    }

    @Override
    public String getId() {
        return material.getId() + "_" + stoneType.getId() + "_" + oreType.getName();
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(block);
        stack.setTagCompound(new NBTTagCompound());
        if (stoneType == null || material == null) return stack;
        stack.getTagCompound().setString(Ref.KEY_ORE_STACK_STONE, stoneType.getId());
        stack.getTagCompound().setInteger(Ref.KEY_ORE_STACK_MATERIAL, material.getInternalId());
        stack.getTagCompound().setInteger(Ref.KEY_ORE_STACK_TYPE, oreType.ordinal());
        return stack;
    }
}
