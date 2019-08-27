package muramasa.gtu.api.blocks;

import muramasa.gtu.api.registration.IItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GTItemBlock extends ItemBlock {

    public GTItemBlock(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
        setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getBlock() instanceof IItemBlock ? ((IItemBlock) getBlock()).getDisplayName(stack) : stack.getUnlocalizedName();
    }
}
