package muramasa.antimatter.registration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public interface IColorHandler {

    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return -1;
    }

    default int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        return -1;
    }
}
