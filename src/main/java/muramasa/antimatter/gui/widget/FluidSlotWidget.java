package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.gui.*;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FluidSlotWidget extends Widget {

    private final int slot;
    private final SlotData<?> slots;
    private FluidStack stack = FluidStack.EMPTY;

    protected FluidSlotWidget(GuiInstance gui, int fluidSlot, SlotData<?> slots) {
        super(gui);
        this.slot = fluidSlot;
        this.slots = slots;
        setX(slots.getX());
        setY(slots.getY());
        setW(16);
        setH(16);
    }

    public static WidgetSupplier.WidgetProvider build(int slot, SlotData<?> slots) {
        return a -> new FluidSlotWidget(a,slot, slots);
    }

    @Override
    public void init(IGuiHandler source, ICanSyncData data, Container container) {
        super.init(source, data, container);
        ICapabilityProvider provider = (ICapabilityProvider) source;
        data.syncFluidStack(() -> provider.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(t -> t.getFluidInTank(slot)).orElse(FluidStack.EMPTY), stack -> this.stack = stack);
    }

    @Override
    protected void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderFluid(matrixStack, this.stack, this.slots, realX(), realY(), mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderFluid(MatrixStack stack, FluidStack fluid, SlotData slot, int x, int y, int mouseX, int mouseY) {
        if (fluid.isEmpty()) return;
        RenderHelper.drawFluid(stack, Minecraft.getInstance(), x, y, getW(), getH(), 16, fluid);
        if (this.isInside(mouseX, mouseY)) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            int slotColor = -2130706433;
            this.fillGradient(stack, x, y, x + 16, y + 16, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            List<ITextComponent> str = new ArrayList<>();
            str.add(new StringTextComponent(fluid.getDisplayName().getString()));
            str.add(new StringTextComponent(NumberFormat.getNumberInstance(Locale.US).format(fluid.getAmount()) + " mB").mergeStyle(TextFormatting.GRAY));
            AntimatterJEIPlugin.addModDescriptor(str, fluid);
            drawText(str, mouseX, mouseY+getH(), Minecraft.getInstance().fontRenderer, stack);
        }
    }
}
