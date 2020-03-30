package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.IIngredientRenderer;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.fluid.AntimatterFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidStackRenderer implements IIngredientRenderer<FluidStack> {

    public FluidStackRenderer() {

    }

    @Override
    public void render(int xPosition, int yPosition, @Nullable FluidStack fluidStack) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();

        RenderHelper.drawFluid(Minecraft.getInstance(), xPosition, yPosition, 16, 16, 16, fluidStack);

        RenderSystem.color4f(1, 1, 1, 1);

//        if (overlay != null) {
//            GlStateManager.pushMatrix();
//            GlStateManager.translate(0, 0, 200);
//            overlay.draw(minecraft, xPosition, yPosition);
//            GlStateManager.popMatrix();
//        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    @Override
    public List<String> getTooltip(FluidStack fluidStack, ITooltipFlag tooltipFlag) {
        return getFluidTooltip(fluidStack);
    }

    public static List<String> getFluidTooltip(FluidStack fluidStack) {
        List<String> tooltip = new ArrayList<>();
        //tooltip.add(fluidStack.getFluid().getLocalizedName(fluidStack));
        tooltip.add(TextFormatting.BLUE + "Amount: " + fluidStack.getAmount());
        tooltip.add(TextFormatting.RED + "Temp: " + fluidStack.getFluid().getAttributes().getTemperature() + " K");
        if (fluidStack.getFluid() instanceof AntimatterFluid) {
            tooltip.add(TextFormatting.GREEN + "State: " + ((AntimatterFluid) fluidStack.getFluid()).getState());
        } else {
            tooltip.add(TextFormatting.GREEN + "State: " + (fluidStack.getFluid().getAttributes().isGaseous() ? "Gas" : "Liquid"));
        }
        return tooltip;
    }
}
