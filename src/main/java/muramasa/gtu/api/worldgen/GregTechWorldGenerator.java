package muramasa.gtu.api.worldgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.internal.LinkedTreeMap;
import exterminatorjeff.undergroundbiomes.api.event.UBForceReProcessEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.util.XSTR;
import muramasa.gtu.loaders.WorldGenLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
public class GregTechWorldGenerator implements IWorldGenerator {

    private static HashMap<String, HashMap<String, WorldGenBase>> REGISTRY = new HashMap<>();
    private static HashMap<String, HashMap<String, Object>> DEFAULT_DATA = new HashMap<>();
    private static ImmutableMap<String, Class<?>> TYPE = ImmutableMap.of("vein", WorldGenOreVein.class, "small", WorldGenOreSmall.class, "stone", WorldGenStone.class);

    private static Int2ObjectArrayMap<List<WorldGenOreVein>> LAYER = new Int2ObjectArrayMap<>();
    private static Int2ObjectArrayMap<List<WorldGenOreSmall>> SMALL = new Int2ObjectArrayMap<>();
    private static Int2ObjectArrayMap<List<WorldGenStone>> STONE = new Int2ObjectArrayMap<>();

    static {
        //veins, singles, stones
        REGISTRY.put("vein", new HashMap<>());
        REGISTRY.put("small", new HashMap<>());
        REGISTRY.put("stone", new HashMap<>());
    }

    public GregTechWorldGenerator() {
        GameRegistry.registerWorldGenerator(this, Integer.MAX_VALUE);
    }

    public static void register(WorldGenBase worldGen) {
        if (worldGen instanceof WorldGenOreVein) REGISTRY.get("vein").put(worldGen.getId(), worldGen);
        if (worldGen instanceof WorldGenOreSmall) REGISTRY.get("small").put(worldGen.getId(), worldGen);
        if (worldGen instanceof WorldGenStone) REGISTRY.get("stone").put(worldGen.getId(), worldGen);
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
            throw new RuntimeException("GregTechWorldGenerator caught an exception while initializing");
        }
    }

    public static void reload() {
        try {
            GregTech.LOGGER.info("GregTechWorldGenerator: Started data rebuild!");
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
            }));
            REGISTRY.get("stone").values().stream().filter(WorldGenBase::isEnabled).forEach(w -> w.getDimensions().forEach(d -> {
                STONE.computeIfAbsent(d, k -> new ArrayList<>()).add((WorldGenStone) w.build());
            }));
            GregTech.LOGGER.info("GregTechWorldGenerator: Finished data rebuild!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GregTechWorldGenerator caught an exception while reloading");
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPopulateChunkPost(PopulateChunkEvent.Post e) {
        handleOres(e.getRand(), e.getChunkX(), e.getChunkZ(), e.getWorld());
        MinecraftForge.EVENT_BUS.post(new UBForceReProcessEvent(e.getGenerator(), e.getWorld(), e.getRand(), e.getChunkX(), e.getChunkZ(), false));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        try {
            XSTR rand = new XSTR(Math.abs(random.nextInt()) + 1);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            int rockAmount = 4;
            int passedX = chunkX * 16;
            int passedZ = chunkZ * 16;
            int j = Math.max(1, rockAmount + rand.nextInt(rockAmount));
            for (int i = 0; i < j; i++) {
                pos.setPos(passedX + 8 + rand.nextInt(16), 0, passedZ + 8 + rand.nextInt(16));
                pos.setY(world.getHeight(pos.getX(), pos.getZ()) - 1);
                WorldGenHelper.setRock(world, pos, Materials.NULL);
            }

            //Generate Stones and Small Ores
            if (STONE.size() > 0) {
                for (WorldGenStone stone : STONE.get(world.provider.getDimension())) {
                    stone.generate(world, rand, chunkX * 16, chunkZ * 16, pos, null, generator, provider);
                }
            }
            if (SMALL.size() > 0) {
                for (WorldGenOreSmall small : SMALL.get(world.provider.getDimension())) {
                    small.generate(world, rand, chunkX * 16, chunkZ * 16, pos, null, generator, provider);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleOres(Random random, int chunkX, int chunkZ, World world) {
        XSTR rand = new XSTR(Math.abs(random.nextInt()) + 1);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

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
                        WorldGenOreVein.generate(world, chunkX, chunkZ, x, z, pos, null);
                    }
                }
            }

            if (world.provider.getDimension() == Ref.END || world.provider.getDimension() == Ref.ASTEROIDS) {
                WorldGenLoader.ASTEROID_GEN.generate(world, rand, chunkX, chunkZ, pos, null, null, null);
            }

            //if (Ref.debugWorldGen) GregTech.LOGGER.info("Oregen took " + (oreGenTime - leftOverTime) + " Leftover gen took " + (leftOverTime - startTime) + " Worldgen took " + duration + " ns");
        }
    }
}