package muramasa.antimatter.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.random.RandomGenerator;

@Accessors(chain = true)
public class StoneLayerOre {

    @Getter
    private final Material material;
    @Getter
    private StoneType stoneType;
    @Getter
    private BlockState oreState, oreSmallState;
    @Getter
    private final long chance;
    @Getter
    private final int minY;
    @Getter
    private final int maxY;
    private final List<String> filteredBiomes = new ArrayList<>();
    @Getter
    @Setter
    private boolean filteredBiomesBlacklist = false;

    public StoneLayerOre(Material material, long chance, int minY, int maxY) {
        this.material = material;
        this.chance = bind(1, Ref.U, chance);
        this.minY = minY;
        this.maxY = maxY;
    }

    public StoneLayerOre setStatesByStoneType(StoneType stoneType) {
        this.oreState = AntimatterMaterialTypes.ORE.get().get(material, stoneType).asState();
        this.oreSmallState = AntimatterMaterialTypes.ORE_SMALL.get().get(material, stoneType).asState();
        this.stoneType = stoneType;
        return this;
    }

    private StoneLayerOre addFilteredBiome(String biomeID){
        if (!filteredBiomes.contains(biomeID)) {
            filteredBiomes.add(biomeID);
        }
        return this;
    }

    public StoneLayerOre addFilteredBiome(ResourceKey<Biome> biome){
        return addFilteredBiome(biome.location().toString());
    }

    public StoneLayerOre addFilteredBiome(TagKey<Biome> biomeTagKey){
        return addFilteredBiome("#" + biomeTagKey.location());
    }

    public boolean canPlace(BlockPos pos, RandomSource rand, LevelAccessor world) {
        Holder<Biome> biome = world.getBiome(pos);
        boolean failed = !filteredBiomesBlacklist;
        if (!filteredBiomes.isEmpty()){
            for (String filteredBiome : filteredBiomes) {
                BiPredicate<String, Holder<Biome>> predicate = (s, biomeHolder) -> {
                    if (s.startsWith("#")){
                        TagKey<Biome> compare = TagUtils.getBiomeTag(new ResourceLocation(filteredBiome.replace("#", "")));
                        return biomeHolder.is(compare);
                    } else {
                        ResourceKey<Biome> compare = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(filteredBiome));
                        return biomeHolder.is(compare);
                    }
                };
                if (predicate.test(filteredBiome, biome)){
                    failed = filteredBiomesBlacklist;
                    break;
                }
            }
            if (failed) return false;
        }
        return pos.getY() >= minY && pos.getY() <= maxY && boundedNextLong(rand, Ref.U) < chance;
    }

    public static long boundedNextLong(RandomSource rng, long bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }
        // Specialize boundedNextLong for origin == 0, bound > 0
        final long m = bound - 1;
        long r = rng.nextLong();
        if ((bound & m) == 0L) {
            // The bound is a power of 2.
            r &= m;
        } else {
            // Must reject over-represented candidates
            /* This loop takes an unlovable form (but it works):
               because the first candidate is already available,
               we need a break-in-the-middle construction,
               which is concisely but cryptically performed
               within the while-condition of a body-less for loop. */
            for (long u = r >>> 1;
                 u + m - (r = u % bound) < 0L;
                 u = rng.nextLong() >>> 1)
                ;
        }
        return r;
    }

    public static long bind(long min, long max, long boundValue) {
        return min > max ? Math.max(max, Math.min(min, boundValue)) : Math.max(min, Math.min(max, boundValue));
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("chance", chance);
        json.addProperty("material", material.getId());
        json.addProperty("filteredBiomesBlacklist", filteredBiomesBlacklist);
        if (minY > Integer.MIN_VALUE) {
            json.addProperty("minY", minY);
        }
        if (maxY < Integer.MAX_VALUE) {
            json.addProperty("maxY", maxY);
        }
        if (stoneType != null){
            json.addProperty("stoneType", stoneType.getId());
        }
        if (!filteredBiomes.isEmpty()){
            JsonArray array = new JsonArray();
            filteredBiomes.forEach(array::add);
            json.add("filteredBiomes", array);
        }
        return json;
    }

    public static StoneLayerOre fromJson(JsonObject json){
        StoneType stoneType = null;
        if (json.has("stoneType")){
            stoneType = StoneType.get(json.get("stoneType").getAsString());
            if (stoneType == null) throw new IllegalStateException("stone type: " + json.get("stoneType").getAsString() + " does not exist!");
        }
        StoneLayerOre  stoneLayerOre = new StoneLayerOre(
                Material.get(json.get("material").getAsString()),
                json.get("chance").getAsLong(),
                json.has("minY") ? json.get("minY").getAsInt() : Integer.MIN_VALUE,
                json.has("maxY") ? json.get("maxY").getAsInt() : Integer.MAX_VALUE);
        if (stoneType != null){
            stoneLayerOre.setStatesByStoneType(stoneType);
        }
        if (json.has("filteredBiomesBlacklist")){
            stoneLayerOre.setFilteredBiomesBlacklist(json.get("filteredBiomesBlacklist").getAsBoolean());
        }
        if (json.has("filteredBiomes")){
            JsonArray array = json.getAsJsonArray("filteredBiomes");
            array.forEach(j -> stoneLayerOre.addFilteredBiome(j.getAsString()));
        }
        return stoneLayerOre;
    }
}
