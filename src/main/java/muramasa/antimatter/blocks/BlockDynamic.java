package muramasa.antimatter.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public abstract class BlockDynamic extends Block implements IBlockDynamicConfig, IAntimatterObject, ITextureProvider, IModelProvider {

    protected Int2ObjectOpenHashMap<Texture[]> configLookup = new Int2ObjectOpenHashMap<>();
    protected Texture[] defaultTextures;

    public BlockDynamic(Block.Properties properties, Texture... defaultTextures) {
        super(properties);
        this.defaultTextures = defaultTextures;
    }

    public Int2ObjectOpenHashMap<Texture[]> getConfigLookup() {
        return configLookup;
    }

    @Override
    public Texture[] getTextures() {
        return defaultTextures;
    }

    public Texture[] getConfigTextures() {
        return new Texture[0];
    }

    public void add(int config, Texture... textures) {
        configLookup.put(config, textures);
    }

    public void onConfigBuild() {
        //NOOP
    }

    /** Connection Logic **/
    public int[] getConfig(BlockState state, IBlockReader world, BlockPos.MutableBlockPos mut, BlockPos pos) {
        int[] ct = new int[1];
        for (int s = 0; s < 6; s++) {
            if (canConnect(world, mut.setPos(pos.offset(Ref.DIRECTIONS[s])))) ct[0] += 1 << s;
        }
        return ct;
    }

    public boolean canConnect(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == this;
    }
}
