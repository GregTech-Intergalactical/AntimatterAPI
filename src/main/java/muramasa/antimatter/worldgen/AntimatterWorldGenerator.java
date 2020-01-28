package muramasa.antimatter.worldgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.internal.LinkedTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.util.XSTR;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Most of GTI WorldGen code is from the GTNewHorizons GT5 fork, refactored for 1.12 and somewhat optimised
 * Written in 1.7 by moronwmachinegun and mitchej123, adapted by Muramasa
 * **/
@Mod.EventBusSubscriber
public class AntimatterWorldGenerator /*implements IWorldGenerator*/ {

    private static HashMap<String, HashMap<String, WorldGenBase>> REGISTRY = new HashMap<>();
    private static HashMap<String, HashMap<String, Object>> DEFAULT_DATA = new HashMap<>();
    private static ImmutableMap<String, Class<?>> TYPE = ImmutableMap.of("vein", WorldGenOreVein.class, "small", WorldGenOreSmall.class, "stone", WorldGenStone.class);

    private static Int2ObjectOpenHashMap<List<WorldGenBase>> BASE = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectOpenHashMap<List<WorldGenOreVein>> LAYER = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectOpenHashMap<List<WorldGenOreSmall>> SMALL = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectOpenHashMap<List<WorldGenStone>> STONE = new Int2ObjectOpenHashMap<>();

    static {
        //veins, singles, stones
        REGISTRY.put("vein", new HashMap<>());
        REGISTRY.put("small", new HashMap<>());
        REGISTRY.put("stone", new HashMap<>());
        REGISTRY.put("base", new HashMap<>());
    }

    public AntimatterWorldGenerator() {
        //TODO
        //GameRegistry.registerWorldGenerator(this, Integer.MAX_VALUE);
    }

    public static void register(WorldGenBase worldGen) {
        //TODO use a prefix to determine type and allow filtering world gen objects
        if (worldGen instanceof WorldGenOreVein) REGISTRY.get("vein").put(worldGen.getId(), worldGen);
        else if (worldGen instanceof WorldGenOreSmall) REGISTRY.get("small").put(worldGen.getId(), worldGen);
        else if (worldGen instanceof WorldGenStone) REGISTRY.get("stone").put(worldGen.getId(), worldGen);
        else REGISTRY.get("base").put(worldGen.getId(), worldGen);
    }

