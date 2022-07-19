package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.material.MaterialType;

public class AMMaterialEvent extends EventJS {
    final MaterialEvent event;
    public AMMaterialEvent(MaterialEvent event){
        this.event = event;
    }

    public MaterialEvent getEvent() {
        return event;
    }

    public MaterialType type(String type) {
        return AntimatterAPI.get(MaterialType.class, type);
    }
}
