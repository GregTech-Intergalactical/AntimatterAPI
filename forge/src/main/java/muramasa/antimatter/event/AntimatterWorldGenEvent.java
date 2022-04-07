package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.worldgen.vein.WorldGenVein;

import java.util.List;

public class AntimatterWorldGenEvent extends AntimatterEvent{

    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();

    public AntimatterWorldGenEvent(IAntimatterRegistrar registrar) {
        super(registrar);
    }

    public void vein(List<WorldGenVein> veins) {
        VEINS.addAll(veins);
    }
}
