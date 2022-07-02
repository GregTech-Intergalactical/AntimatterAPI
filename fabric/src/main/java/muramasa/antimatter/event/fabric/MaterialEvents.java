package muramasa.antimatter.event.fabric;

import muramasa.antimatter.event.MaterialEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class MaterialEvents {
    public static final Event<Register> MATERIAL = EventFactory.createArrayBacked(Register.class, listeners -> event -> {
        for (Register listener : listeners) {
            listener.onMaterialRegister(event);
        }
    });

    @FunctionalInterface
    public interface Register{
        void onMaterialRegister(MaterialEvent event);
    }
}
