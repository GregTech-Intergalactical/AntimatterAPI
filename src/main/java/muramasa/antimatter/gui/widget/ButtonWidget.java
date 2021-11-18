package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.*;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.util.int4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonWidget extends Widget {
    protected final ResourceLocation res;
    @Nullable
    protected final ResourceLocation bodyLoc;
    @Nullable
    protected final int4 resLoc;
    @Nullable
    protected final ButtonBody body;
    @Nullable
    protected final ButtonOverlay overlay;
    @Nullable
    protected String message;
    protected Function<ButtonWidget, Boolean> activeHandler;
    protected Consumer<ButtonWidget> onPress;
    protected boolean pressed = false;

    public ButtonWidget(GuiInstance instance, IGuiElement parent, ResourceLocation res, @Nullable ButtonBody body, @Nullable ButtonOverlay overlay, @Nullable Consumer<ButtonWidget> onPress) {
        super(instance, parent);
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
        if (this.onPress != null) {
            if (this.gui.handler.isRemote()) {
                clientClick();
            }
            this.onPress.accept(this);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void clientClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(res);
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
            RenderSystem.color4f(color, color, color, 1);
        }
        if (overlay != null) {
            ScreenWidget.blit(matrixStack, realX(), realY(), getW(), getH(), overlay.getX(), overlay.getY(), overlay.getW(), overlay.getH(), 256, 256);
        } else if (this.bodyLoc != null) {
            minecraft.getTextureManager().bind(this.bodyLoc);
            ScreenWidget.blit(matrixStack, realX(), realY(), getW(), getH(), resLoc.x, resLoc.y, resLoc.z, resLoc.w, 256, 256);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1, 1, 1, 1);
        ITextComponent message = getMessage();
        if (!message.getString().isEmpty()) {
            AbstractGui.drawCenteredString(matrixStack, minecraft.font, message, realX() + getW() / 2, realY() + (getH() - 8) / 2, 16777215);
        }
    }

    public ButtonWidget(GuiInstance instance, IGuiElement parent, ResourceLocation res, @Nullable ResourceLocation bodyLoc, @Nullable int4 loc, @Nullable ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        super(instance, parent);
        this.res = res;
        this.body = null;
        this.overlay = overlay;
        this.resLoc = loc;
        this.bodyLoc = bodyLoc;
        this.onPress = onPress;
    }

    public static WidgetSupplier build(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder((a, b) -> new ButtonWidget(a, b, res, body, overlay, onPress)).clientSide();
    }

    public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder((a, b) -> new ButtonWidget(a, b, res, bodyLoc, loc, overlay, onPress)).clientSide();
    }

    public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, IGuiEvent.IGuiEventFactory ev, int id) {
        return builder((a, b) -> new ButtonWidget(a, b, res, bodyLoc, loc, overlay, but -> Antimatter.NETWORK.sendToServer(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))))).clientSide();
    }

    public static WidgetSupplier build(String res, ButtonBody body, ButtonOverlay overlay, IGuiEvent.IGuiEventFactory ev, int id) {
        return builder(((a, b) -> new ButtonWidget(a, b, new ResourceLocation(a.handler.handlerDomain(), res), body, overlay, but -> Antimatter.NETWORK.sendToServer(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id)))))).clientSide();
    }

}