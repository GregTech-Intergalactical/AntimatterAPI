package muramasa.itech.client.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public class TintedBakedQuad extends BakedQuad {

    public TintedBakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, boolean applyDiffuseLighting, VertexFormat format) {
        super(vertexDataIn, tintIndexIn, faceIn, spriteIn, applyDiffuseLighting, format);
    }
}
