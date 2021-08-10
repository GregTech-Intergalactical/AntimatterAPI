package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.util.int4;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class ProgressWidget<T extends ContainerMachine<?>> extends AntimatterWidget<T> {
    public final BarDir direction;
    public final boolean barFill;

    public ProgressWidget(AntimatterContainerScreen<? extends T> screen, IGuiHandler handler, int4 loc, BarDir dir, int x, int y, int width, int height, boolean barFill) {
        super(screen, handler, x, y, width, height);
        this.direction = dir;
        this.uv = loc;
        this.barFill = barFill;
    }

    public static <T extends ContainerMachine<?>> WidgetSupplier<T> build(BarDir dir, boolean barFill) {
        return builder((screen, handler) -> new ProgressWidget<>(screen, handler, dir.getUV(), dir, dir.getPos().x + 6, dir.getPos().y + 6, dir.getUV().z, dir.getUV().w, barFill));
    }

    @Override
    public void renderWidget(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (container().getTile().recipeHandler.map(MachineRecipeHandler::getClientProgressRaw).orElse(0) <= 0){
            return;
        }
        ContainerMachine<?> container = container();
        int progressTime;
        int x = this.x, y = this.y, xLocation = uv.x, yLocation = uv.y, length = uv.z, width = uv.w;
        switch (direction){
            case TOP:
                progressTime = (int) (uv.w * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (!barFill) {
                    progressTime = width - progressTime;
                }
                y = (y + width) - progressTime;
                yLocation = (yLocation + width) - progressTime;
                width = progressTime;
                break;
            case LEFT:
                progressTime = (int) (uv.z * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (barFill){
                    length = progressTime;
                } else {
                    length = length - progressTime;
                }
                break;
            case BOTTOM:
                progressTime = (int) (uv.w * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (barFill){
                    width = progressTime;
                } else {
                    width = width - progressTime;
                }
                break;
            default:
                progressTime = (int) (uv.z * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
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

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        AntimatterJEIPlugin.showCategory(container().getTile().getMachineType());
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderToolTip(matrixStack, mouseX, mouseY);
        screen().renderTooltip(matrixStack, new StringTextComponent("Show Recipes"), mouseX, mouseY);
    }
}
