package muramasa.antimatter.event.forge;

import net.minecraftforge.eventbus.api.Event;

public abstract class AntimatterEvent extends Event {
    public final IAntimatterRegistrar sender;

    public AntimatterEvent(IAntimatterRegistrar registrar) {
        this.sender = registrar;
    }
}

