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

    public static boolean generateVein(Material material, LevelAccessor level, int dimType, int minX, int minZ, Random random) {
        try {
            Block tStone = level.getBlockState(new BlockPos(minX+8, level.getMinBuildHeight(), minZ+8)).getBlock();
            // Requires existing Bedrock!
            if (tStone != Blocks.BEDROCK) return false;
            // Generate the bedrock Ore Blocks.
            for (int tX = 5; tX < 11; tX++) {
                for (int tZ = 5; tZ < 11; tZ++) {
                    switch (random.nextInt(6)) {
                        case 0 -> WorldGenHelper.setState(level, new BlockPos(minX + tX, level.getMinBuildHeight(), minZ + tZ), AntimatterMaterialTypes.ORE.get().get(material, AntimatterStoneTypes.BEDROCK).asState());
                        case 1, 2 -> WorldGenHelper.setState(level, new BlockPos(minX + tX, level.getMinBuildHeight(), minZ + tZ), AntimatterMaterialTypes.ORE_SMALL.get().get(material, AntimatterStoneTypes.BEDROCK).asState());
                    }
                }
            }
            // At least one Ore Block must be there. So force place a large one somewhere in the Center.
            WorldGenHelper.setState(level, new BlockPos(minX + 6 + random.nextInt(4), level.getMinBuildHeight(), minZ + 6 + random.nextInt(4)), AntimatterMaterialTypes.ORE.get().get(material, AntimatterStoneTypes.BEDROCK).asState());
            // Use Deepslate if available, except in the Nether.
            tStone = Blocks.DEEPSLATE;
            int yOffset = level.getMinBuildHeight() < 0 ? Math.abs(level.getMinBuildHeight()) : -level.getMinBuildHeight();
            // Keep Distances within the Chunk for this important step.
            int[] tD1 = new int[] { 5,  4,  2,  1,  0,  2,  5};
            int[] tD2 = new int[] {11, 12, 14, 15, 16, 14, 11};
            // Portion a Muffin shaped Ore Blob around the Bedrock Spot.
            for (int tY = 1; tY < tD1.length; tY++) for (int tX = tD1[tY]; tX < tD2[tY]; tX++) for (int tZ = tD1[tY]; tZ < tD2[tY]; tZ++) {
                level.setBlock(new BlockPos(minX + tX, tY - yOffset, minZ + tZ), tStone.defaultBlockState(), 0);
                /*if (GENERATED_NO_BEDROCK_ORE) {
                    level.setBlock(minX+tX, tY, minZ+tZ, tStone, 0, 0);
                } else {
                    WD.removeBedrock(level, minX+tX, tY, minZ+tZ);
                }*/
                switch (random.nextInt(6)) {
                    case 0 -> WorldGenHelper.setOre(level, new BlockPos(minX + tX, tY - yOffset, minZ + tZ), material, AntimatterMaterialTypes.ORE);
                    case 1, 2 -> WorldGenHelper.setOre(level, new BlockPos(minX + tX, tY - yOffset, minZ + tZ), material, AntimatterMaterialTypes.ORE_SMALL);
                }
            }

            for (int i = 5+random.nextInt(3); i-->0;) {
                int tX = 5+random.nextInt(6), tZ = 5+random.nextInt(6), tW = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, tX, tZ);

                for (int tY = tD1.length - yOffset; tY < tW; tY++) {
                    switch(random.nextInt(7)) {case 0: tX++; break; case 1: tX--; break; case 2: tZ++; break; case 3: tZ--; break;}
                    if (tX <= 0 || tX >= 15 || tZ <= 0 || tZ >= 15) {
                        WorldGenHelper.setOre(level, new BlockPos(minX + tX, tY, minZ + tZ), material, AntimatterMaterialTypes.ORE_SMALL);
                        break;
                    } else if (random.nextInt(3) != 0) {
                        WorldGenHelper.setOre(level, new BlockPos(minX + tX, tY, minZ + tZ), material, AntimatterMaterialTypes.ORE_SMALL);
                    }
                }
            }

            return true;
        } catch(Throwable e) {
            Antimatter.LOGGER.error(e);
        }
        return false;
    }
}
