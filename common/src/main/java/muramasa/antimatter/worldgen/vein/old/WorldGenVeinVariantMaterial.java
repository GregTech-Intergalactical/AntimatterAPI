package muramasa.antimatter.worldgen.vein.old;

import com.google.gson.JsonObject;
import muramasa.antimatter.material.Material;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldGenVeinVariantMaterial {

    public final int weight;
    public final int maxY;
    public final Material material;
    public final int minY;

    WorldGenVeinVariantMaterial(int weight, Material material, int minY, int maxY) {
        this.weight = weight;
        this.material = material;
        this.minY = minY;
        this.maxY = maxY;
    }

    static List<WorldGenVeinVariantMaterial> getFlat(int weight, Material material, int minY, int maxY) {
        return IntStream.range(0, weight).mapToObj(i -> new WorldGenVeinVariantMaterial(1, material, minY, maxY)).collect(Collectors.toList());
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("weight", weight);
        json.addProperty("material", material.getId());
        if (minY > Integer.MIN_VALUE) {
            json.addProperty("minY", minY);
        }
        if (maxY < Integer.MAX_VALUE) {
            json.addProperty("maxY", maxY);
        }
        return json;
    }

    public static WorldGenVeinVariantMaterial fromJson(JsonObject json){
        return new WorldGenVeinVariantMaterial(
                json.get("weight").getAsInt(),
                Material.get(json.get("material").getAsString()),
                json.has("minY") ? json.get("minY").getAsInt() : Integer.MIN_VALUE,
                json.has("maxY") ? json.get("maxY").getAsInt() : Integer.MAX_VALUE);
    }
}
