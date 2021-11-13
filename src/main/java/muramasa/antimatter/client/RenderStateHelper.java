package muramasa.antimatter.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL13;

@OnlyIn(Dist.CLIENT)
public class RenderStateHelper {
    public static void setGlClearColorFromInt(int colorValue, int opacity) {
        int i = (colorValue & 16711680) >> 16;
        int j = (colorValue & 65280) >> 8;
        int k = (colorValue & 255);
        RenderSystem.clearColor(i / 255.0f, j / 255.0f, k / 255.0f, opacity / 255.0f);
    }

    public static void disableLightmap() {
        RenderSystem.activeTexture(GL13.GL_TEXTURE1);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
    }

    public static void enableLightmap() {
        RenderSystem.activeTexture(GL13.GL_TEXTURE1);
//        RenderSystem.matrixMode(5890);
//        RenderSystem.loadIdentity();
//        float f = 0.00390625F;
//        RenderSystem.scalef(0.00390625F, 0.00390625F, 0.00390625F);
//        RenderSystem.translatef(8.0F, 8.0F, 8.0F);
//        RenderSystem.matrixMode(5888);
////        Minecraft.getInstance().getTextureManager().bindTexture(this.locationLightMap);
//        RenderSystem.texParameter(3553, 10241, 9729);
//        RenderSystem.texParameter(3553, 10240, 9729);
//        RenderSystem.texParameter(3553, 10242, 10496);
//        RenderSystem.texParameter(3553, 10243, 10496);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
    }

}
