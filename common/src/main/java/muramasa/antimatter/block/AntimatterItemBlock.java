package muramasa.antimatter.block;

import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IItemBlockProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class AntimatterItemBlock extends BlockItem {

    public AntimatterItemBlock(Block block) {
        super(block, new Properties().tab(block instanceof IItemBlockProvider ? ((IItemBlockProvider) block).getItemGroup() : Ref.TAB_BLOCKS));
        if (block.getRegistryName() != null) setRegistryName(block.getRegistryName());
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return getBlock() instanceof IItemBlockProvider ? ((IItemBlockProvider) getBlock()).getDisplayName(stack) : new TranslatableComponent(stack.getDescriptionId());
    }
}
