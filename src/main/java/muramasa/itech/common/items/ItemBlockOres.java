package muramasa.itech.common.items;

import muramasa.itech.api.materials.Materials;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockOres extends ItemBlock {

    public ItemBlockOres(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
//        return Prefix.ORE.getDisplayName(Materials.generated[stack.getMetadata()]);
        return "ORE NAME ERROR";
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) {
                return Materials.generated[stack.getMetadata()].getRGB();
            }
            return -1;
        }
    }
}
