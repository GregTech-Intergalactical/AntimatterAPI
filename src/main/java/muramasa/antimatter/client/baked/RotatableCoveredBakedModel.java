package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Tuple;

import java.util.List;

public class RotatableCoveredBakedModel extends CoveredBakedModel {

    public RotatableCoveredBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public List<BakedQuad> getCoverQuads(BlockState state, CoverInstance<?> instance, int dir) {
        return MODEL_CACHE.get(instance.getCover())[Utils.rotateFacing(Ref.DIRS[dir], state.get(BlockStateProperties.HORIZONTAL_FACING)).getIndex()];
    }
}
