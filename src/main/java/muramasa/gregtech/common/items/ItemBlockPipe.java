package muramasa.gregtech.common.items;

import muramasa.gregtech.api.pipe.types.Pipe;
import muramasa.gregtech.common.blocks.pipe.BlockPipe;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockPipe extends ItemBlock {

    public ItemBlockPipe(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getType().getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        List<String> lines = getType().getTooltip(stack);
        if (lines.size() > 0) tooltip.addAll(lines);
    }

    public Pipe getType() {
        return ((BlockPipe) getBlock()).getType();
    }
}
