package muramasa.antimatter.registration;

import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface IItemBlockProvider {

    default AntimatterItemBlock getItemBlock(Block block) {
        return new AntimatterItemBlock(block);
    }

    default ItemGroup getItemGroup() {
        return Ref.TAB_BLOCKS;
    }

    default ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(stack.getTranslationKey());
    }
}
