package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
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
    //protected MachineState machineState = MachineState.IDLE;

    protected MachineStateWidget(GuiInstance gui, IGuiElement parent) {
        super(gui, parent);
        this.isRecipe = ((TileEntityMachine<?>)gui.handler).has(MachineFlag.RECIPE);
    }

    @Override
    public void init() {
        super.init();
        //gui.syncInt(() -> ((ContainerMachine<?>)gui.container).getTile().getMachineState().ordinal(), v -> this.machineState = MachineState.values()[v]);
    }

    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        //Draw error.
        //No need to sync machine state.
        MachineState machineState = ((TileEntityMachine<?>)gui.handler).getMachineState();
        if (isRecipe) {
            if (machineState == MachineState.POWER_LOSS) {
                drawTexture(matrixStack, this.gui.handler.getGuiTexture(), realX(), realY(), this.state.x, this.state.y, this.state.z, this.state.w);
            }
        }
    }

    @Override
    public void mouseOver(MatrixStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        MachineState machineState = ((TileEntityMachine<?>)gui.handler).getMachineState();
        if (isRecipe) {
            renderTooltip(stack, new StringTextComponent(machineState.getDisplayName()), mouseX, mouseY);
        }
    }

    public static WidgetSupplier build() {
        return builder(MachineStateWidget::new);
    }
}
