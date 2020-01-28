package muramasa.antimatter.blocks;

import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public abstract class BlockDynamic extends Block implements IAntimatterObject, ITextureProvider, IModelProvider, IInfoProvider {

    protected Texture[] defaultTextures;
    protected int[] DEFAULT_CONFIG = new int[]{-1};

    public BlockDynamic(Block.Properties properties, Texture... defaultTextures) {
        super(properties);
        this.defaultTextures = defaultTextures;
    }

    @Override
    public Texture[] getTextures() {
        return defaultTextures;
    }

    /** Connection Logic **/
    public int[] getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int[] ct = new int[1];
        BlockState adjState;
        for (int s = 0; s < 6; s++) {
            adjState = world.getBlockState(mut.setPos(pos.offset(Ref.DIRECTIONS[s])));
            if (canConnect(world, adjState, mut)) ct[0] += 1 << s;
        }
        return ct[0] == 0 ? DEFAULT_CONFIG : ct;
    }

    public boolean canConnect(IBlockReader world, BlockState state, BlockPos pos) {
        return state.getBlock() == this;
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        info.add("Dynamic Config: " + Arrays.toString(getConfig(state, world, new BlockPos.Mutable(pos), pos)));
        return info;
    }
}
