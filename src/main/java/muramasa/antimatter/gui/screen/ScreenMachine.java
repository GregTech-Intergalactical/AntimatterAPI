package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.slot.SlotFakeFluid;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.machine.MachineFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static muramasa.antimatter.gui.SlotType.FL_IN;
import static muramasa.antimatter.gui.SlotType.FL_OUT;

// TODO - recipe stuff only when tile.getMachineType().has(MachineFlag.RECIPE)
public class ScreenMachine<T extends ContainerMachine> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected T container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        gui = container.getTile().getMachineType().getGui().getTexture(container.getTile().getMachineTier(), "machine");
        if (container.getTile().isClientSide()) container.getTile().recipeHandler.ifPresent(rh -> rh.setClientProgress(0));
    }

    protected void drawTitle(MatrixStack stack, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(stack, name, getCenteredStringX(name), 4, 0x404040);
    }



    @Override
    protected void init() {
        super.init();
        ResourceLocation loc = container.getTile().getMachineType().getGui().getButtonLocation();
        for (ButtonData button : container.getTile().getMachineType().getGui().getButtons()) {
            addButton(button.getType().getButtonSupplier().get(guiLeft, guiTop, container.getTile(), playerInventory, loc, button));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        drawTitle(stack, mouseX, mouseY);
        if (container.getTile().has(MachineFlag.RECIPE)) {
            drawTooltipInArea(stack,"Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
        if (container.getTile().has(MachineFlag.FLUID)) {
            //TODO
            container.getTile().fluidHandler.ifPresent(t -> {
                int[] index = new int[]{0};
                FluidStack[] inputs = t.getInputs();
                container.getTile().getMachineType().getGui().getSlots(FL_IN).forEach(sl -> {
                    renderFluid(stack, inputs[index[0]++], sl,mouseX,mouseY);
                });
                index[0] = 0;
                FluidStack[] outputs = t.getOutputs();
                container.getTile().getMachineType().getGui().getSlots(FL_OUT).forEach(sl -> {
                    renderFluid(stack, outputs[index[0]++], sl, mouseX,mouseY);
                });
            });
        }
    }

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
                FluidStack stack = t.getFluidInTank(fl.getSlotIndex());
                if (!stack.isEmpty()) {
                    AntimatterJEIPlugin.uses(stack,input.getTranslationKey().equals("key.keyboard.u"));
                }
            });
        }
        return false;
    }

    public void renderFluid(MatrixStack stack, FluidStack fluid, SlotData slot, int mouseX, int mouseY) {
        int x = slot.getX();
        int y = slot.getY();
        if (fluid.isEmpty()) return;
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        ResourceLocation stillLocation = fluid.getFluid().getAttributes().getStillTexture(fluid);
        int color = fluid.getFluid().getAttributes().getColor();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(stillLocation);
        ResourceLocation spriteLocation = sprite.getName();
        minecraft.getTextureManager().bindTexture(new ResourceLocation(spriteLocation.getNamespace(), "textures/" + spriteLocation.getPath() + ".png"));
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderSystem.color4f(r,g,b,0.6f);
        blit(stack,/*guiLeft +*/ x, /*guiTop +*/ y, 0, 16, 16, sprite);
        RenderSystem.color4f(1,1,1,1);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();

        if (this.isSlotSelected(slot.getX(), slot.getY(), mouseX, mouseY)) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            int slotColor = -2130706433;
            this.fillGradient(stack, x, y, x + 16, y + 16, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            List<String> str = new ArrayList<>();
            str.add(fluid.getDisplayName().getString());
            str.add(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluid.getAmount()) + " mB").mergeStyle(TextFormatting.GRAY).getString());
            AntimatterJEIPlugin.addModDescriptor(str, fluid);
            this.renderTooltip(stack, str.stream().map(t -> IReorderingProcessor.fromString(t, Style.EMPTY)).collect(Collectors.toList()), mouseX-guiLeft,mouseY-guiTop);
        }
    }

    private boolean isSlotSelected(int x, int y, double mouseX, double mouseY) {
        return this.isPointInRegion(x, y, 16, 16, mouseX, mouseY);
    }
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        container.getTile().drawInfo(stack, Minecraft.getInstance().fontRenderer, guiLeft, guiTop);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        drawTexture(stack, gui, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected void drawProgress(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        int progressTime = (int) (20 * container.getTile().recipeHandler.map(MachineRecipeHandler::getClientProgress).orElse(0F));
        drawTexture(stack, gui, guiLeft + (xSize / 2) - 10, guiTop + 24, xSize, 0, progressTime, 18);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!ModList.get().isLoaded("jei") || !container.getTile().has(MachineFlag.RECIPE)) return false;
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            AntimatterJEIPlugin.showCategory(container.getTile().getMachineType());
            return true;
        }
        return false;
    }
}
