package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOre;
import muramasa.antimatter.worldgen.vanillaore.WorldGenVanillaOre;
import muramasa.antimatter.worldgen.vein.WorldGenVeinLayer;
import muramasa.antimatter.worldgen.vein.WorldGenVeinLayerBuilder;
import muramasa.antimatter.worldgen.vein.old.WorldGenVein;

import java.util.List;

public class WorldGenEvent {
    public final List<WorldGenVeinLayer> VEINS = new ObjectArrayList<>();
    public final List<WorldGenStoneLayer> STONE_LAYERS = new ObjectArrayList<>();

    public final List<WorldGenSmallOre> SMALL_ORES = new ObjectArrayList<>();

    public final List<WorldGenVanillaOre> VANILLA_ORES = new ObjectArrayList<>();

    public void vein(WorldGenVeinLayer veins) {
        if (VEINS.stream().anyMatch(s -> s.getId().equals(veins.getId()))){
            Antimatter.LOGGER.warn("Duplicate vein layer spawn, aborting. Id: " + veins.getId());
            return;
        }
        VEINS.add(veins);
    }

    public void stoneLayer(List<WorldGenStoneLayer> veins) {
        STONE_LAYERS.addAll(veins);
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
