package muramasa.gregtech.api.cover.impl;

import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public abstract class CoverMaterial extends CoverTintable {

    abstract Prefix getPrefix();

    abstract Material getMaterial();

    @Override
    public List<BakedQuad> onRender(List<BakedQuad> quads, int side) {
        return BakedBase.tex(super.onRender(quads, side), TINTED_COVER_LAYER, getMaterial().getSet().getBlockTexture(getPrefix()));
    }

    @Override
    public int getRGB() {
        return getMaterial().getRGB();
    }
}
