package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.List;

// TODO - recipe stuff only when tile.getMachineType().has(MachineFlag.RECIPE)
public class ScreenMachine<T extends TileEntityMachine<T>, U extends ContainerMachine<T>> extends AntimatterContainerScreen<U> implements IHasContainer<U> {

    protected U container;
    protected String name;

    public ScreenMachine(U container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        //gui = container.getTile().getMachineType().getGui().getTexture(container.getTile().getMachineTier(), "machine");
    }

    protected void drawTitle(MatrixStack stack, int mouseX, int mouseY) {
        Minecraft.getInstance().font.draw(stack, name, getCenteredStringX(name), 4, 0x404040);
    }

    /*
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
        Slot slot = getSlotUnderMouse();
        //TODO: Properly do this.
        if (!(input.getTranslationKey().equals("key.keyboard.u") || input.getTranslationKey().equals("key.keyboard.r"))) return false;
        if (slot instanceof SlotFakeFluid) {
            SlotFakeFluid fl = (SlotFakeFluid) slot;
            container.getTile().fluidHandler.ifPresent(t -> {
                FluidStack stack = fl.dir == FluidHandler.FluidDirection.INPUT ? t.getInputTanks().getFluidInTank(fl.getSlotIndex()) : t.getOutputTanks().getFluidInTank(fl.getSlotIndex());
                if (!stack.isEmpty()) {
                    AntimatterJEIPlugin.uses(stack,input.getTranslationKey().equals("key.keyboard.u"));
                }
            });
        }
        return false;
    }*/

    //@Nonnull final ItemStack stack, MatrixStack mStack, List<? extends ITextProperties> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font
    protected void drawText(List<? extends ITextProperties> textLines, int x, int y, FontRenderer font, MatrixStack matrixStack) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiUtils.drawHoveringText(ItemStack.EMPTY, matrixStack, textLines, x, y, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight(), -1, font);
    }

    private boolean isSlotSelected(int x, int y, double mouseX, double mouseY) {
        return this.isHovering(x, y, 16, 16, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        //container.getTile().drawInfo(stack, Minecraft.getInstance().fontRenderer, guiLeft, guiTop);
    }
    /*
    protected void drawProgress(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        GuiData data = container.getTile().getMachineType().getGui();

        if (container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgressRaw).orElse(0) <= 0){
            return;
        }
        int x = data.getProgress().x, y = data.getProgress().y, xLocation = data.getProgressLocation().x, yLocation = data.getProgressLocation().y, length = data.getProgress().z, width = data.getProgress().w;
        int progressTime;
        switch (data.getDir()){
            case TOP:
                progressTime = (int) (data.getProgress().w * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (!data.isBarFill()) {
                    progressTime = width - progressTime;
                }
                y = (y + width) - progressTime;
                yLocation = (yLocation + width) - progressTime;
                width = progressTime;
                break;
            case LEFT:
                progressTime = (int) (data.getProgress().z * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (data.isBarFill()){
                    length = progressTime;
                } else {
                    length = length - progressTime;
                }
                break;
            case BOTTOM:
                progressTime = (int) (data.getProgress().w * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (data.isBarFill()){
                    width = progressTime;
                } else {
                    width = width - progressTime;
                }
                break;
            default:
                progressTime = (int) (data.getProgress().z * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
                if (!data.isBarFill()) {
                    progressTime = length - progressTime;
                }
                x = (x + length) - progressTime;
                xLocation = (xLocation + length) - progressTime;
                length = progressTime;
                break;
        }
        drawTexture(stack, gui, guiLeft + x, guiTop + y, xLocation, yLocation, length, width);
    }
    */
}