    public static void init() {
        try {
            //Write default data
            File defaultFile = new File(Ref.CONFIG, "WorldGenerationDefault.json");
            if (!defaultFile.exists()) defaultFile.createNewFile();
            BufferedWriter br = new BufferedWriter(new FileWriter(defaultFile));
            Ref.GSON.toJson(REGISTRY, br);
            br.close();

            //Generate default data
            String defaultData = new String(Files.readAllBytes(defaultFile.toPath()));
            DEFAULT_DATA = Ref.GSON.fromJson(defaultData, new TypeToken<HashMap<String, HashMap>>(){}.getType());

            WorldGenHelper.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
    }

    public static void reload() {
        try {
            Antimatter.LOGGER.info("AntimatterWorldGenerator: Started data rebuild!");
            //Clear compiled maps
            LAYER.clear(); SMALL.clear(); STONE.clear();

            //Remove custom data
            REGISTRY.forEach((k, v) -> v.entrySet().removeIf(e -> e.getValue().isCustom()));

            //Inject default data
            REGISTRY.forEach((k, v) -> DEFAULT_DATA.entrySet().stream().filter(e -> e.getKey().equals(k)).forEach(e -> {
                e.getValue().forEach((i, j) -> v.get(i).onDataOverride((LinkedTreeMap) j));
            }));

            //Check for override data
            File overrideFile = new File(Ref.CONFIG, "WorldGenerationOverride.json");
            if (!overrideFile.exists()) overrideFile.createNewFile();
            String jsonData = new String(Files.readAllBytes(overrideFile.toPath()));
            HashMap<String, HashMap<String, Object>> dataMap = Ref.GSON.fromJson(jsonData, new TypeToken<HashMap<String, HashMap>>(){}.getType());
            if (dataMap != null) {
                //Inject override data
                REGISTRY.forEach((k, v) -> dataMap.entrySet().stream().filter(e -> e.getKey().equals(k)).forEach(e -> {
                    e.getValue().forEach((i, j) -> {
                        if (v.containsKey(i)) v.get(i).onDataOverride((LinkedTreeMap) j);
                        else v.put(i, ((WorldGenBase) Ref.GSON.fromJson(Ref.GSON.toJsonTree(j).getAsJsonObject(), TYPE.get(k))).asCustom());
                    });
                }));
            }

            WorldGenOreVein.TOTAL_WEIGHT = 0;
            WorldGenOreVein.VALID_VEINS.clear();

            //Rebuild compiled maps
            REGISTRY.get("vein").values().stream().filter(WorldGenBase::isEnabled).forEach(w -> w.getDimensions().forEach(d -> {
                LAYER.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenOreVein) w.build());
            }));
            REGISTRY.get("small").values().stream().filter(WorldGenBase::isEnabled).forEach(w -> w.getDimensions().forEach(d -> {
                SMALL.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenOreSmall) w.build());
                BASE.computeIfAbsent(d, k -> new ArrayList<>()).add(w.build());
            }));
            REGISTRY.get("stone").values().stream().filter(WorldGenBase::isEnabled).forEach(w -> w.getDimensions().forEach(d -> {
                STONE.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenStone) w.build());
                BASE.computeIfAbsent(d, k -> new ArrayList<>()).add(w.build());
            }));
            REGISTRY.get("base").values().stream().filter(WorldGenBase::isEnabled).forEach(w -> w.getDimensions().forEach(d -> {
                BASE.computeIfAbsent(d, k -> new ArrayList<>()).add(w.build());
            }));
            Antimatter.LOGGER.info("AntimatterWorldGenerator: Finished data rebuild!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while reloading");
        }
    }

    public static List<WorldGenOreVein> getVeins(int dimension) {
        return LAYER.get(dimension);
    }

    public static List<WorldGenOreSmall> getSmalls(int dimension) {
        return SMALL.get(dimension);
    }

    public static List<WorldGenStone> getStones(int dimension) {
        return STONE.get(dimension);
    }

    /*@Override*/
    public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator generator, AbstractChunkProvider provider) {
        try {
            XSTR rand = new XSTR(Math.abs(random.nextInt()) + 1);
            BlockPos.Mutable pos = new BlockPos.Mutable();
            List<WorldGenBase> worldGenObjects = BASE.get(world.getDimension().getType().getId());
            if (worldGenObjects != null && worldGenObjects.size() > 0) {
                worldGenObjects.forEach(o -> o.generate(world, rand, chunkX * 16, chunkZ * 16, pos, null, generator, provider));
            }

            if (LAYER.size() > 0) {
                // Determine bounding box on how far out to check for ore veins affecting this chunk
                int westX = chunkX - (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16);
                int eastX = chunkX + (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16 + 1); // Need to add 1 since it is compared using a <
                int northZ = chunkZ - (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16);
                int southZ = chunkZ + (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16 + 1);

                // Search for oreVein seeds and add to the list;
                for (int x = westX; x < eastX; x++) {
                    for (int z = northZ; z < southZ; z++) {
                        if (((Math.abs(x) % 3) == 1) && ((Math.abs(z) % 3) == 1)) { //Determine if this X/Z is an oreVein seed
                            WorldGenOreVein.generate(world, chunkX, chunkZ, x, z, pos, null);
                        }
                    }
                }
                //if (Ref.debugWorldGen) GregTech.LOGGER.info("Oregen took " + (oreGenTime - leftOverTime) + " Leftover gen took " + (leftOverTime - startTime) + " Worldgen took " + duration + " ns");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}