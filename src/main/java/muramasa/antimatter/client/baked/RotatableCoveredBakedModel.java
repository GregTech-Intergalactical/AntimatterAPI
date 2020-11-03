package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;

public class RotatableCoveredBakedModel extends CoveredBakedModel {

    public RotatableCoveredBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public List<BakedQuad> getCoverQuads(BlockState state, CoverInstance<?> instance, int dir, Texture baseTex, IModelData data){
        return super.getCoverQuads(state,instance,dir, baseTex, data);
        //return MODEL_CACHE.get(instance.getCover())[Utils.rotateFacing(Ref.DIRS[dir], state.get(BlockStateProperties.HORIZONTAL_FACING)).getIndex()];
    }
}
