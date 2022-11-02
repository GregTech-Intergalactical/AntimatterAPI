package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.cover.ICoverMode;
import muramasa.antimatter.cover.ICoverModeHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoverModeHandlerWidget  extends Widget {
    ICoverMode coverMode;
    public CoverModeHandlerWidget(@NotNull GuiInstance gui, @Nullable IGuiElement parent) {
        super(gui, parent);
    }

    @Override
    public void init() {
        super.init();
        if (gui.handler instanceof ICoverModeHandler coverModeHandler){
            gui.syncInt(coverModeHandler::coverModeToInt, i -> coverMode = coverModeHandler.getCoverMode(i), ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        }

    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        if (gui.handler instanceof ICoverModeHandler coverModeHandler && coverMode != null) {
            drawTexture(matrixStack, gui.handler.getGuiTexture(), realX() + coverMode.getX(), realY() + coverMode.getY(), coverModeHandler.getOverlayX(), coverModeHandler.getOverlayY(), 18, 18);
        }
    }

    public static WidgetSupplier build() {
        return builder(CoverModeHandlerWidget::new);
    }
}
