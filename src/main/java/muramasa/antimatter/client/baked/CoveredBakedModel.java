package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CoveredBakedModel extends DynamicBakedModel {

    protected static Int2ObjectMap<List<BakedQuad>> MODEL_CACHE = new Int2ObjectOpenHashMap<>();

    public CoveredBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (MODEL_CACHE.isEmpty()) buildCoverCache(state, rand, data);
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, data);
        //quads.addAll(MODEL_CACHE.get(getCoverCacheId(AntimatterAPI.get(Cover.class, "output"), Direction.SOUTH)));
        return quads;
    }

    public static int getCoverCacheId(Cover cover, Direction dir) {
        return dir.getIndex() * cover.getId().hashCode();
    }

    public static void buildCoverCache(BlockState state, Random rand, IModelData data) {
        List<BakedQuad> quads;
        for (Cover c : AntimatterAPI.all(Cover.class)) {
            quads = ModelUtils.getBaked(c.getModel()).getQuads(state, Direction.NORTH, rand, data);
            for (Direction dir : Ref.DIRS) {
                MODEL_CACHE.put(getCoverCacheId(c, dir), ModelUtils.trans(quads, dir.toVector3f(), new Vector3f(0, 0, 0)));
            }
        }
    }
}
