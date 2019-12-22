package muramasa.gtu.api.registration;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.GTItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface IItemBlock {

    default GTItemBlock getItemBlock(Block block) {
        return new GTItemBlock(block);
    }

    default ItemGroup getItemGroup() {
        return Ref.TAB_BLOCKS;
    }

    default ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(stack.getTranslationKey());
    }
}
