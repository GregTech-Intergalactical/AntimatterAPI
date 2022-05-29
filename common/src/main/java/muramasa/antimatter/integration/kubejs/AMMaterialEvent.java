package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import muramasa.antimatter.event.MaterialEvent;

public class AMMaterialEvent extends EventJS {
    final MaterialEvent event;
    public AMMaterialEvent(MaterialEvent event){
        this.event = event;
    }

    public MaterialEvent getEvent() {
        return event;
    }
}
