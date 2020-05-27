package muramasa.antimatter.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.block.BlockDynamic;
import muramasa.antimatter.block.IInfoProvider;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IInteractHandler;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.ModelConfig;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

public abstract class BlockPipe<T extends PipeType<?>> extends BlockDynamic implements IItemBlockProvider, IColorHandler, IInfoProvider {

    protected PipeType<?> type;
    protected PipeSize size;

    protected int modelId = 0;
    protected Texture side = new Texture(Ref.ID, "block/pipe/pipe_side");
    protected Texture[] faces = new Texture[] {new Texture(Ref.ID, "block/pipe/pipe_vtiny"), new Texture(Ref.ID, "block/pipe/pipe_tiny"), new Texture(Ref.ID, "block/pipe/pipe_small"), new Texture(Ref.ID, "block/pipe/pipe_normal"), new Texture(Ref.ID, "block/pipe/pipe_large"), new Texture(Ref.ID, "block/pipe/pipe_huge")};

    public BlockPipe(String prefix, PipeType<?> type, PipeSize size) {
        super(type.getDomain(), prefix + "_" + type.getMaterial().getId() + "_" + size.getId(), Block.Properties.create(net.minecraft.block.material.Material.IRON));
        this.type = type;
        this.size = size;
        AntimatterAPI.register(BlockPipe.class, getId(), this);
    }

    public T getType() {
        return (T) type;
    }

    public PipeSize getSize() {
        return size;
    }

    public int getRGB() {
        return getType().getMaterial().getRGB();
    }

    public int getModelId() {
        return modelId;
    }

    public Texture getSide() {
        return side;
    }

    public Texture getFace() {
        return faces[size.ordinal()];
    }

