package muramasa.antimatter.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@FunctionalInterface
public interface IShapeGetter {
    VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);
}
