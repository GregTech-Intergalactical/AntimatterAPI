package muramasa.antimatter.blocks;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.*;
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
            Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 12.0D, 2.0D, 8.0D)
    };

    protected Material material;
    protected StoneType stoneType;

    public BlockSurfaceRock(String domain, Material material, StoneType stoneType) {
        super(domain, "surface_rock" + material.getId() + "_" + stoneType.getId(), Block.Properties.create(net.minecraft.block.material.Material.ROCK).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.STONE), new Texture("minecraft", "block/stone"));
        this.material = material;
        this.stoneType = stoneType;
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
        return new int[]{state.get(AntimatterProperties.ROCK_MODEL) + 1};
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

//    @Override
//    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
//        if (fromPos.up().equals(pos)) {
//            if (world.getBlockState(fromPos).getBlockFaceShape(world, fromPos, Direction.UP) != BlockFaceShape.SOLID) {
//                world.destroyBlock(pos, true);
//            }
//        }
//    }

//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityMaterial) {
//            TileEntityMaterial ore = (TileEntityMaterial) tile;
//            if (ore.getMaterial() == Materials.NULL) {
//                int chance = Ref.RNG.nextInt(4);
//                drops.add(Materials.Stone.getDustTiny(chance == 0 ? 1 : chance));
//            }
//            else drops.add(ore.getMaterial().getRock(1));
//        }
//    }

//    @Override
//    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
//        if (player.isCrouching()) return ActionResultType.FAIL;
//        harvestBlock(world, player, pos, state, Utils.getTile(world, pos), player.getHeldItem(hand));
//        if (super.removedByPlayer(state, world, pos, player, true, null)) {
//            return ActionResultType.SUCCESS;
//        }
//        return ActionResultType.FAIL;
//    }

//    @Override
//    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
//        ConfiguredModel[] models = new ConfiguredModel[7];
//        for (int i = 0; i < models.length; i++) {
//            models[i] = new ConfiguredModel(provider.getBuilder(getId()).parent(provider.getExistingFile(provider.modLoc("block/rock/rock_" + i))).texture("all", getStoneType().getTexture()));
//        }
//        provider.getVariantBuilder(this).partialState().setModels(models);
//    }

    public static BlockState get(Material material, StoneType stoneType) {
        BlockSurfaceRock rock = AntimatterAPI.get(BlockSurfaceRock.class, "rock_" + material.getId() + "_" + stoneType.getId());
        return rock != null ? rock.getDefaultState() : Blocks.AIR.getDefaultState();
    }
}
