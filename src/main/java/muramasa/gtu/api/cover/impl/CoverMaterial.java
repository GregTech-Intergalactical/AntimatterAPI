package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public abstract class CoverMaterial extends CoverTintable {

    abstract MaterialType getType();

    abstract Material getMaterial();

    @Override
    public List<BakedQuad> onRender(List<BakedQuad> quads, int side) {
        return ModelUtils.tex(super.onRender(quads, side), TINTED_COVER_LAYER, getMaterial().getSet().getBlockTexture(getType()));
    }

    @Override
    public int getRGB() {
        return getMaterial().getRGB();
    }
}
