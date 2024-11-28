package muramasa.antimatter.worldgen.bedrockore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import muramasa.antimatter.worldgen.vanillaore.WorldGenVanillaOre;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenBedrockVein extends WorldGenBase<WorldGenBedrockVein> {
    public final int probability;
    public final Material material;
    public final boolean indicatorRocks, indicatorFlowers;
    public final Block flower;

    private WorldGenBedrockVein(String id, int probability, Material material, boolean indicatorRocks, boolean indicatorFlowers, Block flower, List<ResourceLocation> dimensions) {
        super(id, WorldGenBedrockVein.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());
        this.probability = probability;
        this.material = material;
        this.indicatorRocks = indicatorRocks;
        this.indicatorFlowers = indicatorFlowers;
        this.flower = flower;
    }

    public static WorldGenBedrockVein create(String id, int probability, Material material, boolean indicatorRocks, boolean indicatorFlowers, Block flower, ResourceLocation... dimensions) {
        WorldGenBedrockVein vein = new WorldGenBedrockVein(id, probability, material, indicatorRocks, indicatorFlowers, flower, List.of(dimensions));
        AntimatterWorldGenerator.writeJson(vein.toJson(), vein.getId(), "bedrock_veins");
        return AntimatterWorldGenerator.readJson(WorldGenBedrockVein.class, vein, WorldGenBedrockVein::fromJson, "bedrock_veins");
    }

    public static WorldGenBedrockVein create(String id, int probability, Material material, ResourceLocation... dimensions) {
        return create(id, probability, material, true, dimensions);
    }

    public static WorldGenBedrockVein create(String id, int probability, Material material, boolean indicatorRocks, ResourceLocation... dimensions) {
        return create(id, probability, material, indicatorRocks, false, Blocks.AIR, dimensions);
    }

    public static WorldGenBedrockVein create(String id, int probability, Material material, boolean indicatorRocks, Block flower, ResourceLocation... dimensions) {
        return create(id, probability, material, indicatorRocks, true, flower, dimensions);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("probability", probability);
        jsonObject.addProperty("material", material.getId());
        jsonObject.addProperty("indicatorRocks", indicatorRocks);
        jsonObject.addProperty("indicatorFlowers", indicatorFlowers);
        if (flower != null && flower != Blocks.AIR){
            jsonObject.addProperty("flower", AntimatterPlatformUtils.INSTANCE.getIdFromBlock(flower).toString());
        }
        JsonArray array = new JsonArray();
        getDimensions().forEach(r -> array.add(r.toString()));
        if (!array.isEmpty()){
            jsonObject.add("dims", array);
        }
        return jsonObject;
    }

    public static WorldGenBedrockVein fromJson(String id, JsonObject jsonObject) {
        List<ResourceLocation> dims = new ArrayList<>();
        if (jsonObject.has("dims")){
            JsonArray array = jsonObject.getAsJsonArray("dims");
            array.forEach(j -> {
                if (j instanceof JsonPrimitive object){
                    dims.add(new ResourceLocation(object.getAsString()));
                }
            });
        }
        return new WorldGenBedrockVein(
                id,
                jsonObject.get("probability").getAsInt(),
                Material.get(jsonObject.get("material").getAsString()),
                jsonObject.get("indicatorRocks").getAsBoolean(),
                jsonObject.get("indicatorFlowers").getAsBoolean(),
                jsonObject.has("flower") ? AntimatterPlatformUtils.INSTANCE.getBlockFromId(new ResourceLocation(jsonObject.get("flower").getAsString())) : Blocks.AIR,
                dims
        );
    }


}
