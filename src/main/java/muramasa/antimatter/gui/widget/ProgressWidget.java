package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int4;
import net.minecraft.util.text.StringTextComponent;



public class ProgressWidget extends Widget {
    public final BarDir direction;
    public final boolean barFill;
    private final int4 uv;
    private int progress = 0;
    private int maxProgress = 0;

    public ProgressWidget(GuiInstance instance, IGuiElement parent, int4 loc, BarDir dir, int x, int y, int width, int height, boolean barFill) {
        super(instance, parent);
        this.direction = dir;
        this.uv = loc;
        this.barFill = barFill;
        setX(x);
        setY(y);
        setW(width);
        setH(height);
    }

    @Override
    public void init() {
        super.init();
        gui.syncInt(() -> ((ContainerMachine<?>)gui.container).getTile().recipeHandler.map(MachineRecipeHandler::getCurrentProgress).orElse(0), i -> this.progress = i);
        gui.syncInt(() -> ((ContainerMachine<?>)gui.container).getTile().recipeHandler.map(rec -> rec.getActiveRecipe() == null ? 0 : rec.getActiveRecipe().getDuration()).orElse(0), i -> this.maxProgress = i);
    }

    public static WidgetSupplier build(BarDir dir, boolean barFill) {
        return builder((a,b) -> new ProgressWidget(a,b, dir.getUV(), dir, dir.getPos().x + 6, dir.getPos().y + 6, dir.getUV().z, dir.getUV().w, barFill));
    }

    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        int progressTime;
        int x = this.realX(), y = this.realY(), xLocation = uv.x, yLocation = uv.y, length = uv.z, width = uv.w;
        float progress = this.progress / (float) this.maxProgress;
        switch (direction){
            case TOP:
                progressTime = (int) (uv.w * progress);
                if (!barFill) {
                    progressTime = width - progressTime;
                }
                y = (y + width) - progressTime;
                yLocation = (yLocation + width) - progressTime;
                width = progressTime;
                break;
            case LEFT:
                progressTime = (int) (uv.z * progress);
                if (barFill){
                    length = progressTime;
                } else {
                    length = length - progressTime;
                }
                break;
            case BOTTOM:
                progressTime = (int) (uv.w * progress);
                if (barFill){
                    width = progressTime;
                } else {
                    width = width - progressTime;
                }
                break;
            default:
                progressTime = (int) (uv.z * progress);
                if (!barFill) {
                    progressTime = length - progressTime;
                }
                x = (x + length) - progressTime;
                xLocation = (xLocation + length) - progressTime;
                length = progressTime;
                break;
        }
        drawTexture(matrixStack, gui.handler.getGuiTexture(), realX(), realY(), xLocation, yLocation, length, width);
    }

    @Override
    public void mouseOver(MatrixStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        if (isInside(mouseX, mouseY)) {
            renderTooltip(stack, new StringTextComponent("Show Recipes"), mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        if (this.gui.handler instanceof TileEntityMachine) {
            AntimatterJEIPlugin.showCategory(((TileEntityMachine<?>)this.gui.handler).getMachineType());
        }
    }
}
