package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.event.SlotClickEvent;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;
import static muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin.intToSuperScript;

public class FluidSlotWidget extends Widget {

    private final int slot;
    private final SlotData<?> slots;
    private FluidHolder stack = FluidHooks.emptyFluid();

    protected FluidSlotWidget(GuiInstance gui, IGuiElement parent, int fluidSlot, SlotData<?> slots) {
        super(gui, parent);
        this.slot = fluidSlot;
        this.slots = slots;
        setX(slots.getX());
        setY(slots.getY());
        setW(16);
        setH(16);
    }

    public static WidgetSupplier build(int slot, SlotData<?> slots) {
        return builder((a, b) -> new FluidSlotWidget(a, b, slot, slots));
    }

    @Override
    public void init() {
        super.init();
        if (this.gui.handler instanceof TileEntityMachine<?> blockEntity){
            this.gui.syncFluidStack(() -> blockEntity.fluidHandler
                    .map(t -> t.getFluidInTank(slot)).orElse(FluidHooks.emptyFluid()), stack -> this.stack = stack, SERVER_TO_CLIENT);
        }

    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        renderFluid(matrixStack, this.stack, realX(), realY());
    }

    @Environment(EnvType.CLIENT)
    public void renderFluid(PoseStack stack, FluidHolder fluid, int x, int y) {
        if (fluid.isEmpty())
            return;
        RenderHelper.drawFluid(stack, Minecraft.getInstance(), x, y, getW(), getH(), 16, fluid);
    }

    @Override
    public void mouseOver(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        if (this.stack.isEmpty())
            return;
        int x = realX();
        int y = realY();
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        int slotColor = -2130706433;
        this.fillGradient(stack, x, y, x + 16, y + 16, slotColor, slotColor);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
        List<Component> str = new ArrayList<>();
        str.add(FluidPlatformUtils.getFluidDisplayName(this.stack));
        long mb = (this.stack.getFluidAmount() / TesseractGraphWrappers.dropletMultiplier);
        if (AntimatterPlatformUtils.isFabric()){
            str.add(new TranslatableComponent("antimatter.tooltip.fluid.amount", new TextComponent(mb + " " + intToSuperScript(this.stack.getFluidAmount() % 81L) + "/₈₁ L")).withStyle(ChatFormatting.BLUE));
        } else {
            str.add(new TranslatableComponent("antimatter.tooltip.fluid.amount", mb + " L").withStyle(ChatFormatting.BLUE));
        }
        str.add(new TranslatableComponent("antimatter.tooltip.fluid.temp", FluidPlatformUtils.getFluidTemperature(this.stack.getFluid())).withStyle(ChatFormatting.RED));
        String liquid = !FluidPlatformUtils.isFluidGaseous(this.stack.getFluid()) ? "liquid" : "gas";
        str.add(new TranslatableComponent("antimatter.tooltip.fluid." + liquid).withStyle(ChatFormatting.GREEN));
        AntimatterJEIREIPlugin.addModDescriptor(str, this.stack);
        drawHoverText(str, (int) mouseX, (int) mouseY, Minecraft.getInstance().font, stack);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers, double mouseX, double mouseY) {
        if (!isInside(mouseX, mouseY))
            return super.keyPressed(keyCode, scanCode, modifiers, mouseX, mouseY);
        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
        if (!(input.getName().equals("key.keyboard.u") || input.getName().equals("key.keyboard.r")))
            return false;
        AntimatterJEIREIPlugin.uses(stack, input.getName().equals("key.keyboard.u"));
        return true;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        AbstractGuiEventPacket pkt = gui.handler.createGuiPacket(new SlotClickEvent(this.slot, this.slots.getType()));
        gui.sendPacket(pkt);
    }
}
