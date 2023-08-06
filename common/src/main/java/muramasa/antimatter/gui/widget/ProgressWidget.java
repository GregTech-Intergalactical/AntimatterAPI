package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.*;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int4;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;


public class ProgressWidget extends Widget {
    public final BarDir direction;
    public final boolean barFill;

    ResourceLocation texture;
    private final int4 uv;
    private int progress = 0;
    private int maxProgress = 0;
    private float percent = 0.0F;

    public ProgressWidget(GuiInstance instance, IGuiElement parent) {
        super(instance, parent);
        GuiData gui = instance.handler.getGui();
        this.direction = gui.getMachineData().getDir();
        this.uv = new int4(0, gui.getMachineData().getProgressSize().y, gui.getMachineData().getProgressSize().x, gui.getMachineData().getProgressSize().y);
        this.barFill = gui.getMachineData().doesBarFill();
        setX(gui.getMachineData().getProgressPos().x + 6);
        setY(gui.getMachineData().getProgressPos().y + 6);
        setW(gui.getMachineData().getProgressSize().x);
        setH(gui.getMachineData().getProgressSize().y);
        texture = gui.getMachineData().getProgressTexture(((TileEntityMachine<?>)instance.handler).getMachineTier());
    }

    @Override
    public void init() {
        super.init();
        gui.syncFloat(() -> ((ContainerMachine<?>) gui.container).getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F), i -> this.percent = i, SERVER_TO_CLIENT);
        gui.syncInt(() -> ((ContainerMachine<?>) gui.container).getTile().recipeHandler.map(MachineRecipeHandler::getCurrentProgress).orElse(0), i -> this.progress = i, SERVER_TO_CLIENT);
        gui.syncInt(() -> ((ContainerMachine<?>) gui.container).getTile().recipeHandler.map(rec -> rec.getActiveRecipe() == null ? 0 : rec.getActiveRecipe().getDuration()).orElse(0), i -> this.maxProgress = i, SERVER_TO_CLIENT);
    }

    public static WidgetSupplier build() {
        return builder(ProgressWidget::new);
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
        drawTexture(matrixStack, texture, realX(), realY(), 0, 0, uv.z, uv.w, uv.w, uv.z * 2);
        if (progress > 0) {
            drawTexture(matrixStack, texture, realX(), realY(), xLocation, yLocation, length, width, uv.w, uv.z * 2);
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
