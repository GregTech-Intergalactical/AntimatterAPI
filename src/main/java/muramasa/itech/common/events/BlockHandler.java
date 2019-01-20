package muramasa.itech.common.events;

import muramasa.itech.api.capability.IComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockHandler {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (e.getWorld().getTileEntity(e.getPos()) instanceof IComponent) {
            System.out.println("BROKE COMPONENT");
        }
    }
}
