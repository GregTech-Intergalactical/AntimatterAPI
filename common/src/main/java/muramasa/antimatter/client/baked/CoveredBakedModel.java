package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CoveredBakedModel extends AttachableBakedModel {

    public CoveredBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<BakedModel[]> bakedTuple) {
        super(particle, bakedTuple);
    }

    @Override
    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @Nonnull Random rand,
                                                 @Nonnull BlockAndTintGetter level, BlockPos pos) {
        return Collections.emptyList();//attachCoverQuads(new ArrayList<>(), state, side, data);
    }
/*
    protected final List<BakedQuad> attachCoverQuads(List<BakedQuad> quads, BlockState state, Direction side,
                                                     @Nonnull IModelData data) {
        TileEntityBase<?> tile = data.getData(AntimatterProperties.TILE_PROPERTY);
        if (tile == null)
            return quads;
        CoverHandler<?> covers = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, side)
                .filter(t -> t instanceof CoverHandler).map(t -> (CoverHandler) t).orElse(null);
        if (covers == null)
            return quads;
        Texture tex = data.hasProperty(AntimatterProperties.MULTI_MACHINE_TEXTURE)
                ? data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE).apply(side)
                : data.getData(AntimatterProperties.MACHINE_TEXTURE).apply(side);
        ICover c = covers.get(side);
        if (c.isEmpty())
            return quads;
        quads = covers.getTexturer(side).getQuads("cover", quads, state, c,
                new BaseCover.DynamicKey(Utils.dirFromState(state), tex, c.getId()), side.get3DDataValue(),
                data);
        return quads;
    }*/
}
