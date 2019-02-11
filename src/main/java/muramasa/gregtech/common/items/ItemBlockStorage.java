package muramasa.gregtech.common.items;

import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.common.blocks.BlockStorage;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockStorage extends ItemBlock {

    public ItemBlockStorage(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Material material = ((BlockStorage) Block.getBlockFromItem(stack.getItem())).getMaterial();
        if (material != null) {
            return Prefix.Block.getDisplayName(material);
        }
        return getUnlocalizedName();
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) {
                Material material = ((BlockStorage) Block.getBlockFromItem(stack.getItem())).getMaterial();
                if (material != null) {
                    return material.getRGB();
                }
            }
            return -1;
        }
    }
}
