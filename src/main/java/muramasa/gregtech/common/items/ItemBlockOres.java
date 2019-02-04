package muramasa.gregtech.common.items;

import muramasa.gregtech.api.materials.Material;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockOres extends ItemBlock {

    public ItemBlockOres(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String namePre = I18n.format("prefix.pre.ore.name");
        namePre = namePre.equals("") ? "" : namePre + " ";
        String namePost = I18n.format("prefix.post.ore.name");
        namePost = namePost.equals("") ? "" : " " + namePost;
        return namePre + Material.get(stack.getMetadata()).getDisplayName() + namePost ;
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 1) {
                return Material.generated[stack.getMetadata()].getRGB();
            }
            return -1;
        }
    }
}
