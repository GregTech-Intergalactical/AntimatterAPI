package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int4;
import net.minecraft.util.text.StringTextComponent;

public class MachineStateWidget extends Widget {
    /* Location in most machine textures. */
    protected final int4 state = new int4(176,56, 8, 8);
    /* If the container contains recipe flag. */
    protected final boolean isRecipe;
    /* Synced machine state. */
    protected MachineState machineState = MachineState.IDLE;

    protected MachineStateWidget(GuiInstance gui) {
        super(gui);
        this.isRecipe = ((TileEntityMachine<?>)gui.handler).has(MachineFlag.RECIPE);
    }

    @Override
    public void init() {
        super.init();
        gui.syncInt(() -> ((ContainerMachine<?>)gui.container).getTile().getMachineState().ordinal(), v -> this.machineState = MachineState.values()[v]);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        //Draw error.
        if (isRecipe) {
            if (machineState == MachineState.POWER_LOSS) {
                drawTexture(matrixStack, this.gui.handler.getGuiTexture(), realX(), realY(), this.state.x, this.state.y, this.state.z, this.state.w);
            }
        }
        if (isRecipe && isInside(mouseX, mouseY)) {
           renderTooltip(matrixStack, new StringTextComponent(machineState.getDisplayName()), mouseX, mouseY);
        }
    }

    public static WidgetSupplier build() {
        return builder(MachineStateWidget::new);
    }
}
