package muramasa.gregtech.common.events;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.util.ToolHelper;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber
public class RenderGameOverlayHandler extends Gui {

    private static ResourceLocation energyBar = new ResourceLocation(Ref.MODID, "textures/gui/energy_bar.png");

    @SubscribeEvent
    public static void onRenderDebugInfo(RenderGameOverlayEvent.Text e) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            TileEntity tile = Utils.getTile(Minecraft.getMinecraft().world, Minecraft.getMinecraft().objectMouseOver.getBlockPos());
            if (tile instanceof TileEntityBase) {
                e.getLeft().addAll(((TileEntityBase) tile).getInfo());
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre e) {
        EntityPlayerSP entityPlayerSP = Ref.mc.player;
        ItemStack stack = entityPlayerSP.getHeldItemMainhand();
        if (ToolType.isPowered(stack) && e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && !entityPlayerSP.isCreative()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post e) {
        EntityPlayerSP entityPlayerSP = Ref.mc.player;
        ItemStack stack = entityPlayerSP.getHeldItemMainhand();
        if (e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (ToolType.isPowered(stack) && !entityPlayerSP.isCreative()) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPushMatrix();
                int x = (e.getResolution().getScaledWidth() / 2) - 91;
                int y = e.getResolution().getScaledHeight() - 29;
                Ref.mc.renderEngine.bindTexture(energyBar);
                int energySize = (int)(180 * ((float) ToolHelper.getEnergy(stack) / (float) ToolHelper.getMaxEnergy(stack)));
                drawModalRectWithCustomSizedTexture(x, y, 0, 0, 182, 5, 182, 15);
                drawModalRectWithCustomSizedTexture(x + 1, y + 1, 0, 6, energySize, 3, 182, 15);
                int iconU = energySize == 0 ? 4 : 0;
                drawModalRectWithCustomSizedTexture(x + 89, y, iconU, 10, 4, 5, 182, 15);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            } else if (ItemType.DebugScanner.isEqual(stack)) {

            }
        }
    }
}
