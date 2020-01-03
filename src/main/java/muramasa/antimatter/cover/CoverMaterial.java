package muramasa.antimatter.cover;

import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.QuadLayer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;

import java.util.List;

public abstract class CoverMaterial extends CoverTintable {

    abstract MaterialType getType();

    abstract Material getMaterial();

    @Override
    public List<BakedQuad> onRender(IBakedModel baked, List<BakedQuad> quads, int side) {
        return ModelUtils.tex(super.onRender(baked, quads, side), QuadLayer.COVER_BASE, getMaterial().getSet().getTextures(getType())[0]);
    }

    @Override
    public int getRGB() {
        return getMaterial().getRGB();
    }
}
