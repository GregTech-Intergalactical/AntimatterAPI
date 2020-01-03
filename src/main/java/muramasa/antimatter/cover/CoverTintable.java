package muramasa.antimatter.cover;

import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.QuadLayer;
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
