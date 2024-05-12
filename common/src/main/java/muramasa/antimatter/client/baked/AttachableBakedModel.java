package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An attachable baked model is a model that can, depending on context, render
 * different quads for different side.
 */

//For now, use only general quads.
public class AttachableBakedModel extends DynamicBakedModel {
    public AttachableBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<BakedModel[]> bakedTuple) {
        super(particle, bakedTuple);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos) {
        try {
            if (onlyGeneralQuads && side != null) return Collections.emptyList();
            if (state == null) {
                return Collections.emptyList();
                //return getItemQuads(side, rand, data); //TODO item quads
            }
            if (side != null) return Collections.emptyList();
            List<BakedQuad> quads = new ObjectArrayList<>();
            quads.addAll(getBlockQuads(state, null, rand, level, pos));
            for (Direction dir : Ref.DIRS) {
                quads.addAll(getBlockQuads(state, dir, rand, level, pos));
            }
            return quads;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public final List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos) {
        //if (side != null) return super.getBlockQuads(state, side, rand, data);
        // if (side == null) return super.getBlockQuads(state, null,rand,data);
        //if (side == null) return super.getBlockQuads(state,null,rand,data);
        if (side == null) return Collections.emptyList();
        List<BakedQuad> sideQuads = attachQuadsForSide(state, side, rand, level, pos);
        if (sideQuads.size() == 0) return super.getBlockQuads(state, side, rand, level, pos);
        return sideQuads;
    }

    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos) {
        return Collections.emptyList();
    }
}
