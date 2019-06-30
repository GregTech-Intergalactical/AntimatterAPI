package muramasa.gtu.api.worldgen;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.util.XSTR;
import muramasa.gtu.loaders.WorldGenLoader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

/**
 * Most of GTI WorldGen code is from the GTNewHorizons GT5 fork, refactored for 1.12 and somewhat optimised
 * Written in 1.7 by moronwmachinegun and mitchej123, adapted by Muramasa
 * **/
public class GregTechWorldGenerator implements IWorldGenerator {

    private static File CONFIG_DIR;

    private static HashMap<String, Set<WorldGenBase>> WORLD_GEN_REGISTRY = new HashMap<>();
    private static HashMap<String, Type> TYPE_REGISTRY = new HashMap<>();

    public static Int2ObjectArrayMap<List<WorldGenOreLayer>> LAYER = new Int2ObjectArrayMap<>();
    private static Int2ObjectArrayMap<List<WorldGenOreSmall>> SMALL = new Int2ObjectArrayMap<>();
    private static Int2ObjectArrayMap<List<WorldGenStone>> STONE = new Int2ObjectArrayMap<>();

    static {
        WORLD_GEN_REGISTRY.put("worldgen_ore_layer", new HashSet<>());
        WORLD_GEN_REGISTRY.put("worldgen_ore_small", new HashSet<>());
        WORLD_GEN_REGISTRY.put("worldgen_stone", new HashSet<>());
        TYPE_REGISTRY.put("worldgen_ore_layer", new TypeToken<List<WorldGenOreLayer>>(){}.getType());
        TYPE_REGISTRY.put("worldgen_ore_small", new TypeToken<List<WorldGenOreSmall>>(){}.getType());
        TYPE_REGISTRY.put("worldgen_stone", new TypeToken<List<WorldGenStone>>(){}.getType());
    }

    public GregTechWorldGenerator(File file) {
        CONFIG_DIR = file;
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    public static void register(WorldGenBase worldGen) {
        if (worldGen instanceof WorldGenOreLayer) WORLD_GEN_REGISTRY.get("worldgen_ore_layer").add(worldGen);
        if (worldGen instanceof WorldGenOreSmall) WORLD_GEN_REGISTRY.get("worldgen_ore_small").add(worldGen);
        if (worldGen instanceof WorldGenStone) WORLD_GEN_REGISTRY.get("worldgen_stone").add(worldGen);
    }

    public static void handleJSON() {
        try {
            //Generate default data
            Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
            File defaultData = new File(CONFIG_DIR, "WorldGenerationDefault.json");
            if (!defaultData.exists()) CONFIG_DIR.createNewFile();
            BufferedWriter br = new BufferedWriter(new FileWriter(defaultData));
            gson.toJson(WORLD_GEN_REGISTRY, br);
            br.close();

            //Check for override data
            String jsonData = new String(Files.readAllBytes(new File(CONFIG_DIR, "WorldGenerationOverride.json").toPath()));
            HashMap<String, HashSet> dataMap = gson.fromJson(jsonData, new TypeToken<HashMap<String, HashSet>>(){}.getType());
            if (dataMap != null) { //Some override data is present, inject into WORLD_GEN_REGISTRY
                WORLD_GEN_REGISTRY.forEach((k, v) -> dataMap.entrySet().stream().filter(e -> e.getKey().equals(k)).forEach(e -> {
                    List<WorldGenBase> list = gson.fromJson(gson.toJsonTree(e.getValue()).getAsJsonArray(), TYPE_REGISTRY.get(k));
                    list.forEach(w -> {
                        v.remove(w); v.add(w);
                    });
                }));
            }

            WORLD_GEN_REGISTRY.get("worldgen_ore_layer").forEach(w -> w.getDimensions().forEach(d -> {
                if (w.isEnabled()) LAYER.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenOreLayer) w.build());
            }));
            WORLD_GEN_REGISTRY.get("worldgen_ore_small").forEach(w -> w.getDimensions().forEach(d -> {
                if (w.isEnabled()) SMALL.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenOreSmall) w.build());
            }));
            WORLD_GEN_REGISTRY.get("worldgen_stone").forEach(w -> w.getDimensions().forEach(d -> {
                if (w.isEnabled()) STONE.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenStone) w.build());
            }));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GregTechWorldGenerator caught an exception when handling json");
        }
    }

    public static List<WorldGenOreLayer> getLayers(int dimension) {
        return LAYER.get(dimension);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        try {
            XSTR rand = new XSTR(Math.abs(random.nextInt()) + 1);

            //Generate Stones and Small Ores
            if (STONE.size() > 0) {
                for (WorldGenStone stone : STONE.get(world.provider.getDimension())) {
                    stone.generate(world, rand, chunkX * 16, chunkZ * 16, generator, provider);
                }
            }
            if (SMALL.size() > 0) {
                for (WorldGenOreSmall small : SMALL.get(world.provider.getDimension())) {
                    small.generate(world, rand, chunkX * 16, chunkZ * 16, generator, provider);
                }
            }

            if (LAYER.size() > 0) {
                // Determine bounding box on how far out to check for ore veins affecting this chunk
                int westX = chunkX - (Ref.ORE_VEIN_MAX_SIZE / 16);
                int eastX = chunkX + (Ref.ORE_VEIN_MAX_SIZE / 16 + 1); // Need to add 1 since it is compared using a <
                int northZ = chunkZ - (Ref.ORE_VEIN_MAX_SIZE / 16);
                int southZ = chunkZ + (Ref.ORE_VEIN_MAX_SIZE / 16 + 1);

                // Search for oreVein seeds and add to the list;
                for (int x = westX; x < eastX; x++) {
                    for (int z = northZ; z < southZ; z++) {
                        if (((Math.abs(x) % 3) == 1) && ((Math.abs(z) % 3) == 1)) { //Determine if this X/Z is an oreVein seed
                            WorldGenOreLayer.worldGenFindVein(world, chunkX, chunkZ, x, z, generator, provider);
                        }
                    }
                }

                if (world.provider.getDimension() == Ref.END || world.provider.getDimension() == Ref.ASTEROIDS) {
                    WorldGenLoader.ASTEROID_GEN.generate(world, rand, chunkX, chunkZ, generator, provider);
                }

                //if (Ref.debugWorldGen) GregTech.LOGGER.info("Oregen took " + (oreGenTime - leftOverTime) + " Leftover gen took " + (leftOverTime - startTime) + " Worldgen took " + duration + " ns");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}