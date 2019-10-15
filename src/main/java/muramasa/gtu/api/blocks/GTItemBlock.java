package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.registration.IItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GTItemBlock extends BlockItem {

    public GTItemBlock(Block block) {
        super(block, new Item.Properties().group(block instanceof IItemBlock ? ((IItemBlock) block).getItemGroup() : Ref.TAB_BLOCKS));
        if (block.getRegistryName() != null) setRegistryName(block.getRegistryName());
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return getBlock() instanceof IItemBlock ? ((IItemBlock) getBlock()).getDisplayName(stack) : new TranslationTextComponent(stack.getTranslationKey());
    }
}
