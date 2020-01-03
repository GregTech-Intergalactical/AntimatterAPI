package muramasa.antimatter.cover;

import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.QuadLayer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;

import java.util.List;

public abstract class CoverTintable extends Cover {

    public abstract int getRGB();

    @Override
    public List<BakedQuad> onRender(IBakedModel baked, List<BakedQuad> quads, int side) {
        return ModelUtils.tint(quads, QuadLayer.COVER_BASE, getRGB());
    }
}
