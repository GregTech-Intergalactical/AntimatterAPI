package muramasa.antimatter.dynamic;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.block.IInfoProvider;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockDynamic extends BlockBasic implements IInfoProvider {

    public static final int[] DEFAULT_CONFIG = new int[]{0};

    protected ModelConfig config = new ModelConfig();
    protected Int2ObjectLinkedOpenHashMap<VoxelShape> shapes = new Int2ObjectLinkedOpenHashMap<>();
    protected Random r = new Random();

    public BlockDynamic(String domain, String id, Properties properties) {
        super(domain, id, properties);
    }

    public BlockDynamic(String domain, String id) {
        super(domain, id);
    }

    /**
     * Connection Logic
     **/
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int[] ct = new int[1];
        for (Direction side : Ref.DIRS) {
            mut.setPos(pos.offset(side));
            BlockState adjState = world.getBlockState(mut);
            TileEntity adjTile = world.getTileEntity(mut);
            if (canConnect(world, adjState, adjTile, mut)) {
                ct[0] += 1 << side.getIndex();
            }
        }
        return config.set(ct[0] == 0 ? DEFAULT_CONFIG : ct);
    }

    public boolean canConnect(IBlockReader world, BlockState state, @Nullable TileEntity tile, BlockPos pos) {
        return state.getBlock() == this;
    }

    public VoxelShape getShapeByModelIndex(BlockPos pos) {
//        if (config.hasModelIndex()) {
//            VoxelShape shape = shapes.get(config.getModelIndex());
//            if (shape != null) return shape;
//        }
//        return VoxelShapes.fullCube();

        //TODO this is awful, but I'm too sick to figure out why modelIndex is different here vs ModelConfigRandom.getQuads
        //TODO: I commented this because otherwise it wont load
        //r.setSeed(getPositionRandom(null, pos));
        int modelIndex = config.getModelIndex();
        int index = 0;
        if (shapes.size() > 0) {
            index = r.nextInt(shapes.size());
        }

        return shapes.containsKey(index) ? shapes.get(index) : VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return getShapeByModelIndex(pos);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        info.add("Config: " + Arrays.toString(getConfig(state, world, new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ()), pos).getConfig()));
        //info.add("Model Index: " + config.getModelIndex());
        //r.setSeed(getPositionRandom(null, pos));
        //info.add("Rand Index: " + (shapes.size() > 1 ? r.nextInt(shapes.size()) : -1));
        return info;
    }
}
