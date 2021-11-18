package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoveredBakedModel extends AttachableBakedModel {

    public CoveredBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<IBakedModel[]> bakedTuple) {
        super(particle, bakedTuple);
    }

    @Override
    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @Nonnull Random rand,
                                                 @Nonnull IModelData data) {
        return attachCoverQuads(new ArrayList<>(), state, side, data);
    }

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
    }
}
