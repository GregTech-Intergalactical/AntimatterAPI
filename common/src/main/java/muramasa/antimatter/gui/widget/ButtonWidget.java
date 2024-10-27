package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonWidget extends Widget {
    static int backgroundCenter = 0xff8b8b8b;
    static int backgroundBlackEdge = 0xff373737;
    static int backgroundWhiteEdge = 0xffffffff;
    @NotNull
    protected final ButtonOverlay body;
    @Nullable
    protected String tooltipKey;
    protected Consumer<ButtonWidget> onPress;
    protected boolean pressed = false;
    protected boolean renderBackground = false;

    public ButtonWidget(GuiInstance instance, IGuiElement parent, @NotNull ButtonOverlay body, @Nullable Consumer<ButtonWidget> onPress) {
        super(instance, parent);
        this.body = body;
        this.onPress = onPress;
    }

    protected void setClick(Consumer<ButtonWidget> clicker) {
        this.onPress = clicker;
    }

    public ButtonWidget setTooltipKey(@Nullable String tooltipKey) {
        this.tooltipKey = tooltipKey;
        return this;
    }

    public ButtonWidget setRenderBackground(boolean renderBackground){
        this.renderBackground = renderBackground;
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

    @Environment(EnvType.CLIENT)
    protected void clientClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void mouseOver(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        if (getTooltipKey() != null){
            renderTooltip(stack, Utils.translatable(getTooltipKey()), mouseX, mouseY);
        }
    }

    protected String getTooltipKey(){
        return tooltipKey;
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        if (renderBackground){
            int x = realX();
            int y = realY();
            fillGradient(matrixStack, x, y, getW(), getH(), backgroundCenter, backgroundCenter);
            fillGradient(matrixStack, x + getW(), y - 1, 1, 1, backgroundCenter, backgroundCenter);
            fillGradient(matrixStack, x - 1, y + getH(), 1, 1, backgroundCenter, backgroundCenter);
            fillGradient(matrixStack, x - 1, y - 1, getW() + 1, 1, backgroundWhiteEdge, backgroundWhiteEdge);
            fillGradient(matrixStack, x - 1, y, 1, getH(), backgroundWhiteEdge, backgroundWhiteEdge);
            fillGradient(matrixStack, x, y + getH(), getW() + 1, 1, backgroundBlackEdge, backgroundBlackEdge);
            fillGradient(matrixStack, x + getW(), y, 1, getH(), backgroundBlackEdge, backgroundBlackEdge);
        }
        renderButtonBody(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderButtonBody(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks){
        int xTex = 0;
        int yTex = 0;
        if (getBody().isChangedOnHovered() && isInside(mouseX, mouseY)) {
            yTex += getBody().getH();
        }
        drawTexture(matrixStack, getBody().getTexture(), realX(), realY(), xTex, yTex, getBody().getW(), getBody().getH(), getBody().getW(), getBody().getH() * (getBody().isChangedOnHovered() ? 2 : 1));
    }

    protected ButtonOverlay getBody(){
        return body;
    }

    public static WidgetSupplier build(ButtonOverlay body, IGuiEvent.IGuiEventFactory ev, int id, boolean renderBackground) {
        return builder(((a, b) -> new ButtonWidget(a, b,  body, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id)))).setRenderBackground(renderBackground))).clientSide();
    }

    public static WidgetSupplier build(ButtonOverlay body, IGuiEvent.IGuiEventFactory ev, int id, boolean renderBackground, String tooltipKey) {
        return builder(((a, b) -> new ButtonWidget(a, b, body, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id)))).setRenderBackground(renderBackground).setTooltipKey(tooltipKey))).clientSide();
    }
}