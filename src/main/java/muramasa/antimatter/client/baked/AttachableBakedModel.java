package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An attachable baked model is a model that can, depending on context, render
 * different quads for different side.
 */

//For now, use only general quads.
public class AttachableBakedModel extends DynamicBakedModel {
    public AttachableBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public final List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        //if (side != null) return super.getBlockQuads(state, side, rand, data);
       // if (side == null) return super.getBlockQuads(state, null,rand,data);
        if (side == null) return Collections.emptyList();
        List<BakedQuad> sideQuads = attachQuadsForSide(state,side,rand,data);
        if (sideQuads.size() == 0) return super.getBlockQuads(state,side,rand,data);
        return sideQuads;
    }

    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return Collections.emptyList();
    }
}
