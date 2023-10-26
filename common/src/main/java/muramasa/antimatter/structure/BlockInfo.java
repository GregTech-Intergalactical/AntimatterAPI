package muramasa.antimatter.structure;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockInfo represents immutable information for block in world
 * This includes block state and tile entity, and needed for complete representation
 * of some complex blocks like machines, when rendering or manipulating them without world instance
 */
@Getter
public class BlockInfo {

    public static final BlockInfo EMPTY = new BlockInfo(Blocks.AIR);

    private final BlockState blockState;

    public BlockInfo(Block block) {
        this(block.defaultBlockState());
    }
    public BlockInfo(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockEntity getTileEntity() {
        return null;
    }

    public void apply(Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, blockState);
    }
}
