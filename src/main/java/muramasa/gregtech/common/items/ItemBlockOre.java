package muramasa.gregtech.common.items;

import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.common.blocks.BlockOre;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockOre extends ItemBlock {

    public ItemBlockOre(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Material material = ((BlockOre) Block.getBlockFromItem(stack.getItem())).getMaterial();
        if (material != null) {
            return Prefix.Ore.getDisplayName(material);
        }
        return getUnlocalizedName();
    }
}
