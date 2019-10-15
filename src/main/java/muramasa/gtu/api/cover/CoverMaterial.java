package muramasa.gtu.api.cover;

import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.QuadLayer;
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
