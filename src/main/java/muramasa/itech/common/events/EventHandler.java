package muramasa.itech.common.events;

import net.minecraftforge.common.MinecraftForge;

public class EventHandler {

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BlockHighlightHandler());
        MinecraftForge.EVENT_BUS.register(new RenderGameOverlayHandler());
        MinecraftForge.EVENT_BUS.register(new RenderPlayerHandler());
        MinecraftForge.EVENT_BUS.register(new BlockHandler());
    }
}
