package muramasa.antimatter.event.fabric;

import muramasa.antimatter.event.CraftingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class CraftingEvents {
    public static final Event<Crafting> CRAFTING = EventFactory.createArrayBacked(Crafting.class, listeners -> event -> {
        for (Crafting listener : listeners) {
            listener.onMaterialRegister(event);
        }
    });

    public interface Crafting {
        void onMaterialRegister(CraftingEvent event);
    }
}
