package muramasa.gregtech.common.events;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.tools.ToolType;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.api.interfaces.IComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BlockHandler {

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock e) {
        if (e.getHand() == EnumHand.OFF_HAND || ToolType.get(e.getItemStack()) != null) return;
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (tile == null) return;
        if (tile.hasCapability(GTCapabilities.COVERABLE, e.getFace())) {
            ToolType type = ToolType.get(e.getItemStack());
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, e.getFace());
            if (coverHandler == null) return;
            boolean swing = coverHandler.onInteract(e.getEntityPlayer(), e.getHand(), e.getFace(), type);
            if (swing) e.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
        } else if (tile.hasCapability(GTCapabilities.CONFIGURABLE, e.getFace())) {
            ToolType type = ToolType.get(e.getItemStack());
            IConfigHandler configHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, e.getFace());
            if (configHandler == null) return;
            boolean swing = configHandler.onInteract(e.getEntityPlayer(), e.getHand(), e.getFace(), type);
            if (swing) e.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (!(tile instanceof IComponent)) return;
        ((IComponent) tile).getComponentHandler().onComponentRemoved();
    }
}
