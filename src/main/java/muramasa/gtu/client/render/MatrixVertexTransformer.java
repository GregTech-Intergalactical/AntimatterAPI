package muramasa.gtu.client.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

//Credit: From AE2
public final class MatrixVertexTransformer extends QuadGatheringTransformer {

    private final Matrix4f transform;

    public MatrixVertexTransformer(Matrix4f transform) {
        this.transform = transform;
    }

    @Override
    protected void processQuad() {
        VertexFormat format = this.parent.getVertexFormat();
        int count = format.getElementCount();

        for (int v = 0; v < 4; v++) {
            for (int e = 0; e < count; e++) {
                VertexFormatElement element = format.getElement(e);
                if (element.getUsage() == VertexFormatElement.Usage.POSITION) {
                    this.parent.put(e, this.transform(this.quadData[e][v], element.getElementCount()));
                } else if (element.getUsage() == VertexFormatElement.Usage.NORMAL) {
                    this.parent.put(e, this.transformNormal(this.quadData[e][v]));
                } else {
                    this.parent.put(e, this.quadData[e][v]);
                }
            }
        }
    }

    @Override
    public void setQuadTint(int tint) {
        this.parent.setQuadTint(tint);
    }

    @Override
    public void setQuadOrientation(Direction orientation) {
        this.parent.setQuadOrientation(orientation);
    }

    @Override
    public void setApplyDiffuseLighting(boolean diffuse) {
        this.parent.setApplyDiffuseLighting(diffuse);
    }

    @Override
    public void setTexture(TextureAtlasSprite texture) {
        this.parent.setTexture(texture);
    }

    private float[] transform(float[] fs, int elemCount) {
        switch (fs.length) {
            case 3:
                Vector3f v3 = new Vector3f(fs[0], fs[1], fs[2]);
                v3.x -= 0.5f;
                v3.y -= 0.5f;
                v3.z -= 0.5f;
                this.transform.transform(v3);
                v3.x += 0.5f;
                v3.y += 0.5f;
                v3.z += 0.5f;
                return new float[] {v3.x, v3.y, v3.z};
            case 4:
                Vector4f v4 = new Vector4f(fs[0], fs[1], fs[2], fs[3]);
                if (elemCount == 3) v4.w = 1; // Otherwise all translation is lost
                v4.x -= 0.5f;
                v4.y -= 0.5f;
                v4.z -= 0.5f;
                this.transform.transform(v4);
                v4.x += 0.5f;
                v4.y += 0.5f;
                v4.z += 0.5f;
                return new float[] {v4.x, v4.y, v4.z, v4.w};
            default: return fs;
        }
    }

    private float[] transformNormal(float[] fs) {
        Vector4f normal;
        switch (fs.length) {
            case 3:
                normal = new Vector4f(fs[0], fs[1], fs[2], 0);
                this.transform.transform(normal);
                normal.normalize();
                return new float[] {normal.x, normal.y, normal.z};
            case 4:
                normal = new Vector4f(fs[0], fs[1], fs[2], fs[3]);
                this.transform.transform(normal);
                normal.normalize();
                return new float[] {normal.x, normal.y, normal.z, normal.w};
            default: return fs;
        }
    }
}
