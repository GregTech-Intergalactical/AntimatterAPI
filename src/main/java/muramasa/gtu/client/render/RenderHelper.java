package muramasa.gtu.client.render;

import muramasa.gtu.api.data.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
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

    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(loc.toString());
        return sprite != null ? sprite : Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(Textures.ERROR.toString());
    }

    public static TextureAtlasSprite getSprite(Fluid fluid) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
    }

    public static void drawFluid(Minecraft mc, int posX, int posY, int width, int height, int scaledAmount, FluidStack stack) {
        if (stack == null) return;
        Fluid fluid = stack.getFluid();
        if (fluid == null) return;

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(mc, fluid);
        int fluidColor = fluid.getColor(stack);

        //Draw the fluid texture
        drawTiledSprite(mc, posX, posY, width, height, 16, 16, fluidColor, scaledAmount, fluidStillSprite);

        //Render the amount String
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 200);
        if (stack.amount >= 2000) {
            String amount = stack.amount / 1000 + "";
            mc.fontRenderer.drawStringWithShadow(amount, posX + (16 - mc.fontRenderer.getStringWidth(amount) + 1), posY + 9, 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

    public static void drawTiledSprite(Minecraft mc, int posX, int posY, int tiledWidth, int tiledHeight, int texWidth, int texHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        setGLColorFromInt(color);

        int xTileCount = tiledWidth / texWidth;
        int xRemainder = tiledWidth - (xTileCount * texWidth);
        int yTileCount = scaledAmount / texHeight;
        int yRemainder = scaledAmount - (yTileCount * texHeight);

        final int yStart = posY + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : texWidth;
                int height = (yTile == yTileCount) ? yRemainder : texHeight;
                int x = posX + (xTile * texWidth);
                int y = yStart - ((yTile + 1) * texHeight);
                if (width > 0 && height > 0) {
                    int maskTop = texHeight - height;
                    int maskRight = texWidth - width;

                    drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    public static TextureAtlasSprite getStillFluidSprite(Minecraft minecraft, Fluid fluid) {
        TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
        ResourceLocation fluidStill = fluid.getStill();
        TextureAtlasSprite fluidStillSprite = null;
        if (fluidStill != null) fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
        if (fluidStillSprite == null) fluidStillSprite = textureMapBlocks.getMissingSprite();
        return fluidStillSprite;
    }

    public static void drawTextureWithMasking(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
        double uMin = (double) textureSprite.getMinU();
        double uMax = (double) textureSprite.getMaxU();
        double vMin = (double) textureSprite.getMinV();
        double vMax = (double) textureSprite.getMaxV();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        bufferBuilder.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }

    public static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static int rgbToABGR(int rgb) {
        rgb |= 0xFF000000;
        int r = (rgb >> 16) & 0xFF;
        int b = rgb & 0xFF;
        return (rgb & 0xFF00FF00) | (b << 16) | r;
    }
}
