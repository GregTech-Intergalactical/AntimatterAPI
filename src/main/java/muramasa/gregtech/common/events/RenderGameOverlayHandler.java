package muramasa.gregtech.common.events;

import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.util.ToolHelper;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class RenderGameOverlayHandler extends Gui {

    private static ResourceLocation energyBar = new ResourceLocation(Ref.MODID, "textures/gui/energy_bar.png");

    @SubscribeEvent(receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        EntityPlayerSP entityPlayerSP = Ref.mc.player;
        ItemStack stack = entityPlayerSP.getHeldItemMainhand();
        if (ToolType.isPowered(stack) && event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && !entityPlayerSP.isCreative()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        EntityPlayerSP entityPlayerSP = Ref.mc.player;
        ItemStack stack = entityPlayerSP.getHeldItemMainhand();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (ToolType.isPowered(stack) && !entityPlayerSP.isCreative()) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPushMatrix();
                int x = (event.getResolution().getScaledWidth() / 2) - 91;
                int y = event.getResolution().getScaledHeight() - 29;
                Ref.mc.renderEngine.bindTexture(energyBar);
                int energySize = (int)(180 * ((float) ToolHelper.getEnergy(stack) / (float) ToolHelper.getMaxEnergy(stack)));
                drawModalRectWithCustomSizedTexture(x, y, 0, 0, 182, 5, 182, 15);
                drawModalRectWithCustomSizedTexture(x + 1, y + 1, 0, 6, energySize, 3, 182, 15);
                int iconU = energySize == 0 ? 4 : 0;
                drawModalRectWithCustomSizedTexture(x + 89, y, iconU, 10, 4, 5, 182, 15);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            } else if (ItemType.DebugScanner.isItemEqual(stack)) {

            }
        }
    }
}
