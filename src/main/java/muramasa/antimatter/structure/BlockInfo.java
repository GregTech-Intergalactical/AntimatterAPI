package muramasa.antimatter.structure;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * BlockInfo represents immutable information for block in world
 * This includes block state and tile entity, and needed for complete representation
 * of some complex blocks like machines, when rendering or manipulating them without world instance
 */
public class BlockInfo {

    public static final BlockInfo EMPTY = new BlockInfo(Blocks.AIR);

    private final BlockState blockState;
    private final TileEntity tileEntity;

    public BlockInfo(Block block) {
        this(block.defaultBlockState());
    }

    public BlockInfo(BlockState blockState) {
        this(blockState, null);
    }
    public BlockInfo(BlockState blockState, TileEntity tileEntity) {
        this.blockState = blockState;
        this.tileEntity = tileEntity;
        Preconditions.checkArgument(tileEntity == null || blockState.getBlock().hasTileEntity(blockState),
                "Cannot create block info with tile entity for block not having it");
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    public void apply(World world, BlockPos pos) {
        world.setBlockAndUpdate(pos, blockState);
        if (tileEntity != null) {
            world.setBlockEntity(pos, tileEntity);
        }
    }
}
