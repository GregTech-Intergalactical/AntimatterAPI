package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class AntimatterContainerScreen<T extends Container> extends ContainerScreen<T> {

    public AntimatterContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
        super(container, invPlayer, title);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public void drawTexture(ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(loc);
        blit(left, top, x, y, sizeX, sizeY);
    }

    public int getCenteredStringX(String s) {
        return xSize / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s) / 2;
    }

    public void drawTooltipInArea(String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        List<String> list = new ObjectArrayList<>();
        list.add(line);
        drawTooltipInArea(list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(List<String> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            renderTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }

//    public void drawContainedFluids(int mouseX, int mouseY) {
//        tile.fluidHandler.ifPresent(h -> {
//            FluidStack[] fluids;
//            List<SlotData> slots;
//
//            //TODO this should use getInputsRaw to preserve ordering
//            fluids = h.getInputs();
//            slots = tile.getType().getGui().getSlots(SlotType.FL_IN, tile.getTier());
//            if (fluids != null) {
//                for (int i = 0; i < fluids.length; i++) {
//                    if (i >= slots.size()) continue;
//                    RenderHelper.drawFluid(mc, slots.get(i).x, slots.get(i).y, 16, 16, 16, fluids[i]);
//                    drawTooltipInArea(FluidStackRenderer.getFluidTooltip(fluids[i]), mouseX, mouseY, slots.get(i).x, slots.get(i).y, 16, 16);
//                }
//            }
//            fluids = h.getOutputs();
//            slots = tile.getType().getGui().getSlots(SlotType.FL_OUT, tile.getTier());
//            if (fluids != null) {
//                for (int i = 0; i < fluids.length; i++) {
//                    if (i >= slots.size()) continue;
//                    RenderHelper.drawFluid(mc, slots.get(i).x, slots.get(i).y, 16, 16, 16, fluids[i]);
//                    drawTooltipInArea(FluidStackRenderer.getFluidTooltip(fluids[i]), mouseX, mouseY, slots.get(i).x, slots.get(i).y, 16, 16);
//                }
//            }
//        });
//    }
}
