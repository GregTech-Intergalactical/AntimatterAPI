package muramasa.gregtech.common.events;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockHandler {

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock e) {
        if (/*e.getWorld().isRemote || */e.getHand() == EnumHand.OFF_HAND) return;
        //TODO fix
        if (e.getEntityPlayer().getHeldItem(e.getHand()) != ItemStack.EMPTY) return;
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (tile == null) return;
        if (tile.hasCapability(GTCapabilities.COVERABLE, e.getFace())) {
            tile.getCapability(GTCapabilities.COVERABLE, e.getFace()).get(e.getFace()).onInteract(e.getEntityPlayer(), tile, e.getFace(), null);
        } else if (tile.hasCapability(GTCapabilities.CONFIGURABLE, e.getFace())) {
            tile.getCapability(GTCapabilities.CONFIGURABLE, e.getFace()).onInteract(e.getEntityPlayer(), e.getFace(), null);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (!(tile instanceof TileEntityComponent)) return;
        System.out.println("BROKE COMPONENT");
    }
}
