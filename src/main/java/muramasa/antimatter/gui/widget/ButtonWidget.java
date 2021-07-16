package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class ButtonWidget extends Button {
    private final ResourceLocation res;
    private final ButtonBody body;
    private final ButtonOverlay overlay;

    protected ButtonWidget(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, Button.IPressable onPress) {
        super(0,0,0,0, new StringTextComponent(""), onPress);
        this.res = res;
        this.body = body;
        this.overlay = overlay;
    }

    public static <T extends AntimatterContainer> WidgetSupplier.WidgetProvider<T> build(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, Button.IPressable onPress) {
        return screen -> new ButtonWidget(res, body, overlay, onPress);
    }

    public void renderWidget(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
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