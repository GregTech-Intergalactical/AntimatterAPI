//package muramasa.gtu.integration.jei.renderer;
//
//import mezz.jei.api.ingredients.IIngredientRenderer;
//import muramasa.gtu.client.render.RenderHelper;
//import muramasa.antimatter.fluid.GregTechFluid;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraftforge.fluids.FluidStack;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FluidStackRenderer implements IIngredientRenderer<FluidStack> {
//
//    public FluidStackRenderer() {
//
//    }
//
//    @Override
//    public void render(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
//        GlStateManager.enableBlend();
//        GlStateManager.enableAlpha();
//
//        RenderHelper.drawFluid(minecraft, xPosition, yPosition, 16, 16, 16, fluidStack);
//
//        GlStateManager.color(1, 1, 1, 1);
//
////        if (overlay != null) {
////            GlStateManager.pushMatrix();
////            GlStateManager.translate(0, 0, 200);
////            overlay.draw(minecraft, xPosition, yPosition);
////            GlStateManager.popMatrix();
////        }
//
//        GlStateManager.disableAlpha();
//        GlStateManager.disableBlend();
//    }
//
//    @Override
//    public List<String> getTooltip(Minecraft minecraft, FluidStack fluidStack, ITooltipFlag tooltipFlag) {
//        return getFluidTooltip(fluidStack);
//    }
//
//    public static List<String> getFluidTooltip(FluidStack fluidStack) {
//        List<String> tooltip = new ArrayList<>();
//        tooltip.add(fluidStack.getFluid().getLocalizedName(fluidStack));
//        tooltip.add(TextFormatting.BLUE + "Amount: " + fluidStack.amount);
//        tooltip.add(TextFormatting.RED + "Temp: " + fluidStack.getFluid().getTemperature() + " K");
//        if (fluidStack.getFluid() instanceof GregTechFluid) {
//            tooltip.add(TextFormatting.GREEN + "State: " + ((GregTechFluid) fluidStack.getFluid()).getState());
//        } else {
//            tooltip.add(TextFormatting.GREEN + "State: " + (fluidStack.getFluid().isGaseous() ? "Gas" : "Liquid"));
//        }
//        return tooltip;
//    }
//}
