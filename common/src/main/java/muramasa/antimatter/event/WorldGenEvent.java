package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.worldgen.StoneLayerOre;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOre;
import muramasa.antimatter.worldgen.vanillaore.WorldGenVanillaOre;
import muramasa.antimatter.worldgen.vein.WorldGenVeinLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WorldGenEvent {
    public final List<WorldGenVeinLayer> VEINS = new ObjectArrayList<>();
    public final List<WorldGenStoneLayer> STONE_LAYERS = new ObjectArrayList<>();

    public final List<WorldGenSmallOre> SMALL_ORES = new ObjectArrayList<>();

    public final List<WorldGenVanillaOre> VANILLA_ORES = new ObjectArrayList<>();
    public final Int2ObjectOpenHashMap<List<StoneLayerOre>> COLLISION_MAP = new Int2ObjectOpenHashMap<>();

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

    public void addCollision(BlockState top, BlockState bottom, StoneLayerOre... oresToAdd) {
        COLLISION_MAP.computeIfAbsent(Objects.hash(top, bottom), k -> new ObjectArrayList<>()).addAll(Arrays.asList(oresToAdd));
    }
}
