package muramasa.antimatter.worldgen.smallore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import muramasa.antimatter.material.Material;
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

public class WorldGenSmallOre extends WorldGenBase<WorldGenSmallOre> {
    public final Material material;
    public final int minY, maxY, amountPerChunk;
    public final List<ResourceLocation> dimensions;
    public final List<String> biomes;

    public final boolean biomeBlacklist;

    WorldGenSmallOre(String id, Material material, int minY, int maxY, int amountPerChunk, List<ResourceLocation> dimensions, List<String> biomes, boolean biomeBlacklist){
        super(id, WorldGenSmallOre.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.material = material;
        this.minY = minY;
        this.maxY = maxY;
        this.amountPerChunk = amountPerChunk;
        this.dimensions = dimensions;
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
        json.addProperty("material", material.getId());
        if (minY > Integer.MIN_VALUE) {
            json.addProperty("minY", minY);
        }
        if (maxY < Integer.MAX_VALUE) {
            json.addProperty("maxY", maxY);
        }
        json.addProperty("amountPerChunk", amountPerChunk);
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

    public static WorldGenSmallOre fromJson(String id, JsonObject json){
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
        return new WorldGenSmallOre(
                id,
                Material.get(json.get("material").getAsString()),
                json.get("amountPerChunk").getAsInt(),
                json.has("minY") ? json.get("minY").getAsInt() : Integer.MIN_VALUE,
                json.has("maxY") ? json.get("maxY").getAsInt() : Integer.MAX_VALUE,
                dims,
                biomes,
                json.get("biomeBlacklist").getAsBoolean());
    }
}
