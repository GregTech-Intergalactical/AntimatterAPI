package muramasa.antimatter.worldgen.vein;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldGenVeinVariant {

    public final int weight;
    public final float oreChance;
    public final float smallOreChance;
    public final float markerOreChance;
    public final float surfaceStoneChance;
    public final List<WorldGenVeinVariantMaterial> materials;

    WorldGenVeinVariant(int weight, float oreChance, float smallOreChance, float markerOreChance, float surfaceStoneChance, List<WorldGenVeinVariantMaterial> materials) {
        this.weight = weight;
        this.oreChance = oreChance;
        this.smallOreChance = smallOreChance;
        this.markerOreChance = markerOreChance;
        this.surfaceStoneChance = surfaceStoneChance;
        this.materials = materials;
    }

    static List<WorldGenVeinVariant> getFlat(int weight, float oreChance, float smallOreChance, float markerOreChance, float surfaceStoneChance, List<WorldGenVeinVariantMaterial> materials) {
        return IntStream.range(0, weight).mapToObj(i -> {
            List<WorldGenVeinVariantMaterial> flatMaterials = new ArrayList<>();
            for (WorldGenVeinVariantMaterial material : materials) {
                flatMaterials.addAll(WorldGenVeinVariantMaterial.getFlat(material.weight, material.material, material.minY, material.maxY));
            }
            return new WorldGenVeinVariant(
                    1,
                    oreChance,
                    smallOreChance,
                    markerOreChance,
                    surfaceStoneChance,
                    flatMaterials
            );
        }).collect(Collectors.toList());
    }


    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("weight", weight);
        json.addProperty("oreChance", oreChance);
        json.addProperty("smallOreChance", smallOreChance);
        json.addProperty("markerOreChance", markerOreChance);
        json.addProperty("surfaceStoneChance", surfaceStoneChance);
        JsonArray array = new JsonArray();
        materials.forEach(m -> {
            array.add(m.toJson());
        });
        if (!array.isEmpty()) {
            json.add("materials", array);
        }
        return json;
    }

    public static WorldGenVeinVariant fromJson(JsonObject json){
        List<WorldGenVeinVariantMaterial> materials = new ArrayList<>();
        if (json.has("materials")){
            JsonArray array = json.getAsJsonArray("materials");
            array.forEach(j -> {
                if (j instanceof JsonObject object){
                    materials.add(WorldGenVeinVariantMaterial.fromJson(object));
                }
            });
        }
        return new WorldGenVeinVariant(json.get("weight").getAsInt(), json.get("oreChance").getAsFloat(), json.get("smallOreChance").getAsFloat(), json.get("markerOreChance").getAsFloat(), json.get("surfaceStoneChance").getAsFloat(), materials);
    }
}
