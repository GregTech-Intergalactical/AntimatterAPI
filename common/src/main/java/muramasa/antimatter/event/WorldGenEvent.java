package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOreMaterial;
import muramasa.antimatter.worldgen.vein.WorldGenVein;

import java.util.List;

public class WorldGenEvent {
    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();

    public final List<WorldGenSmallOreMaterial> SMALL_ORES = new ObjectArrayList<>();

    public void vein(List<WorldGenVein> veins) {
        VEINS.addAll(veins);
    }

    public void smallOre(List<WorldGenSmallOreMaterial> veins) {
        SMALL_ORES.addAll(veins);
    }
}
