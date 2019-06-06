package muramasa.gtu.api.registration;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public interface IColorHandler {

    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return -1;
    }

    default int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        return -1;
    }
}
