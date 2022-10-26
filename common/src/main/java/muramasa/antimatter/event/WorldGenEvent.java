package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.worldgen.vein.WorldGenVein;

import java.util.List;

public class WorldGenEvent {
    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();

    public void vein(List<WorldGenVein> veins) {
        VEINS.addAll(veins);
    }
}
