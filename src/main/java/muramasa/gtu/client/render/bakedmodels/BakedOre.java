package muramasa.gtu.client.render.bakedmodels;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.TextureSet;
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

import static muramasa.gtu.api.properties.GTProperties.*;

public class BakedOre extends BakedBase {

    public static IBakedModel[] STONES;
    public static Int2ObjectArrayMap<IBakedModel[]> OVERLAYS;
    public static ItemOverrideOre ITEM_OVERRIDE = new ItemOverrideOre();

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new LinkedList<>();
        IExtendedBlockState exState = (IExtendedBlockState) state;
        if (exState == null) return quads;

        int stone = exState.getValue(ORE_STONE);
        quads.addAll(STONES[stone].getQuads(state, side, rand));

        int material = exState.getValue(ORE_MATERIAL);
        int type = exState.getValue(ORE_TYPE);

        TextureSet set = Material.get(material).getSet();
        IBakedModel[] array = OVERLAYS.get(type);
        IBakedModel overlay = array[set.getInternalId()];

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
