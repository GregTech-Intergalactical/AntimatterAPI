package muramasa.gtu.api.registration;

import muramasa.gtu.api.blocks.GTItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface IItemBlock {

    default GTItemBlock getItemBlock(Block block) {
        return new GTItemBlock(block);
    }

    default String getDisplayName(ItemStack stack) {
        return stack.getUnlocalizedName();
    }
}
