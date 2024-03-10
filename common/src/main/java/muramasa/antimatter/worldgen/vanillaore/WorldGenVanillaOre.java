package muramasa.antimatter.worldgen.vanillaore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WorldGenVanillaOre extends WorldGenBase<WorldGenVanillaOre> {
    public final Material primary, secondary;
    public final MaterialType<?> materialType, secondaryType;
    public final int minY, maxY, weight, size, plateau, probability;
    public final float secondaryChance, discardOnExposureChance;
    public final List<String> biomes;

    public final boolean biomeBlacklist, triangle, spawnOnOceanFloor;

    WorldGenVanillaOre(String id, Material primary, Material secondary, MaterialType<?> type, MaterialType<?> secondaryType, float secondaryChance, float discardOnExposureChance, int minY, int maxY, int weight, int size, int probability, boolean triangle, int plateau, boolean spawnOnOceanFlor, List<ResourceLocation> dimensions, List<String> biomes, boolean biomeBlacklist){
        super(id, WorldGenVanillaOre.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.primary = primary;
        this.secondary = secondary;
        this.materialType = type;
        this.secondaryType = secondaryType;
        this.secondaryChance = secondaryChance;
        this.discardOnExposureChance = discardOnExposureChance;
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.size = size;
        this.probability = probability;
        this.triangle = triangle;
        this.plateau = plateau;
        this.spawnOnOceanFloor = spawnOnOceanFlor;
        this.biomes = biomes;
        this.biomeBlacklist = biomeBlacklist;

    }

    @Override
    public Predicate<Holder<Biome>> getValidBiomes() {
        return b -> {
            if (biomes.isEmpty()) return biomeBlacklist;
            Predicate<String> predicate = s -> {
                if (s.contains("#")) return b.is(TagUtils.getBiomeTag(new ResourceLocation(s.replace("#", ""))));
                return b.is(ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(s)));
            };
            return biomeBlacklist ? biomes.stream().anyMatch(predicate) : biomes.stream().noneMatch(predicate);

        };
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("primary", primary.getId());
        if (materialType != AntimatterMaterialTypes.ORE) json.addProperty("materialType", materialType.getId());
        if (secondary != Material.NULL) {
            json.addProperty("secondary", secondary.getId());
            json.addProperty("secondaryChance", secondaryChance);
            if (secondaryType != materialType) json.addProperty("secondaryType", secondaryType.getId());
        }
        json.addProperty("discardOnExposureChance", discardOnExposureChance);
        if (minY > Integer.MIN_VALUE) {
            json.addProperty("minY", minY);
        }
        if (maxY < Integer.MAX_VALUE) {
            json.addProperty("maxY", maxY);
        }
        json.addProperty("weight", weight);
        json.addProperty("size", size);
        json.addProperty("probability", probability);
        if (triangle) json.addProperty("triangle", true);
        if (plateau > 0) json.addProperty("plateau", plateau);
        if (spawnOnOceanFloor) json.addProperty("spawnOnOceanFloor", true);
        JsonArray array = new JsonArray();
        getDimensions().forEach(r -> array.add(r.toString()));
        if (!array.isEmpty()){
            json.add("dims", array);
        }
        JsonArray array2 = new JsonArray();
        biomes.forEach(array2::add);
        if (!array2.isEmpty()){
            json.add("biomes", array2);
        }
        json.addProperty("biomeBlacklist", biomeBlacklist);
        return json;
    }

    public static WorldGenVanillaOre fromJson(String id, JsonObject json){
        List<String> biomes = new ArrayList<>();
        List<ResourceLocation> dims = new ArrayList<>();
        if (json.has("biomes")){
            JsonArray array = json.getAsJsonArray("biomes");
            array.forEach(j -> {
                if (j instanceof JsonPrimitive object){
                    biomes.add(object.getAsString());
                }
            });
        }
        if (json.has("dims")){
            JsonArray array = json.getAsJsonArray("dims");
            array.forEach(j -> {
                if (j instanceof JsonPrimitive object){
                    dims.add(new ResourceLocation(object.getAsString()));
                }
            });
        }
        MaterialType<?> materialType = json.has("materialType") ? AntimatterAPI.get(MaterialType.class, json.get("materialType").getAsString()) : AntimatterMaterialTypes.ORE;
        return new WorldGenVanillaOre(
                id,
                Material.get(json.get("primary").getAsString()),
                json.has("secondary") ? Material.get(json.get("secondary").getAsString()) : Material.NULL,
                materialType,
                json.has("secondaryType") ? AntimatterAPI.get(MaterialType.class, json.get("secondaryType").getAsString()) : materialType,
                json.has("secondaryChance") ? json.get("secondaryChance").getAsFloat() : 0.0f,
                json.get("discardOnExposureChance").getAsFloat(),
                json.has("minY") ? json.get("minY").getAsInt() : Integer.MIN_VALUE,
                json.has("maxY") ? json.get("maxY").getAsInt() : Integer.MAX_VALUE,
                json.get("weight").getAsInt(),
                json.get("size").getAsInt(),
                json.get("probability").getAsInt(),
                json.has("triangle") && json.get("triangle").getAsBoolean(),
                json.has("plateau") ? json.get("plateau").getAsInt() : 0,
                json.has("spawnOnOceanFloor") && json.get("spawnOnOceanFloor").getAsBoolean(),
                dims,
                biomes,
                json.get("biomeBlacklist").getAsBoolean());
    }
}
