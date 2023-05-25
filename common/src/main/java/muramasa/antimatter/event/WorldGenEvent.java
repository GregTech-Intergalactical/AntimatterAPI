package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOre;
import muramasa.antimatter.worldgen.vein.WorldGenVein;

import java.util.List;

public class WorldGenEvent {
    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();

    public final List<WorldGenSmallOre> SMALL_ORES = new ObjectArrayList<>();

    public void vein(List<WorldGenVein> veins) {
        VEINS.addAll(veins);
    }

    public void smallOre(WorldGenSmallOre veins) {
        SMALL_ORES.add(veins);
    }
}
