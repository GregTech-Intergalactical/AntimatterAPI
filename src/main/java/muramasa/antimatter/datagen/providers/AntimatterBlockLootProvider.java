package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.BlockPipe;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import static muramasa.antimatter.Ref.GSON;

public class AntimatterBlockLootProvider implements IDataProvider, IAntimatterProvider {
    protected final String providerDomain, providerName;
    private final DataGenerator generator;
    private final Map<Block, Function<Block, LootTable.Builder>> tables = new Object2ObjectOpenHashMap<>();


    public AntimatterBlockLootProvider(String providerDomain, String providerName, DataGenerator gen) {
        generator = gen;
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    @Override
    public void run() {

    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public Types staticDynamic() {
        return Types.STATIC;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        AntimatterAPI.all(BlockMachine.class, providerDomain, this::add);
        AntimatterAPI.all(BlockMultiMachine.class,providerDomain, this::add);
        AntimatterAPI.all(BlockPipe.class,providerDomain, this::add);
        AntimatterAPI.all(BlockStorage.class,providerDomain, this::add);
        AntimatterAPI.all(BlockOre.class,providerDomain, this::add);


        for (Map.Entry<Block, Function<Block, LootTable.Builder>> e : tables.entrySet()) {
            Path path = getPath(generator.getOutputFolder(), e.getKey().getRegistryName());
            IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().apply(e.getKey()).setParameterSet(LootParameterSets.BLOCK).build()), path);
        }
    }

    private static Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
    }

    private void add(Block block) {
        tables.put(block, this::build);
    }

    protected LootTable.Builder build(Block block) {
        return LootTable.builder().addLootPool(LootPool.builder().addEntry(ItemLootEntry.builder(block)));
    }

    @Override
    public String getName() {
        return providerName;
    }
}
