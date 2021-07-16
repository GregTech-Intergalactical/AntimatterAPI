package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;

public class FluidSlotWidget<T extends ContainerMachine<?>> extends AntimatterWidget<T> {
    public FluidSlotWidget(AntimatterContainerScreen<? extends T> screen) {
        super(screen);
    }

    public static <T extends ContainerMachine<?>> WidgetSupplier.WidgetProvider<T> build() {
        return FluidSlotWidget::new;
    }

    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
    }
}
