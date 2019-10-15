package muramasa.gtu.client.render;

import net.minecraft.client.renderer.model.BakedQuad;

//Credit: http://www.minecraftforge.net/forum/topic/66893-112-is-there-a-way-to-color-bakedquads/?do=findComment&comment=321762
public class BakedQuadTinted extends BakedQuad {

    private int[] vertexData;

    public BakedQuadTinted(BakedQuad quad, int rgb) {
        super(quad.getVertexData(), quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.vertexData = quad.getVertexData();
        for (int i = 0; i < 4; i++) {
            vertexData[(format.getColorOffset() / 4) + format.getIntegerSize() * i] = RenderHelper.rgbToABGR(rgb);
        }
    }

    @Override
    public int[] getVertexData() {
        return vertexData;
    }
}
