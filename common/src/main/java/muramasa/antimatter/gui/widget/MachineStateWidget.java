package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int4;
import net.minecraft.network.chat.TextComponent;

public class MachineStateWidget extends Widget {
    /* Location in most machine textures. */
    protected final int4 state = new int4(176, 56, 8, 8);
    protected int2 location;
    /* If the container contains recipe flag. */
    protected final boolean isRecipe;
    protected final Tier tier;
    /* Synced machine state. */
    //protected MachineState machineState = MachineState.IDLE;

    protected MachineStateWidget(GuiInstance gui, IGuiElement parent) {
        super(gui, parent);
        this.tier = ((TileEntityMachine<?>) gui.handler).getMachineTier();
        this.setX(gui.handler.getGui().getMachineData().getMachineStatePos().x);
        this.setY(gui.handler.getGui().getMachineData().getMachineStatePos().y);
        this.setW(gui.handler.getGui().getMachineData().getMachineStateSize().x);
        this.setH(gui.handler.getGui().getMachineData().getMachineStateSize().y);
        this.isRecipe = ((TileEntityMachine<?>) gui.handler).has(MachineFlag.RECIPE);
    }

    @Override
    public void init() {
        super.init();
        //gui.syncInt(() -> ((ContainerMachine<?>)gui.container).getTile().getMachineState().ordinal(), v -> this.machineState = MachineState.values()[v]);
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        //Draw error.
        //No need to sync machine state.
        MachineState machineState = ((TileEntityMachine<?>) gui.handler).getMachineState();
        if (isRecipe) {
            if (machineState == MachineState.POWER_LOSS) {
                drawTexture(matrixStack, this.gui.handler.getGui().getMachineData().getMachineStateTexture(tier), realX(), realY(), getW(), 0, getW(), getH(), getW() * 2, getH());
            } else {
                drawTexture(matrixStack, this.gui.handler.getGui().getMachineData().getMachineStateTexture(tier), realX(), realY(), 0, 0, getW(), getH(), getW() * 2, getH());
            }
        }
    }

    @Override
    public void mouseOver(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        MachineState machineState = ((TileEntityMachine<?>) gui.handler).getMachineState();
        if (isRecipe) {
            renderTooltip(stack, new TextComponent(machineState.getDisplayName()), mouseX, mouseY);
        }
    }

    public static WidgetSupplier build() {
        return builder(MachineStateWidget::new);
    }
}
