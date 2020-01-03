package muramasa.gtu.api.ore;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.GregTechProperties;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IModelProvider;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.data.providers.GregTechBlockStateProvider;
import muramasa.gtu.data.providers.GregTechItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRock extends BlockMaterialStone implements IModelProvider {

    public BlockRock(Material material, StoneType stoneType) {
        super(material, stoneType, Block.Properties.create(net.minecraft.block.material.Material.ROCK).sound(stoneType.getSoundType()));
        //setHardness(0.2f);
        //setResistance(0.2f);
        setRegistryName(getId());
        GregTechAPI.register(BlockRock.class, this);
        setDefaultState(getStateContainer().getBaseState().with(GregTechProperties.ROCK_MODEL, 0));
    }

    @Override
    public String getId() {
        return "rock_" + getMaterial().getId() + "_" + getStoneType().getId();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(GregTechProperties.ROCK_MODEL);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return getStoneType().getSoundType();
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.create(0.3, 0, 0.3, 0.7, 0.125, 0.7);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return getRenderShape(state, world, pos);
    }

    //    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
//        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 0.125, 0.7);
//    }
//
//    @Nullable
//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
//        return null;
//    }


    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
//        if (fromPos.up().equals(pos)) {
//            if (world.getBlockState(fromPos).getBlockFaceShape(world, fromPos, Direction.UP) != BlockFaceShape.SOLID) {
//                world.destroyBlock(pos, true);
//            }
//        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return getMaterial().getDust(1);
    }

    /** TileEntity Drops Start **/
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
//
//    @Override
//    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
//        if (willHarvest) return true;
//        return super.removedByPlayer(state, world, pos, player, willHarvest);
//    }
//
//    @Override
//    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tile, ItemStack stack) {
//        super.harvestBlock(world, player, pos, state, tile, stack);
//        world.setBlockToAir(pos);
//    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (player.isSneaking()) return false;
        harvestBlock(world, player, pos, state, Utils.getTile(world, pos), player.getHeldItem(hand));
        return super.removedByPlayer(state, world, pos, player, true, null);
    }

    /** TileEntity Drops End **/

    public static BlockState get(Material material, StoneType stoneType) {
        return GregTechAPI.get(BlockRock.class, "rock_" + material.getId() + "_" + stoneType.getId()).getDefaultState();
    }

    @Override
    public void onItemModelBuild(IItemProvider item, GregTechItemModelProvider provider) {

    }

    @Override
    public void onBlockModelBuild(Block block, GregTechBlockStateProvider provider) {
//        ConfiguredModel[] models = new ConfiguredModel[7];
//        for (int i = 0; i < models.length; i++) {
//            models[i] = new ConfiguredModel(provider.getBuilder(getId()).parent(provider.getExistingFile(provider.modLoc("block/rock/rock_" + i))).texture("all", getStoneType().getTexture()));
//        }
//        provider.getVariantBuilder(this).partialState().setModels(models);
    }
}
