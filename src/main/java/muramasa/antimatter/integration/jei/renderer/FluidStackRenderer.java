package muramasa.antimatter.integration.jei.renderer;

public class FluidStackRenderer /*implements IIngredientRenderer<FluidStack>*/ {

    public FluidStackRenderer() {

    }
/*
    @Override
    public void render(MatrixStack stack, int xPosition, int yPosition, @Nullable FluidStack fluidStack) {
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
    public List<ITextComponent> getTooltip(FluidStack fluidStack, ITooltipFlag tooltipFlag) {
        return getFluidTooltip(fluidStack);
    }

    public static List<ITextComponent> getFluidTooltip(FluidStack fluidStack) {
        List<String> tooltip = new ObjectArrayList<>();
        //tooltip.add(fluidStack.getFluid().getLocalizedName(fluidStack));
        tooltip.add(TextFormatting.BLUE + "Amount: " + fluidStack.getAmount());
        tooltip.add(TextFormatting.RED + "Temp: " + fluidStack.getFluid().getAttributes().getTemperature() + " K");

        //TODO @Rongmario
        //if (fluidStack.getFluid() instanceof AntimatterFluid) {
            //tooltip.add(TextFormatting.GREEN + "State: " + ((AntimatterFluid) fluidStack.getFluid()).getState());
        //} else {
            tooltip.add(TextFormatting.GREEN + "State: " + (fluidStack.getFluid().getAttributes().isGaseous() ? "Gas" : "Liquid"));
        //}
        return tooltip;
    }*/
}
