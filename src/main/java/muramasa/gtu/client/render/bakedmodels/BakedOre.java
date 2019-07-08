package muramasa.gtu.client.render.bakedmodels;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.client.render.overrides.ItemOverrideOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import static muramasa.gtu.api.properties.GTProperties.ORE_SET;
import static muramasa.gtu.api.properties.GTProperties.ORE_TYPE;

public class BakedOre extends BakedBase {

    public static Object2ObjectOpenHashMap<String, IBakedModel> STONES;
    public static IBakedModel[][] OVERLAYS;
    public static ItemOverrideOre ITEM_OVERRIDE = new ItemOverrideOre();

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        if (state == null) return quads;

        String stone = ((BlockOre) state.getBlock()).getStoneType().getId();
        quads.addAll(STONES.get(stone).getQuads(state, side, rand));

        int type = state.getValue(ORE_TYPE).ordinal();
        int set = ((IExtendedBlockState) state).getValue(ORE_SET);

        IBakedModel[] array = OVERLAYS[type];
        IBakedModel overlay = array[set];

        quads.addAll(overlay.getQuads(state, side, rand));

        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return super.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ITEM_OVERRIDE;
    }
}