    @Override
    public AntimatterItemBlock getItemBlock() {
        return new PipeItemBlock(this);
    }

//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState exState = (IExtendedBlockState) state;
//        TileEntityPipe tile = getTilePipe(world, pos);
//        if (tile != null) {
//            exState = exState.withProperty(PIPE_CONNECTIONS, tile.getConnections());
//            exState = exState.withProperty(TEXTURE, getData());
//            if (tile.coverHandler.isPresent()) {
//                exState = exState.withProperty(COVER, tile.coverHandler.get().getAll());
//            }
//        }
//        return exState;
//    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getType().getTileType().create();
    }

    @Override
    public ToolType getHarvestTool(BlockState state) {
        return Data.WRENCH.getToolType();
    }

    // TODO: Block if covers are exist

    @Override // Used to set connection for sides where neighbor has pre-set connection
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntityPipe tile = getTilePipe(worldIn, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRECTIONS) {
                TileEntityPipe neighbor = getTilePipe(worldIn, pos.offset(side));
                if (neighbor != null) {
                    if (neighbor.canConnect(side.getOpposite().getIndex())) {
                        tile.setConnection(side);
                    }
                }
            }
        }
    }

    // Used to set connection between pipes on which block was placed
    public void onBlockPlacedTo(World world, BlockPos pos, Direction face) {
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            TileEntityPipe neighbor = getTilePipe(world, pos.offset(face.getOpposite()));
            if (neighbor != null) {
                tile.setConnection(face.getOpposite());
                if (!neighbor.canConnect(face.getIndex())) {
                     neighbor.setConnection(face);
                }
            }
        }
    }

    @Override // Used to clear connection for sides where neighbor was removed
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRECTIONS) {
                // Looking for the side where is a neighbor was
                if (pos.offset(side).equals(neighbor)) {
                    tile.clearConnection(side);
                    return;
                }
            }
        }
    }

    @Override // Used to catch new placed neighbors near pipe which enable connection
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos from, boolean isMoving) {
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRECTIONS) {
                // Looking for the side where is a neighbor changed
                if (pos.offset(side).equals(from)) {
                    if (tile.canConnect(side.getIndex())) {
                        tile.changeConnection(side);
                    }
                    return;
                }
            }
        }
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            LazyOptional<IInteractHandler> interaction = tile.getCapability(AntimatterCaps.INTERACTABLE);
            interaction.ifPresent(i -> i.onInteract(player, hand, hit.getFace(), Utils.getToolType(player)));
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.create(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
    }

    public int getPipeID(int config, int cull) {
        return ((size.ordinal() + 1) * 100) + ((getModelId() + 1) * 1000) + (cull == 0 ? 0 : 10000) + config;
    }

    @Nullable
    private static TileEntityPipe getTilePipe(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityPipe ? (TileEntityPipe) tile : null;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int ct = 0;
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (int s = 0; s < 6; s++) {
                if (tile.canConnect(s)) {
                    ct += 1 << s;
                }
            }
        }
        return config.set(new int[]{getPipeID(ct, 0)});
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        return state.getBlock() instanceof BlockPipe ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return stack.getItem() instanceof PipeItemBlock ? getRGB() : -1;
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(prov.existing("antimatter", "block/pipe/" + getSize().getId() + "/line_inv")).texture("all", getSide()).texture("overlay", getFace());
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        prov.getVariantBuilder(block).forAllStates(s -> ConfiguredModel.builder()
            .modelFile(getPipeConfig(prov.getBuilder(block)))
            .uvLock(true)
            .build()
        );
    }

    public String getModelLoc(String shape, int cull) {
        return "antimatter:block/pipe/" + getSize().getId() + "/" + shape + (cull == 0 ? "" : (shape.equals("base") ? "" : "_culled"));
    }

    public AntimatterBlockModelBuilder getPipeConfig(AntimatterBlockModelBuilder builder) {
        builder.model(getModelLoc("base", 0), of("all", getSide(), "overlay", getFace()));
        builder.staticConfigId("pipe");

        //Default Shape (0 Connections)
        builder.config(getPipeID(0, 0), getModelLoc("base", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //Single Shapes (1 Connections)
        builder.config(getPipeID(1, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(2, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(4, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(8, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 0));
        builder.config(getPipeID(16, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(32, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));

        //Line Shapes (2 Connections)
        builder.config(getPipeID(3, 0), getModelLoc("line", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(12, 0), getModelLoc("line", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(48, 0), getModelLoc("line", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));

        //Elbow Shapes (2 Connections)
        builder.config(getPipeID(5, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(6, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(9, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, -90));
        builder.config(getPipeID(10, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 90));
        builder.config(getPipeID(17, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 180, 0));
        builder.config(getPipeID(18, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 180, 0));
        builder.config(getPipeID(20, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(24, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -180, 0));
        builder.config(getPipeID(33, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(34, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(36, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(40, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));

        //Side Shapes (3 Connections)
        builder.config(getPipeID(7, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(11, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(13, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 180));
        builder.config(getPipeID(14, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(19, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 90));
        builder.config(getPipeID(28, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(35, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, -90));
        builder.config(getPipeID(44, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(49, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 180));
        builder.config(getPipeID(50, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(52, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, -90));
        builder.config(getPipeID(56, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 90));

        //Corner Shapes (3 Connections)
        builder.config(getPipeID(21, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 180));
        builder.config(getPipeID(22, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(25, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -270, 180));
        builder.config(getPipeID(26, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 0));
        builder.config(getPipeID(41, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -180, 180));
        builder.config(getPipeID(42, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));
        builder.config(getPipeID(37, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 180));
        builder.config(getPipeID(38, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //Arrow Shapes (4 Connections)
        builder.config(getPipeID(23, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(27, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -270, 90));
        builder.config(getPipeID(29, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 180));
        builder.config(getPipeID(30, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(39, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 90));
        builder.config(getPipeID(43, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -180, 90));
        builder.config(getPipeID(45, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 180));
        builder.config(getPipeID(46, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));
        builder.config(getPipeID(53, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(180, 180, 0));
        builder.config(getPipeID(54, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(57, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(180, 0, 0));
        builder.config(getPipeID(58, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 0));

        //Cross Shapes (4 Connections)
        builder.config(getPipeID(15, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(51, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(60, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //Five Shapes (5 Connections)
        builder.config(getPipeID(31, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(47, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(55, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(59, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(61, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(180, 0, 0));
        builder.config(getPipeID(62, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //All Shapes (6 Connections)
        builder.config(getPipeID(63, 0), getModelLoc("all", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        return builder.loader(AntimatterModelManager.LOADER_PIPE);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        super.getInfo(info, world, state, pos);
        info.add("Pipe Type: " + getType().getId());
        info.add("Pipe Material: " + getType().getMaterial().getId());
        info.add("Pipe Size: " + getSize().getId());
        return info;
    }
}
