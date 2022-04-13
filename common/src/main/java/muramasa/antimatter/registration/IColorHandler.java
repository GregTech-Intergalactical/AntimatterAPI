package muramasa.antimatter.registration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IColorHandler {

    default boolean registerColorHandlers() {
        return true;
    }

    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return -1;
    }

    default int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int i) {
        return -1;
    }
}
