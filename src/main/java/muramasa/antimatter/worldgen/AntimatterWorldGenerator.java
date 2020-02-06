package muramasa.antimatter.worldgen;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.feature.FeaturePurge;
import muramasa.antimatter.worldgen.feature.FeatureStoneLayer;
import muramasa.antimatter.worldgen.feature.FeatureVeinLayer;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Most of GTI WorldGen code is from the GTNewHorizons GT5 fork, refactored for 1.12 and somewhat optimised
 * Written in 1.7 by moronwmachinegun and mitchej123, adapted by Muramasa
 * **/
@Mod.EventBusSubscriber
public class AntimatterWorldGenerator {

    //private static HashMap<String, HashMap<String, Object>> DEFAULT_DATA = new HashMap<>();
    //private static ImmutableMap<String, Class<?>> TYPE = ImmutableMap.of("vein", WorldGenOreVein.class, "small", WorldGenOreSmall.class, "stone", WorldGenStone.class);

    public static Int2ObjectOpenHashMap<List<WorldGenBase<?>>> BASE = new Int2ObjectOpenHashMap<>();
    public static Int2ObjectOpenHashMap<List<WorldGenVeinLayer>> VEIN_LAYER = new Int2ObjectOpenHashMap<>();
    //public static Int2ObjectOpenHashMap<List<WorldGenStoneLayer>> STONE_LAYER = new Int2ObjectOpenHashMap<>();
    public static Int2ObjectOpenHashMap<List<WorldGenOreSmall>> ORE_SMALL = new Int2ObjectOpenHashMap<>();
    public static Int2ObjectOpenHashMap<List<WorldGenStone>> STONE_BLOB = new Int2ObjectOpenHashMap<>();

    public static List<StoneLayer> STONE_LAYERS = new ArrayList<>();
    public static Map<BlockState, BlockState> STATES_TO_PURGE = new HashMap<>();

    public static final FeaturePurge FEATURE_PURGE = new FeaturePurge();
    public static final FeatureVeinLayer FEATURE_VEIN_LAYER = new FeatureVeinLayer();
    public static final FeatureStoneLayer FEATURE_STONE_LAYER = new FeatureStoneLayer();

    public static void init() {
        try {
//            //Write default data
//            File defaultFile = new File(Ref.CONFIG, "WorldGenerationDefault.json");
//            if (!defaultFile.exists()) defaultFile.createNewFile();
//            BufferedWriter br = new BufferedWriter(new FileWriter(defaultFile));
//            Ref.GSON.toJson(REGISTRY, br);
//            br.close();
//
//            //Generate default data
//            String defaultData = new String(Files.readAllBytes(defaultFile.toPath()));
//            DEFAULT_DATA = Ref.GSON.fromJson(defaultData, new TypeToken<HashMap<String, HashMap>>(){}.getType());

            AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
            AntimatterWorldGenerator.reload();
            WorldGenHelper.init();
            //if (STATES_TO_PURGE.size() > 0) FEATURE_PURGE.init();
            if (VEIN_LAYER.size() > 0) FEATURE_VEIN_LAYER.init();
            if (STONE_LAYERS.size() > 0) FEATURE_STONE_LAYER.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
    }

    public static void reload() {
        try {
            Antimatter.LOGGER.info("AntimatterWorldGenerator: Started data rebuild!");
            //Clear compiled maps
            VEIN_LAYER.clear(); ORE_SMALL.clear(); STONE_BLOB.clear();

//            //Remove custom data
//            REGISTRY.forEach((k, v) -> v.entrySet().removeIf(e -> e.getValue().isCustom()));
//
//            //Inject default data
//            REGISTRY.forEach((k, v) -> DEFAULT_DATA.entrySet().stream().filter(e -> e.getKey().equals(k)).forEach(e -> {
//                e.getValue().forEach((i, j) -> v.get(i).onDataOverride((LinkedTreeMap) j));
//            }));
//
//            //Check for override data
//            File overrideFile = new File(Ref.CONFIG, "WorldGenerationOverride.json");
//            if (!overrideFile.exists()) overrideFile.createNewFile();
//            String jsonData = new String(Files.readAllBytes(overrideFile.toPath()));
//            HashMap<String, HashMap<String, Object>> dataMap = Ref.GSON.fromJson(jsonData, new TypeToken<HashMap<String, HashMap>>(){}.getType());
//            if (dataMap != null) {
//                //Inject override data
//                REGISTRY.forEach((k, v) -> dataMap.entrySet().stream().filter(e -> e.getKey().equals(k)).forEach(e -> {
//                    e.getValue().forEach((i, j) -> {
//                        if (v.containsKey(i)) v.get(i).onDataOverride((LinkedTreeMap) j);
//                        else v.put(i, ((WorldGenBase) Ref.GSON.fromJson(Ref.GSON.toJsonTree(j).getAsJsonObject(), TYPE.get(k))).asCustom());
//                    });
//                }));
//            }

            WorldGenVeinLayer.TOTAL_WEIGHT = 0;
            WorldGenVeinLayer.VALID_VEINS.clear();

            //Rebuild compiled maps
            AntimatterAPI.all(WorldGenBase.class).forEach(w -> w.getDimensions().forEach(d -> {
                BASE.computeIfAbsent((int) d, k -> new ArrayList<>()).add(w.build());
            }));
            AntimatterAPI.all(WorldGenVeinLayer.class).forEach(w -> w.getDimensions().forEach(d -> {
                VEIN_LAYER.computeIfAbsent((int) d, k -> new ArrayList<>()).add(w.build());
            }));
            AntimatterAPI.all(WorldGenOreSmall.class).forEach(w -> w.getDimensions().forEach(d -> {
                ORE_SMALL.computeIfAbsent((int) d, k -> new ArrayList<>()).add(w.build());
            }));
            AntimatterAPI.all(WorldGenStone.class).forEach(w -> w.getDimensions().forEach(d -> {
                STONE_BLOB.computeIfAbsent((int) d, k -> new ArrayList<>()).add(w.build());
            }));

            Antimatter.LOGGER.info("AntimatterWorldGenerator: Finished data rebuild!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while reloading");
        }
    }

//    /*@Override*/
//    public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator generator, AbstractChunkProvider provider) {
//        try {
//            XSTR rand = new XSTR(Math.abs(random.nextInt()) + 1);
//            BlockPos.Mutable pos = new BlockPos.Mutable();
//            BASE.getOrDefault(world.getDimension().getType().getId(), Collections.emptyList()).forEach(w -> {
//                w.generate(world, rand, chunkX * 16, chunkZ * 16, pos, null, generator, provider);
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}