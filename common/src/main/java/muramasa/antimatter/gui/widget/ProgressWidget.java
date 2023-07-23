package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int4;
import net.minecraft.network.chat.TranslatableComponent;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;


public class ProgressWidget extends Widget {
    public final BarDir direction;
    public final boolean barFill;
    private final int4 uv;
    private int progress = 0;
    private int maxProgress = 0;
    private float percent = 0.0F;

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
        gui.syncFloat(() -> ((ContainerMachine<?>) gui.container).getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F), i -> this.percent = i, SERVER_TO_CLIENT);
        gui.syncInt(() -> ((ContainerMachine<?>) gui.container).getTile().recipeHandler.map(MachineRecipeHandler::getCurrentProgress).orElse(0), i -> this.progress = i, SERVER_TO_CLIENT);
        gui.syncInt(() -> ((ContainerMachine<?>) gui.container).getTile().recipeHandler.map(rec -> rec.getActiveRecipe() == null ? 0 : rec.getActiveRecipe().getDuration()).orElse(0), i -> this.maxProgress = i, SERVER_TO_CLIENT);
    }

    public static WidgetSupplier build(BarDir dir, boolean barFill) {
        return builder((a, b) -> new ProgressWidget(a, b, dir.getUV(), dir, dir.getPos().x + 6, dir.getPos().y + 6, dir.getUV().z, dir.getUV().w, barFill));
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        int progressTime;
        int x = this.realX(), y = this.realY(), xLocation = uv.x, yLocation = uv.y, length = uv.z, width = uv.w;
        switch (direction) {
            case TOP -> {
                progressTime = (int) (uv.w * percent);
                if (!barFill) {
                    progressTime = width - progressTime;
                }
                y = (y + width) - progressTime;
                yLocation = (yLocation + width) - progressTime;
                width = progressTime;
            }
            case LEFT -> {
                progressTime = (int) (uv.z * percent);
                if (barFill) {
                    length = progressTime;
                } else {
                    length = length - progressTime;
                }
            }
            case BOTTOM -> {
                progressTime = (int) (uv.w * percent);
                if (barFill) {
                    width = progressTime;
                } else {
                    width = width - progressTime;
                }
            }
            default -> {
                progressTime = (int) (uv.z * percent);
                if (!barFill) {
                    progressTime = length - progressTime;
                }
                x = (x + length) - progressTime;
                xLocation = (xLocation + length) - progressTime;
                length = progressTime;
            }
        }
        if (progress > 0) {
            drawTexture(matrixStack, gui.handler.getGuiTexture(), realX(), realY(), xLocation, yLocation, length, width);
        }
    }

    @Override
    public void mouseOver(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        if (isInside(mouseX, mouseY)) {
            renderTooltip(stack, new TranslatableComponent("antimatter.gui.show_recipes"), mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        if (this.gui.handler instanceof TileEntityMachine<?> machine) {
            AntimatterJEIREIPlugin.showCategory(machine.getMachineType(), machine.getMachineTier());
        }
    }
}
