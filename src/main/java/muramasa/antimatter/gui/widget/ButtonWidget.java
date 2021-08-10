package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.util.int4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

import static muramasa.antimatter.gui.widget.AntimatterWidget.builder;

public class ButtonWidget extends Button {
    private final ResourceLocation res;
    @Nullable
    private final ResourceLocation bodyLoc;
    @Nullable
    private final int4 resLoc;
    @Nullable
    private final ButtonBody body;
    @Nullable
    private final ButtonOverlay overlay;
    private final AntimatterContainerScreen<?> screen;
    private final IGuiHandler handler;
    private Function<ButtonWidget, Boolean> activeHandler;
    protected boolean pressed = false;

    protected ButtonWidget(AntimatterContainerScreen<?> screen, IGuiHandler handler, ResourceLocation res, @Nullable ButtonBody body, @Nullable ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        super(0,0,0,0, new StringTextComponent(""), but -> onPress.accept((ButtonWidget) but));
        this.res = res;
        this.body = body;
        this.overlay = overlay;
        this.handler = handler;
        this.screen = screen;
        this.resLoc = null;
        this.bodyLoc = null;
    }

    @Override
    public void onPress() {
        this.pressed = true;
        super.onPress();
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

    protected ButtonWidget(AntimatterContainerScreen<?> screen, IGuiHandler handler, ResourceLocation res, @Nullable ResourceLocation bodyLoc, @Nullable int4 loc, @Nullable ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        super(0,0,0,0, new StringTextComponent(""), but -> onPress.accept((ButtonWidget) but));
        this.res = res;
        this.body = null;
        this.overlay = overlay;
        this.handler = handler;
        this.screen = screen;
        this.resLoc = loc;
        this.bodyLoc = bodyLoc;
    }

    public static <T extends AntimatterContainer> WidgetSupplier<T> build(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder((screen, handler) -> new ButtonWidget(screen, handler, res, body, overlay, onPress));
    }

    public static <T extends AntimatterContainer> WidgetSupplier<T> build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder((screen, handler) -> new ButtonWidget(screen, handler, res, bodyLoc, loc, overlay, onPress));
    }

    public static <T extends AntimatterContainer> WidgetSupplier<T> build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, IGuiEvent ev, int id) {
        return builder((screen, handler) -> new ButtonWidget(screen, handler, res, bodyLoc, loc, overlay, but -> Antimatter.NETWORK.sendToServer(but.handler.createGuiPacket(ev, id))));
    }

    public static <T extends AntimatterContainer> WidgetSupplier<T> build(ResourceLocation res, ButtonBody body, ButtonOverlay overlay, IGuiEvent ev, int id) {
        return builder((screen, handler) -> new ButtonWidget(screen, handler, res, body, overlay, but -> Antimatter.NETWORK.sendToServer(but.handler.createGuiPacket(ev, id))));
    }

    public AntimatterContainerScreen<?> screen() {
        return screen;
    }

    public void renderWidget(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        //super.renderWidget(stack, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(res);
        RenderSystem.disableDepthTest();
        if (body != null) {
            int xTex = body.getX();
            int yTex = body.getY();
            if (isHovered()) {
                xTex += body.getX2();
                yTex += body.getY2();
            }
            ScreenWidget.blit(stack, x, y, width, height, xTex, yTex, body.getW(), body.getH(), 256, 256);
        }
        boolean isActive = activeHandler == null || activeHandler.apply(this);
        float color = isActive ? 1.0f : pressed ? 0.75f : 0.5f;
        if (color < 1f) {
            RenderSystem.color4f(color,color,color,1);
        }
        if (overlay != null) {
            ScreenWidget.blit(stack, screen().getGuiLeft() + x, screen.getGuiTop() + y, width, height, overlay.getX(), overlay.getY(), overlay.getW(), overlay.getH(), 256, 256);
        } else if (this.bodyLoc != null) {
            minecraft.getTextureManager().bindTexture(this.bodyLoc);
            ScreenWidget.blit(stack, screen().getGuiLeft() + x, screen.getGuiTop() + y, width, height, resLoc.x, resLoc.y, resLoc.z, resLoc.w, 256, 256);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1,1,1,1);
        String text = getMessage().getString();
        if (!text.isEmpty()) drawCenteredString(stack, minecraft.fontRenderer, text, x + width / 2, y + (height - 8) / 2, getFGColor() | MathHelper.ceil(alpha * 255.0F) << 24);
    }
}