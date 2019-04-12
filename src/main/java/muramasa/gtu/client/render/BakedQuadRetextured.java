package muramasa.gtu.client.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Arrays;

public class BakedQuadRetextured extends BakedQuad {

    private final TextureAtlasSprite texture;

    public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite texture) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.texture = texture;
        this.remapQuad();
    }

    protected void remapQuad() {
        try {
            if (texture == null || sprite == null) return;
            for (int i = 0; i < 4; ++i) {
                int j = format.getIntegerSize() * i;
                int uvIndex = format.getUvOffsetById(0) / 4;
                this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)this.sprite.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
                this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)this.sprite.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}
