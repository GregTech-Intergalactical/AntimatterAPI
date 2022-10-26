package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

// TODO - recipe stuff only when tile.getMachineType().has(MachineFlag.RECIPE)
public class ScreenMachine<T extends TileEntityMachine<T>, U extends ContainerMachine<T>> extends AntimatterContainerScreen<U> implements MenuAccess<U> {

    protected U container;
    protected String name;

    public ScreenMachine(U container, Inventory inv, Component name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        //gui = container.getTile().getMachineType().getGui().getTexture(container.getTile().getMachineTier(), "machine");
    }

    protected void drawTitle(PoseStack stack, int mouseX, int mouseY) {
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
                    AntimatterJEIREIPlugin.uses(stack,input.getTranslationKey().equals("key.keyboard.u"));
                }
            });
        }
        return false;
    }*/


    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        //container.getTile().drawInfo(stack, Minecraft.getInstance().fontRenderer, guiLeft, guiTop);
    }
}
