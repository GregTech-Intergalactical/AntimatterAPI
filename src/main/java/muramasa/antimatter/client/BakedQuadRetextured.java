package muramasa.antimatter.client;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class BakedQuadRetextured extends BakedQuad {
    private final TextureAtlasSprite texture;

    public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.texture = textureIn;
        this.remapQuad();
    }

    private void remapQuad() {
        for(int i = 0; i < 4; ++i) {
            int j = format.getIntegerSize() * i;
            int uvIndex = format.getUvOffsetById(0) / 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)this.sprite.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)this.sprite.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
        }
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}