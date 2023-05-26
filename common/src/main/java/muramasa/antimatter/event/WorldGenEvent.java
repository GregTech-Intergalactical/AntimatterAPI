package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOre;
import muramasa.antimatter.worldgen.vanillaore.WorldGenVanillaOre;
import muramasa.antimatter.worldgen.vein.WorldGenVein;

import java.util.List;

public class WorldGenEvent {
    public final List<WorldGenVein> VEINS = new ObjectArrayList<>();

    public final List<WorldGenSmallOre> SMALL_ORES = new ObjectArrayList<>();

    public final List<WorldGenVanillaOre> VANILLA_ORES = new ObjectArrayList<>();

    public void vein(List<WorldGenVein> veins) {
        VEINS.addAll(veins);
    }

    public void smallOre(WorldGenSmallOre veins) {
        if (SMALL_ORES.stream().anyMatch(s -> s.getId().equals(veins.getId()))){
            Antimatter.LOGGER.warn("Duplicate small ore spawn, aborting. Id: " + veins.getId());
            return;
        }
        SMALL_ORES.add(veins);
    }

    public void vanillaOre(WorldGenVanillaOre veins) {
        if (VANILLA_ORES.stream().anyMatch(s -> s.getId().equals(veins.getId()))){
            Antimatter.LOGGER.warn("Duplicate vanilla ore vein, aborting. Id: " + veins.getId());
            return;
        }
        VANILLA_ORES.add(veins);
    }
}
