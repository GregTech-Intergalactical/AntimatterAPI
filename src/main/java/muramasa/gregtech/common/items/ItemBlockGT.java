package muramasa.gregtech.common.items;

import muramasa.gregtech.api.registration.IHasItemBlock;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockGT extends ItemBlock {

    public ItemBlockGT(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (getBlock() instanceof IHasItemBlock) {
            return ((IHasItemBlock) getBlock()).getItemStackDisplayName(getBlock(), stack);
        }
        return stack.getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (getBlock() instanceof IHasItemBlock) {
            List<String> lines = ((IHasItemBlock) getBlock()).addInformation(stack);
            if (lines.size() > 0) tooltip.addAll(lines);
        }
    }
}
