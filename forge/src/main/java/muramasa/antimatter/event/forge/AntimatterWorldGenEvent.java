package muramasa.antimatter.event.forge;

import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.worldgen.vein.old.WorldGenVein;

import java.util.List;

public class AntimatterWorldGenEvent extends AntimatterEvent {

    private final WorldGenEvent event;

    public AntimatterWorldGenEvent(IAntimatterRegistrar registrar, WorldGenEvent event) {
        super(registrar);
        this.event = event;
    }

    public WorldGenEvent getEvent() {
        return event;
    }
}
