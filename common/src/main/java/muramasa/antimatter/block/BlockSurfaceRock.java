package muramasa.antimatter.block;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.dynamic.ModelConfigRandom;
import muramasa.antimatter.material.IMaterialObject;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BlockSurfaceRock extends BlockDynamic implements SimpleWaterloggedBlock, ISharedAntimatterObject, IColorHandler, IMaterialObject {

    protected static final int SURFACE_ROCK_MODEL_COUNT = 7;
    protected static final int[] CONFIG_ARRAY = new int[SURFACE_ROCK_MODEL_COUNT];

    static {
        IntStream.range(0, SURFACE_ROCK_MODEL_COUNT).forEach(i -> CONFIG_ARRAY[i] = i);
    }

    protected Material material;
    protected StoneType stoneType;
    protected final ImmutableMap<String, Texture> textureMap;

    public BlockSurfaceRock(String domain, Material material, StoneType stoneType) {
        super(domain, "surface_rock_" + (material == Material.NULL ? "" :material.getId() + "_" ) + stoneType.getId(), Properties.of(net.minecraft.world.level.material.Material.DECORATION).explosionResistance(1.0f).instabreak().sound(SoundType.STONE).noCollission().noOcclusion());
        this.material = material;
        this.stoneType = stoneType;
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));

        //BlockDynamic
        config = new ModelConfigRandom().set(new BlockPos(0,0,0),CONFIG_ARRAY);
        shapes.put(0, Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D));
        shapes.put(1, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 3.0D, 10.0D));
        shapes.put(2, Block.box(4.0D, 0.0D, 4.0D, 10.0D, 1.0D, 10.0D));
        shapes.put(3, Block.box(7.0D, 0.0D, 8.0D, 13.0D, 1.0D, 12.0D));
        shapes.put(4, Block.box(6.0D, 0.0D, 2.0D, 11.0D, 3.0D, 9.0D));
        shapes.put(5, Block.box(9.0D, 0.0D, 4.0D, 12.0D, 1.0D, 8.0D));
        shapes.put(6, Block.box(5.0D, 0.0D, 4.0D, 12.0D, 2.0D, 8.0D));
        String overlay = material == Material.NULL ? "block/empty" : "material/rock_overlay";
        textureMap = ImmutableMap.of("all", stoneType.getTexture(), "overlay", new Texture(Ref.ID, overlay));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (AntimatterMaterialTypes.ROCK.isVisible()) items.add(new ItemStack(this));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, world, pos, block, fromPos, moving);
        if (fromPos.above().equals(pos) && !world.getBlockState(fromPos).canOcclude()) {
            if (!world.isClientSide) {
                Utils.breakBlock(world, null, ItemStack.EMPTY, pos, 0);
            }
        }
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int i) {
        return i == 1 ? material.getRGB() : -1;
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult) {
        if (player.isCrouching()) return InteractionResult.FAIL;
        if (!player.addItem(AntimatterMaterialTypes.ROCK.get(material, 1))) {
            Containers.dropContents(world, pos, NonNullList.of(ItemStack.EMPTY, AntimatterMaterialTypes.ROCK.get(material, 1)));
        }
        world.removeBlock(pos, true);
        return InteractionResult.SUCCESS;
  //      }
        //return InteractionResult.FAIL;
    }

    @Override
    public ModelConfig getConfig(BlockState state, BlockGetter world, BlockPos.MutableBlockPos mut, BlockPos pos) {
        return config.set(pos, config.getConfig());
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        AntimatterBlockModelBuilder builder = prov.getBuilder(block);
        //builder.model("simple", stoneType.getTexture());
        builder.model(Ref.ID + ":block/rock/surface_rock_0", textureMap).particle(textureMap.get("all"));
        IntStream.range(0, SURFACE_ROCK_MODEL_COUNT).forEach(i -> builder.config(i, Ref.ID + ":block/rock/surface_rock_" + i, c -> c.tex(textureMap)));
        prov.state(block, builder);
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(prov.existing("antimatter", "block/rock/surface_rock_0")).tex(textureMap);
    }

    @Override
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos) {
        super.getInfo(info, world, state, pos);
        info.add("Material: " + material.getId());
        info.add("StoneType: " + stoneType.getId());
        return info;
    }

    public Material getMaterial() {
        return material;
    }

    public StoneType getStoneType() {
        return stoneType;
    }
}
