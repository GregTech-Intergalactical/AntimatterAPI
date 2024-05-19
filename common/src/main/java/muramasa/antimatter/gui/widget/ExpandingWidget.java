package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.*;
import muramasa.antimatter.gui.event.GuiEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static muramasa.antimatter.gui.widget.ButtonWidget.*;

public class ExpandingWidget extends Widget {
    final Predicate<IGuiHandler> syncFunction;
    Consumer<ExpandingWidget> onExpand;
    final ButtonOverlay closed;
    boolean open = false;
    boolean pressed = false;
    int expansion = 50;

    protected ExpandingWidget(@NotNull GuiInstance gui, @Nullable IGuiElement parent, ButtonOverlay closed, Consumer<ExpandingWidget> onExpand, Predicate<IGuiHandler> syncFunction) {
        super(gui, parent);
        this.syncFunction = syncFunction;
        this.closed = closed;
        this.onExpand = onExpand;
    }

    @Override
    public void init() {
        super.init();
        this.gui.syncBoolean(() -> syncFunction.test(gui.handler), b -> {
            open = b;
        }, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        int extra = open ? 50 : 0;
        int x = realX() - extra;
        int y = realY();
        int width = getW() + extra;
        int height = getH() + extra;
        fillGradient(matrixStack, x, y, width, height, backgroundCenter, backgroundCenter);
        fillGradient(matrixStack, x + width, y - 1, 1, 1, backgroundCenter, backgroundCenter);
        fillGradient(matrixStack, x - 1, y + height, 1, 1, backgroundCenter, backgroundCenter);
        fillGradient(matrixStack, x - 1, y - 1, width + 1, 1, backgroundWhiteEdge, backgroundWhiteEdge);
        fillGradient(matrixStack, x - 1, y, 1, height, backgroundWhiteEdge, backgroundWhiteEdge);
        fillGradient(matrixStack, x, y + height, width + 1, 1, backgroundBlackEdge, backgroundBlackEdge);
        fillGradient(matrixStack, x + width, y, 1, height, backgroundBlackEdge, backgroundBlackEdge);
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
        if (this.onExpand != null) {
            if (this.gui.handler.isRemote()) {
                clientClick();
            }
            this.onExpand.accept(this);
        }
    }

    @Environment(EnvType.CLIENT)
    protected void clientClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public static WidgetSupplier build(ButtonOverlay closed, Predicate<IGuiHandler> syncFunction) {
        return builder((a, b) -> new ExpandingWidget(a, b, closed, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(GuiEvents.EXPANDING_WIDGET, Screen.hasShiftDown() ? 1 : 0, 0))), syncFunction));
    }
}
