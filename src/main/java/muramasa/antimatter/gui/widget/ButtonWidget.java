package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.util.int4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonWidget extends Widget {
    private final ResourceLocation res;
    @Nullable
    private final ResourceLocation bodyLoc;
    @Nullable
    private final int4 resLoc;
    @Nullable
    private final ButtonBody body;
    @Nullable
    private final ButtonOverlay overlay;
    @Nullable
    protected String message;
    private Function<ButtonWidget, Boolean> activeHandler;
    private Consumer<ButtonWidget> onPress;
    protected boolean pressed = false;

    protected ButtonWidget(GuiInstance instance, ResourceLocation res, @Nullable ButtonBody body, @Nullable ButtonOverlay overlay, @Nullable Consumer<ButtonWidget> onPress) {
        super(instance);
        this.res = res;
        this.body = body;
        this.overlay = overlay;
        this.resLoc = null;
        this.bodyLoc = null;
        this.onPress = onPress;
    }
    protected void setClick(Consumer<ButtonWidget> clicker) {
        this.onPress = clicker;
    }

    public ButtonWidget setStateHandler(Function<ButtonWidget, Boolean> func) {
        this.activeHandler = func;
        return this;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.pressed = false;
        super.onRelease(mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (button != 0) return;
        this.pressed = true;
        super.onClick(mouseX, mouseY, button);
        if (this.onPress != null) this.onPress.accept(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(res);
        RenderSystem.disableDepthTest();
        if (body != null) {
            int xTex = body.getX();
            int yTex = body.getY();
            if (isInside(mouseX, mouseY)) {
                xTex += body.getX2();
                yTex += body.getY2();
            }
            ScreenWidget.blit(matrixStack, realX(), realY(), this.getW(), this.getH(), xTex, yTex, body.getW(), body.getH(), 256, 256);
        }
        boolean isActive = activeHandler == null || activeHandler.apply(this);
        float color = isActive ? 1.0f : pressed ? 0.75f : 0.5f;
        if (color < 1f) {
            RenderSystem.color4f(color,color,color,1);
        }
        if (overlay != null) {
            ScreenWidget.blit(matrixStack, realX(), realY(), getW(), getH(), overlay.getX(), overlay.getY(), overlay.getW(), overlay.getH(), 256, 256);
        } else if (this.bodyLoc != null) {
            minecraft.getTextureManager().bindTexture(this.bodyLoc);
            ScreenWidget.blit(matrixStack, realX(), realY(), getW(), getH(), resLoc.x, resLoc.y, resLoc.z, resLoc.w, 256, 256);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1,1,1,1);
        ITextComponent message = getMessage();
        if (!message.getString().isEmpty()) {
            AbstractGui.drawCenteredString(matrixStack, minecraft.fontRenderer, message, realX() + getW() / 2, realY() + (getH() - 8) / 2, 16777215);
        }
    }

    protected ButtonWidget(GuiInstance instance, ResourceLocation res, @Nullable ResourceLocation bodyLoc, @Nullable int4 loc, @Nullable ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        super(instance);
        this.res = res;
        this.body = null;
        this.overlay = overlay;
        this.resLoc = loc;
        this.bodyLoc = bodyLoc;
        this.onPress = onPress;
    }

    public static WidgetSupplier build(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder(i -> new ButtonWidget(i, res, body, overlay, onPress));
    }

    public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder(i -> new ButtonWidget(i, res, bodyLoc, loc, overlay, onPress));
    }

    public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, IGuiEvent ev, int id) {
        return builder(i -> new ButtonWidget(i, res, bodyLoc, loc, overlay, but -> Antimatter.NETWORK.sendToServer(but.gui.handler.createGuiPacket(ev, id, Minecraft.getInstance().player.isCrouching() ? 1 : 0))));
    }

    public static WidgetSupplier build(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, IGuiEvent ev, int id) {
        return builder((i -> new ButtonWidget(i, res, body, overlay, but -> Antimatter.NETWORK.sendToServer(but.gui.handler.createGuiPacket(ev, id, Minecraft.getInstance().player.isCrouching() ? 1 : 0)))));
    }

}