package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonWidget extends Widget {
    @NotNull
    protected final ButtonOverlay body;
    @Nullable
    protected String message;
    @Nullable
    protected String tooltipKey;
    protected Function<ButtonWidget, Boolean> activeHandler;
    protected Consumer<ButtonWidget> onPress;
    protected boolean pressed = false;

    public ButtonWidget(GuiInstance instance, IGuiElement parent, @NotNull ButtonOverlay body, @Nullable Consumer<ButtonWidget> onPress) {
        super(instance, parent);
        this.body = body;
        this.onPress = onPress;
    }

    protected void setClick(Consumer<ButtonWidget> clicker) {
        this.onPress = clicker;
    }

    public ButtonWidget setStateHandler(Function<ButtonWidget, Boolean> func) {
        this.activeHandler = func;
        return this;
    }

    public ButtonWidget setTooltipKey(@Nullable String tooltipKey) {
        this.tooltipKey = tooltipKey;
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
            renderTooltip(stack,new TranslatableComponent(getTooltipKey()), mouseX, mouseY);
        }
    }

    protected String getTooltipKey(){
        return tooltipKey;
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture(0, getBody().getTexture());
        int xTex = 0;
        int yTex = 0;
        if (getBody().isChangedOnHovered() && isInside(mouseX, mouseY)) {
            yTex += getBody().getH();
        }

        ScreenWidget.blit(matrixStack, realX(), realY(), this.getW(), this.getH(), xTex, yTex, getBody().getW(), getBody().getH(), getBody().getW(), getBody().getH() * (getBody().isChangedOnHovered() ? 2 : 1));
        /*boolean isActive = activeHandler == null || activeHandler.apply(this);
        float color = isActive ? 1.0f : pressed ? 0.75f : 0.5f;
        if (color < 1f) {
            RenderSystem.setShaderColor(color, color, color, 1);
        }*/
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Component message = getMessage();
        if (!message.getString().isEmpty()) {
            GuiComponent.drawCenteredString(matrixStack, minecraft.font, message, realX() + getW() / 2, realY() + (getH() - 8) / 2, 16777215);
        }
    }

    protected ButtonOverlay getBody(){
        return body;
    }

    /*public ButtonWidget(GuiInstance instance, IGuiElement parent, ResourceLocation res, @Nullable ResourceLocation bodyLoc, @Nullable int4 loc, @Nullable ButtonOverlay overlayOn, @Nullable ButtonOverlay overlayOff, Consumer<ButtonWidget> onPress) {
        super(instance, parent);
        this.res = res;
        this.body = null;
        this.overlayOn = overlayOn;
        this.overlayOff = overlayOff;
        this.resLoc = loc;
        this.bodyLoc = bodyLoc;
        this.onPress = onPress;
    }*/

    /*public static WidgetSupplier build(ButtonBody body, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder((a, b) -> new ButtonWidget(a, b, res, body, overlay, null, onPress)).clientSide();
    }

    public static WidgetSupplier build(ButtonBody body, ButtonOverlay overlayOn, ButtonOverlay overlayOff, Consumer<ButtonWidget> onPress) {
        return builder((a, b) -> new ButtonWidget(a, b, res, body, overlayOn, overlayOff, onPress)).clientSide();
    }*/

    /*public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, Consumer<ButtonWidget> onPress) {
        return builder((a, b) -> new ButtonWidget(a, b, res, bodyLoc, loc, overlay, null, onPress)).clientSide();
    }*/

    /*public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlay, IGuiEvent.IGuiEventFactory ev, int id) {
        return builder((a, b) -> new ButtonWidget(a, b, res, bodyLoc, loc, overlay, null, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))))).clientSide();
    }*/

    /*public static WidgetSupplier build(ResourceLocation res, ResourceLocation bodyLoc, int4 loc, ButtonOverlay overlayOn, ButtonOverlay overlayOff, IGuiEvent.IGuiEventFactory ev, int id) {
        return builder((a, b) -> new ButtonWidget(a, b, res, bodyLoc, loc, overlayOn, overlayOff, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))))).clientSide();
    }*/

    public static WidgetSupplier build(ButtonOverlay body, IGuiEvent.IGuiEventFactory ev, int id) {
        return builder(((a, b) -> new ButtonWidget(a, b,  body, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id)))))).clientSide();
    }

    public static WidgetSupplier build(ButtonOverlay body, IGuiEvent.IGuiEventFactory ev, int id, String tooltipKey) {
        return builder(((a, b) -> new ButtonWidget(a, b, body, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id)))).setTooltipKey(tooltipKey))).clientSide();
    }
}