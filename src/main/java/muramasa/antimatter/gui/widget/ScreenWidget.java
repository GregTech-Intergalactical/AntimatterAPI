package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;

public class ScreenWidget {

    /**
     * Param 1-4: Where to and how big to draw on the screen
     * Param 5-8: What part of the texture file to cut out and draw
     * Param 9-10: How big the entire texture file is in general (pow2 only)
     *
     * @param renderX Where to draw on the screen
     * @param renderY Where to draw on the screen
     * @param renderWidth How big to draw on the screen
     * @param renderHeight How big to draw on the screen
     * @param textureX The top-left x position of texture in the file
     * @param textureY The top-left y position of texture in the file
     * @param textureWidth The total texture width in the file
     * @param textureHeight The total texture height in the file
     * @param totalTextureFileWidth The total texture file size
     * @param totalTextureFileHeight The total texture file size
     */
    public static void blit(MatrixStack stack, int renderX, int renderY, int renderWidth, int renderHeight, int textureX, int textureY, int textureWidth, int textureHeight, int totalTextureFileWidth, int totalTextureFileHeight)
    {
        AbstractGui.blit(stack, renderX, renderY, renderWidth, renderHeight, textureX, textureY, textureWidth, textureHeight, totalTextureFileWidth, totalTextureFileHeight);
    }
}
