package muramasa.antimatter.worldgen.vein.old;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
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
    public final int density;
    public final int minSize;
    public final int maxSize;
    public final float heightScale;
    @Nullable
    public final BlockState fill;
    public final List<WorldGenVeinVariant> variants;

    WorldGenVein(String id, int layer, int weight, int minY, int maxY, int density, int minSize, int maxSize, float heightScale, @Nullable BlockState fill, List<WorldGenVeinVariant> variants, List<ResourceKey<Level>> dimensions) {
        super(id, WorldGenVein.class, dimensions);
        this.layer = layer;
        this.weight = weight;
        this.minY = minY;
        this.maxY = maxY;
        this.density = density;
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

    static List<WorldGenVein> getFlat(String id, int layer, int weight, int minY, int maxY, int density, int minSize, int maxSize, float heightScale, @Nullable BlockState fill, List<WorldGenVeinVariant> variants, List<ResourceKey<Level>> dimensions) {
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
                    density,
                    minSize,
                    maxSize,
                    heightScale,
                    fill,
                    flatVariants,
                    dimensions
            );
        }).collect(Collectors.toList());
    }

    static List<WorldGenVein> getFlat(WorldGenVein vein){
        return getFlat(vein.getId(), vein.layer, vein.weight, vein.minY, vein.maxY, vein.density, vein.minSize, vein.maxSize, vein.heightScale, vein.fill, vein.variants, vein.getDims().stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());
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

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("layer", layer);
        json.addProperty("weight", weight);
        if (minY > Integer.MIN_VALUE) {
            json.addProperty("minY", minY);
        }
        if (maxY < Integer.MAX_VALUE) {
            json.addProperty("maxY", maxY);
        }
        json.addProperty("density", density);
        json.addProperty("minSize", minSize);
        json.addProperty("maxSize", maxSize);
        json.addProperty("heightScale", heightScale);
        if (fill != null){
            json.addProperty("fill", AntimatterPlatformUtils.getIdFromBlock(fill.getBlock()).toString());
        }
        JsonArray array = new JsonArray();
        variants.forEach(m -> {
            array.add(m.toJson());
        });
        if (!array.isEmpty()) {
            json.add("variants", array);
        }
        JsonArray array2 = new JsonArray();
        getDims().forEach(r -> array2.add(r.toString()));
        if (!array2.isEmpty()){
            json.add("dims", array2);
        }
        return json;
    }

    public static WorldGenVein fromJson(String id, JsonObject json){
        List<WorldGenVeinVariant> variants = new ArrayList<>();
        List<ResourceKey<Level>> dims = new ArrayList<>();
        if (json.has("variants")){
            JsonArray array = json.getAsJsonArray("variants");
            array.forEach(j -> {
                if (j instanceof JsonObject object){
                    variants.add(WorldGenVeinVariant.fromJson(object));
                }
            });
        }
        if (json.has("dims")){
            JsonArray array = json.getAsJsonArray("dims");
            array.forEach(j -> {
                if (j instanceof JsonPrimitive object){
                    dims.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(object.getAsString())));
                }
            });
        }
        BlockState fill = json.has("fill") ? AntimatterPlatformUtils.getBlockFromId(new ResourceLocation(json.get("fill").getAsString())).defaultBlockState() : null;
        return new WorldGenVein(
                id,
                json.get("layer").getAsInt(),
                json.get("weight").getAsInt(),
                json.has("minY") ? json.get("minY").getAsInt() : Integer.MIN_VALUE,
                json.has("maxY") ? json.get("maxY").getAsInt() : Integer.MAX_VALUE,
                json.get("density").getAsInt(),
                json.get("minSize").getAsInt(),
                json.get("maxSize").getAsInt(),
                json.get("heightScale").getAsFloat(),
                fill,
                variants,
                dims
        );
    }

}
