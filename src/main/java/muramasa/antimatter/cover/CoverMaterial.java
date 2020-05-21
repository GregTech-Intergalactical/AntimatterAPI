package muramasa.antimatter.cover;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;

import java.util.List;

public abstract class CoverMaterial extends CoverTintable {

    public abstract MaterialType<?> getType();

    public abstract Material getMaterial();

    @Override
    public List<BakedQuad> onRender(IBakedModel baked, List<BakedQuad> quads, int side) {
        //return ModelUtils.tex(super.onRender(baked, quads, side), QuadLayer.COVER_BASE, getMaterial().getSet().getTextures(getType())[0]);
        return quads;
    }

    @Override
    public int getRGB() {
        return getMaterial().getRGB();
    }
}
