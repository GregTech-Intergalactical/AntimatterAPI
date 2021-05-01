package muramasa.antimatter.client.baked;

import java.util.List;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

public class PipeBakedModel extends DynamicBakedModel {

    public static final Int2ObjectOpenHashMap<IBakedModel[]> CONFIGS = new Int2ObjectOpenHashMap<>();

    public PipeBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
        onlyGeneralQuads();
    }

    

    @Override
    public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData data) {
        data = super.getModelData(world, pos, state, data);
        data.setData(AntimatterProperties.PIPE_TILE, ((TileEntityPipe)world.getTileEntity(pos)));
        return data;
    }



    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, Random rand, IModelData data) {
        TileEntityPipe pipe = data.getData(AntimatterProperties.PIPE_TILE);
        PipeCoverHandler<?> covers = pipe.coverHandler.orElse(null);
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, data);
        if (covers == null) return quads;
        if (side == null) {
            for (Direction dir : Ref.DIRS) {
                Texture tex = ((BlockPipe<?>)state.getBlock()).getFace();
                CoverStack<?> c = covers.get(dir);
                if (c.isEmpty()) continue;
                quads = covers.getTexturer(side).getQuads(quads,state,c.getCover(),new BaseCover.DynamicKey(dir, tex, c.getCover().getId()), dir.getIndex(), data);
            }
        }
        return quads;
    }

}
