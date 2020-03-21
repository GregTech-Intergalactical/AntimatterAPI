package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelConfig;
import muramasa.antimatter.client.ModelConfigRandom;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.IntStream;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class BlockSurfaceRock extends BlockDynamic implements IWaterLoggable {

    protected static final int SURFACE_ROCK_MODEL_COUNT = 7;
    protected static final int[] CONFIG_ARRAY = new int[SURFACE_ROCK_MODEL_COUNT];

    static {
        IntStream.range(0, SURFACE_ROCK_MODEL_COUNT).forEach(i -> CONFIG_ARRAY[i] = i);
    }

    protected Material material;
    protected StoneType stoneType;

    public BlockSurfaceRock(String domain, Material material, StoneType stoneType) {
        super(domain, "surface_rock_" + material.getId() + "_" + stoneType.getId(), Block.Properties.create(net.minecraft.block.material.Material.ROCK).hardnessAndResistance(1.0f, 10.0f).sound(SoundType.STONE).doesNotBlockMovement().notSolid(), new Texture("minecraft", "block/stone"));
        this.material = material;
        this.stoneType = stoneType;
        setDefaultState(getStateContainer().getBaseState().with(WATERLOGGED, false));
        AntimatterAPI.register(BlockSurfaceRock.class, this);

        //BlockDynamic
        config = new ModelConfigRandom().set(CONFIG_ARRAY);
        //TODO allow AntimatterModelLoader to load this data from the dynamic model json
        shapes.put(0, Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D));
        shapes.put(1, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 3.0D, 10.0D));
        shapes.put(2, Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 10.0D, 1.0D, 10.0D));
        shapes.put(3, Block.makeCuboidShape(7.0D, 0.0D, 8.0D, 13.0D, 1.0D, 12.0D));
        shapes.put(4, Block.makeCuboidShape(6.0D, 0.0D, 2.0D, 11.0D, 3.0D, 9.0D));
        shapes.put(5, Block.makeCuboidShape(9.0D, 0.0D, 4.0D, 12.0D, 1.0D, 8.0D));
        shapes.put(6, Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 12.0D, 2.0D, 8.0D));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (MaterialType.ROCK.isVisible()) items.add(new ItemStack(this));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (neighbor.up().equals(pos) && !world.getBlockState(neighbor).isSolid()) {
            //world.destroyBlock(pos, true);
        }
    }

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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (player.isCrouching()) return ActionResultType.FAIL;
        harvestBlock(world, player, pos, state, Utils.getTile(world, pos), player.getHeldItem(hand));
        if (super.removedByPlayer(state, world, pos, player, true, null)) {
            player.addItemStackToInventory(MaterialType.ROCK.get(material, 1));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 0f;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        return config;
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        //builder.model("simple", stoneType.getTexture());
        builder.model(Ref.ID + ":block/rock/surface_rock_0", stoneType.getTexture());
        IntStream.range(0, SURFACE_ROCK_MODEL_COUNT).forEach(i -> builder.config(i, Ref.ID + ":block/rock/surface_rock_" + i, c -> c.tex(stoneType.getTexture())));
        prov.state(block, builder);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        super.getInfo(info, world, state, pos);
        info.add("Material: " + material.getId());
        info.add("StoneType: " + stoneType.getId());
        return info;
    }
}
