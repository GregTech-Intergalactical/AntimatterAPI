package muramasa.antimatter.blocks;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class BlockSurfaceRock extends BlockDynamic implements IWaterLoggable {
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D),
            Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 3.0D, 10.0D),
            Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 10.0D, 1.0D, 10.0D),
            Block.makeCuboidShape(7.0D, 0.0D, 8.0D, 13.0D, 1.0D, 12.0D),
            Block.makeCuboidShape(6.0D, 0.0D, 2.0D, 11.0D, 3.0D, 9.0D),
            Block.makeCuboidShape(9.0D, 0.0D, 4.0D, 12.0D, 1.0D, 8.0D),
            Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 12.0D, 2.0D, 8.0D)};
    public BlockSurfaceRock() {
        super(Ref.ID, "surface_rock",
                Block.Properties.create(Material.ROCK).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.STONE),
                new Texture("minecraft", "block/stone"));
        AntimatterAPI.register(BlockSurfaceRock.class, this);
    }
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.get(AntimatterProperties.ROCK_MODEL)];
    }
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AntimatterProperties.ROCK_MODEL, WATERLOGGED);
    }
    @Override
    public int[] getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int[] c = new int[1];
        c[0] = state.get(AntimatterProperties.ROCK_MODEL) + 1;
        return c;
    }
    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
