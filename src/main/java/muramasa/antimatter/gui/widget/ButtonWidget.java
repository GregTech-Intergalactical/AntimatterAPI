package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class ButtonWidget extends Button {
    private ResourceLocation res;
    private ButtonBody body;
    private ButtonOverlay overlay;

    public ButtonWidget(int x, int y, int w, int h, ResourceLocation res, ButtonBody body, ButtonOverlay overlay, Button.IPressable onPress) {
        super(x, y, w, h, "", onPress);
        this.res = res;
        this.body = body;
        this.overlay = overlay;
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(res);
        RenderSystem.disableDepthTest();
        int xTex = body.getTexX();
        int yTex = body.getTexY();
        if (isHovered()) {
            xTex += body.getDiffX();
            yTex += body.getDiffY();
        }
        ScreenWidget.blit(x, y, width, height, xTex, yTex, 16, 16, 256, 256);
        ScreenWidget.blit(x, y, width, height, overlay.getTexX(), overlay.getTexY(), 16, 16, 256, 256);
        RenderSystem.enableDepthTest();
    }
}