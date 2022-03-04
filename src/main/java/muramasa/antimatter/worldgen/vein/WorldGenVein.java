package muramasa.antimatter.worldgen.vein;

import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldGenVein extends WorldGenBase<WorldGenVein> {

    public static final int STONE_VEIN_LAYER = 0;
    public static final int STONE_ORE_VEIN_LAYER = 1;
    public static final int ORE_VEIN_LAYER = 2;

    private static final HashMap<Integer, Integer> MAX_SIZE_PER_LAYER = new HashMap<>();
    private static final HashMap<Integer, Float> CHANCE_PER_LAYER = new HashMap<>();

    public final int layer;
    public final int weight;
    public final int minY;
    public final int maxY;
    public final int minSize;
    public final int maxSize;
    public final float heightScale;
    @Nullable
    public final BlockState fill;
    public final List<WorldGenVeinVariant> variants;

    private WorldGenVein(String id, int layer, int weight, int minY, int maxY, int minSize, int maxSize, float heightScale, @Nullable BlockState fill, List<WorldGenVeinVariant> variants, List<ResourceKey<Level>> dimensions) {
        super(id, WorldGenVein.class, dimensions);
        this.layer = layer;
        this.weight = weight;
        this.minY = minY;
        this.maxY = maxY;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.heightScale = heightScale;
        this.fill = fill;
        this.variants = variants;
        Integer existingMaxSize = MAX_SIZE_PER_LAYER.get(layer);
        if (existingMaxSize == null || maxSize > existingMaxSize) {
            MAX_SIZE_PER_LAYER.put(layer, maxSize);
        }
    }

    static List<WorldGenVein> getFlat(String id, int layer, int weight, int minY, int maxY, int minSize, int maxSize, float heightScale, @Nullable BlockState fill, List<WorldGenVeinVariant> variants, List<ResourceKey<Level>> dimensions) {
        return IntStream.range(0, weight).mapToObj(i -> {
            List<WorldGenVeinVariant> flatVariants = new ArrayList<>();
            for (WorldGenVeinVariant variant : variants) {
                flatVariants.addAll(WorldGenVeinVariant.getFlat(variant.weight, variant.oreChance, variant.smallOreChance, variant.markerOreChance, variant.surfaceStoneChance, variant.materials));
            }
            return new WorldGenVein(
                    id,
                    layer,
                    1,
                    minY,
                    maxY,
                    minSize,
                    maxSize,
                    heightScale,
                    fill,
                    flatVariants,
                    dimensions
            );
        }).collect(Collectors.toList());
    }

    public static Set<Integer> getAllLayers() {
        return MAX_SIZE_PER_LAYER.keySet();
    }

    public static int getMaxLayerSize(int layer) {
        Integer size = MAX_SIZE_PER_LAYER.get(layer);
        if (size != null) {
            return size;
        } else {
            return 0;
        }
    }

    public static void setLayerChance(int layer, float chance) {
        CHANCE_PER_LAYER.put(layer, chance);
    }

    public static double getLayerChance(int layer) {
        Float chance = CHANCE_PER_LAYER.get(layer);
        if (chance != null) {
            return chance;
        } else {
            return 0.1;
        }
    }

}
