package muramasa.antimatter.block;

import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IItemBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class AntimatterItemBlock extends BlockItem {

    public AntimatterItemBlock(Block block) {
        super(block, new Item.Properties().tab(block instanceof IItemBlockProvider ? ((IItemBlockProvider) block).getItemGroup() : Ref.TAB_BLOCKS));
        if (block.getRegistryName() != null) setRegistryName(block.getRegistryName());
    }

    @Nonnull
    @Override
    public ITextComponent getName(@Nonnull ItemStack stack) {
        return getBlock() instanceof IItemBlockProvider ? ((IItemBlockProvider) getBlock()).getDisplayName(stack) : new TranslationTextComponent(stack.getDescriptionId());
    }
}
