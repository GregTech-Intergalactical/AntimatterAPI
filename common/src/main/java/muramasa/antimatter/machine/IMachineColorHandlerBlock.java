package muramasa.antimatter.machine;

import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IMachineColorHandlerBlock {
    int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, @Nullable BlockEntityMachine<?> machine, int i);
}
