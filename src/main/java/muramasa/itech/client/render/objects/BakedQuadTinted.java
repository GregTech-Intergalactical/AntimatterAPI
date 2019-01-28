package muramasa.itech.client.render.objects;

import net.minecraft.client.renderer.block.model.BakedQuad;

public class BakedQuadTinted extends BakedQuad {

    private int[] tintedVertexData;

    public BakedQuadTinted(BakedQuad quad, int rgb) {
        super(quad.getVertexData(), quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        System.out.println("TINT INDEX: " + quad.getTintIndex());
        tintedVertexData = quad.getVertexData();
        for (int i = 0; i < 4; i++) {
            tintedVertexData[(format.getColorOffset() / 4) + format.getIntegerSize() * i] = rgb;
        }
    }

    @Override
    public int[] getVertexData() {
        return tintedVertexData;
    }
}
