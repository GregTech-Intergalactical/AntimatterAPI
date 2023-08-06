package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.Widget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotWidget extends Widget {
    final SlotData<?> slot;
    protected SlotWidget(@NotNull GuiInstance gui, @Nullable IGuiElement parent, SlotData<?> slot) {
        super(gui, parent);
        this.setX(slot.getX() - 1);
        this.setY(slot.getY() - 1);
        this.setH(18);
        this.setW(18);
        this.setDepth(-1);
        this.slot = slot;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public static WidgetSupplier build(SlotData<?> slot) {
        return builder((a, b) -> new SlotWidget(a, b, slot));
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        drawTexture(matrixStack, slot.getTexture(), realX(), realY(), 0, 0, 18, 18, 18, 18);
    }
}
