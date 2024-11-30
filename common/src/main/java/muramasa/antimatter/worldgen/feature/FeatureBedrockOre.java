package muramasa.antimatter.worldgen.feature;

import com.mojang.serialization.Codec;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.bedrockore.WorldGenBedrockVein;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;
import java.util.Random;

import static muramasa.antimatter.data.AntimatterMaterialTypes.BEARING_ROCK;

public class FeatureBedrockOre extends AntimatterFeature<NoneFeatureConfiguration>{

    public FeatureBedrockOre() {
        super(NoneFeatureConfiguration.CODEC, WorldGenBedrockVein.class);
    }

    @Override
    public String getId() {
        return "bedrock_veins";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.BEDROCK_VEINS.get() && !getRegistry().isEmpty();
    }

    @Override
    public void init() {

    }

    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.BEDROCK_VEINS);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctxt) {
        WorldGenLevel world = ctxt.level();
        BlockPos pos = ctxt.origin();
        Random rand = ctxt.random();

        List<WorldGenBedrockVein> veins = AntimatterWorldGenerator.all(WorldGenBedrockVein.class, world.getLevel().dimension());
        if (veins.isEmpty()) return false;
        for (WorldGenBedrockVein vein : veins) {
            if(generateBedrockVein(vein, world, pos.getX(), pos.getZ(), pos.getX() + 16, pos.getZ() + 16, rand)){
                return true;
            }
        }
        return false;
    }

    public static boolean generateBedrockVein(WorldGenBedrockVein vein, LevelAccessor level, int minX, int minZ, int maxX, int maxZ, Random random) {
        if (random.nextInt(vein.probability) != 0) return false;
        if (!generateVein(vein.material, level, minX, minZ, random)) return false;


        if ((vein.indicatorRocks || vein.indicatorFlowers)) {
            boolean tFlowers = vein.indicatorFlowers && vein.flower != Blocks.AIR, tRocks = vein.indicatorRocks && vein.material.has(BEARING_ROCK);


            // Generate first an 8x8 of 4, then a 16x16 of 8, and at the end a 32x32 of 16 Rocks/Flowers. That way the Pattern gets denser in the middle, and Chunk Boundary Issues of GalactiCraft wont be as terrible.
            for (int tD = 4; tD <= 16; tD *= 2) {
                try {
                    for (int i = 0; i < tD; i++) {
                        int tX = minX+random.nextInt(tD*2)+8-tD, tZ = minZ+random.nextInt(tD*2)+8-tD;
                        int y = Math.min(level.getHeight(Heightmap.Types.OCEAN_FLOOR, tX, tZ), level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, tX, tZ));
                        BlockPos offset = new BlockPos(tX, y, tZ);
                        BlockState below = level.getBlockState(new BlockPos(tX, y - 1, tZ));
                        if (tFlowers && (!tRocks || random.nextInt(4) > 0) && vein.flower.canSurvive(vein.flower.defaultBlockState(), level, offset)){
                            level.setBlock(offset, vein.flower.defaultBlockState(), 0);
                        } else if (tRocks){
                            if (!below.isAir() && below != WorldGenHelper.WATER_STATE && AntimatterConfig.STONE_LAYER_ROCKS.get() && AntimatterConfig.SURFACE_ROCKS.get()) {
                                WorldGenHelper.setRock(level, offset, vein.material, Blocks.DEEPSLATE.defaultBlockState(), 1);
                            }
                        }
                    }
                } catch(Throwable e) {
                    Antimatter.LOGGER.error(e);
                }
            }
        }

        return true;
    }

    public static boolean generateVein(Material material, LevelAccessor level, int minX, int minZ, Random random) {
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
                switch (random.nextInt(6)) {
                    case 0 -> WorldGenHelper.setOre(level, new BlockPos(minX + tX, tY - yOffset, minZ + tZ), material, AntimatterMaterialTypes.ORE);
                    case 1, 2 -> WorldGenHelper.setOre(level, new BlockPos(minX + tX, tY - yOffset, minZ + tZ), material, AntimatterMaterialTypes.ORE_SMALL);
                }
            }

            for (int i = 5+random.nextInt(3); i-->0;) {
                int tX = 5+random.nextInt(6), tZ = 5+random.nextInt(6), tW = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, minX + tX, minZ + tZ);

                for (int tY = tD1.length - yOffset; tY < tW; tY++) {
                    switch(random.nextInt(7)) {
                        case 0 -> {
                            tX++;
                            tW = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, minX + tX, minZ + tZ);
                        }
                        case 1 -> {
                            tX--;
                            tW = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, minX + tX, minZ + tZ);
                        }
                        case 2 -> {
                            tZ++;
                            tW = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, minX + tX, minZ + tZ);
                        }
                        case 3 -> {
                            tZ--;
                            tW = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, minX + tX, minZ + tZ);
                        }
                    }
                    if (tY >= tW) break;
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
