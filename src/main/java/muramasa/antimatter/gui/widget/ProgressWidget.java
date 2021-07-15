package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.util.int4;

public class ProgressWidget<T extends ContainerMachine<?>> extends AntimatterWidget<T> {
    public final BarDir direction;
    public final int4 progressLocation;
    public final boolean barFill = true;

    public ProgressWidget(int4 loc, BarDir dir, AntimatterContainerScreen<? extends T> screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height);
        this.direction = dir;
        this.progressLocation = loc;
    }

    public static <T extends ContainerMachine<?>> GuiData.WidgetProvider<T> build(BarDir dir, int x, int y, int width, int height) {
        return screen -> new ProgressWidget<>(new int4(78, 24, 20, 18), dir, screen, x, y, width, height);
    }

    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (container().getTile().recipeHandler.map(MachineRecipeHandler::getClientProgressRaw).orElse(0) <= 0){
            return;
        }
        ContainerMachine<?> container = container();
        int progressTime;
        int x = this.x, y = this.y, xLocation = progressLocation.x, yLocation = progressLocation.y, length = progressLocation.z, width = progressLocation.w;
        switch (direction){
            case TOP:
                progressTime = (int) (progressLocation.w * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (!barFill) {
                    progressTime = width - progressTime;
                }
                y = (y + width) - progressTime;
                yLocation = (yLocation + width) - progressTime;
                width = progressTime;
                break;
            case LEFT:
                progressTime = (int) (progressLocation.z * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (barFill){
                    length = progressTime;
                } else {
                    length = length - progressTime;
                }
                break;
            case BOTTOM:
                progressTime = (int) (progressLocation.w * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (barFill){
                    width = progressTime;
                } else {
                    width = width - progressTime;
                }
                break;
            default:
                progressTime = (int) (progressLocation.z * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (!barFill) {
                    progressTime = length - progressTime;
                }
                x = (x + length) - progressTime;
                xLocation = (xLocation + length) - progressTime;
                length = progressTime;
                break;
        }
        drawTexture(matrixStack, screen().sourceGui(), screen().getGuiLeft() + x, screen().getGuiTop() + y, xLocation, yLocation, length, width);
    }
}
