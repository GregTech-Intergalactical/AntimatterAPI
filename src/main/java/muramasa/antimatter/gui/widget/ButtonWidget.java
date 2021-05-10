package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class ButtonWidget extends Button {
    private ResourceLocation res;
    private ButtonBody body;
    private ButtonOverlay overlay;

    public ButtonWidget(ResourceLocation res, int x, int y, int w, int h, ButtonBody body, ButtonOverlay overlay, String text, Button.IPressable onPress) {
        super(x, y, w, h, new StringTextComponent(text), onPress);
        this.res = res;
        this.body = body;
        this.overlay = overlay;
    }

    public ButtonWidget(ResourceLocation res, int x, int y, int w, int h, ButtonBody body, String text, Button.IPressable onPress) {
        this(res, x, y, w, h, body, null, text, onPress);
    }

    public ButtonWidget(ResourceLocation res, int x, int y, int w, int h, ButtonBody body, ButtonOverlay overlay, Button.IPressable onPress) {
        this(res, x, y, w, h, body, overlay, "", onPress);
    }

    public ButtonWidget(ResourceLocation res, int x, int y, int w, int h, ButtonBody body, Button.IPressable onPress) {
        this(res, x, y, w, h, body, null, "", onPress);
    }

    public void renderWidget(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        //super.renderWidget(stack, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(res);
        RenderSystem.disableDepthTest();
        int xTex = body.getX();
        int yTex = body.getY();
        if (isHovered()) {
            xTex += body.getX2();
            yTex += body.getY2();
        }
        ScreenWidget.blit(stack, x, y, width, height, xTex, yTex, body.getW(), body.getH(), 256, 256);
        if (overlay != null) ScreenWidget.blit(stack, x, y, width, height, overlay.getX(), overlay.getY(), overlay.getW(), overlay.getH(), 256, 256);
        RenderSystem.enableDepthTest();
        String text = getMessage().getString();
        if (!text.isEmpty()) drawCenteredString(stack, minecraft.fontRenderer, text, x + width / 2, y + (height - 8) / 2, getFGColor() | MathHelper.ceil(alpha * 255.0F) << 24);
    }
}