package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.util.int4;
import net.minecraft.util.text.StringTextComponent;

public class MachineStateWidget<T extends ContainerMachine<?>> extends AntimatterWidget<T> {
    protected final int4 state = new int4(84, 45, 8, 8);

    protected MachineStateWidget(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler) {
        super(screen, handler);
        this.uv = new int4(84, 45, 8, 8);
    }

    @Override
    public void renderWidget(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        ContainerMachine<?> c = this.screen().getContainer();
        MachineState state = c.getTile().getMachineState();
        //Draw error.
        if (c.getTile().has(MachineFlag.RECIPE)) {
            if (state == MachineState.POWER_LOSS) {
                drawTexture(stack, this.screen().sourceGui(), screen().getGuiLeft() + this.x, screen().getGuiTop() + this.y, this.state.x, this.state.y, this.state.z, this.state.w);
            }
        }
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderToolTip(matrixStack, mouseX, mouseY);
        if (container().getTile().getMachineType().has(MachineFlag.RECIPE))
            screen().renderTooltip(matrixStack, new StringTextComponent(container().getTile().getMachineState().getDisplayName()), mouseX, mouseY);
    }

    public static <T extends ContainerMachine<?>> WidgetSupplier build() {
        return null;//builder(MachineStateWidget::new);
    }
}
