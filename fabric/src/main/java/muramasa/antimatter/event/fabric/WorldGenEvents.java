package muramasa.antimatter.event.fabric;

import muramasa.antimatter.event.WorldGenEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class WorldGenEvents {
    public static final Event<WorldGen> WORLD_GEN = EventFactory.createArrayBacked(WorldGen.class, listeners -> event -> {
        for (WorldGen listener : listeners) {
            listener.onWorldGen(event);
        }
    });

    public interface WorldGen {
        void onWorldGen(WorldGenEvent event);
    }
}
