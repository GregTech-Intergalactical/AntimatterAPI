package muramasa.itech.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

public class RenderHelper {

    private static DoubleBuffer glBuf = ByteBuffer.allocateDirect(128).order(ByteOrder.nativeOrder()).asDoubleBuffer();

    public static Matrix4f[] sideToMatrixRotation = new Matrix4f[] {
        new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
        new Matrix4f(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f),
        new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
        new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
        new Matrix4f(0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
        new Matrix4f(0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
    };

    public static void applyGLRotationForSide(EnumFacing side) {
        Matrix4f mat = sideToMatrixRotation[side.getOpposite().getIndex()];
        glBuf.put(mat.m00).put(mat.m01).put(mat.m02).put(mat.m03).put(mat.m10).put(mat.m11).put(mat.m12).put(mat.m13).put(mat.m20).put(mat.m21).put(mat.m22).put(mat.m23).put(mat.m30).put(mat.m31).put(mat.m32).put(mat.m33);
        glBuf.flip();
        GL11.glMultMatrix(glBuf);
    }

    public static TextureAtlasSprite getSpriteForFluid(Fluid fluid) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
    }

    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(loc.toString());
    }
}
