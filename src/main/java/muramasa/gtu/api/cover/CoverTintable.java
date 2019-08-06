package muramasa.gtu.api.cover;

import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;

import java.util.List;

public abstract class CoverTintable extends Cover {

    public static final int TINTED_COVER_LAYER = 5; //TODO move to Enum

    public abstract int getRGB();

    @Override
    public List<BakedQuad> onRender(IBakedModel baked, List<BakedQuad> quads, int side) {
        return ModelUtils.tint(quads, TINTED_COVER_LAYER, getRGB());
    }
}
