package muramasa.antimatter.datagen.providers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.BlockPipe;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

public class AntimatterBlockLootProvider extends BlockLootTables implements IDataProvider, IAntimatterProvider {
    protected final String providerDomain, providerName;
    private final DataGenerator generator;
    protected final Map<Block, Function<Block, LootTable.Builder>> tables = new Object2ObjectOpenHashMap<>();

    public static final ILootCondition.IBuilder BRANCH_CUTTER = MatchTool.builder(ItemPredicate.Builder.create().item(Data.BRANCH_CUTTER.getToolStack(Data.NULL, Data.NULL).getItem()));

    //public static final ILootCondition.IBuilder BRANCH_CUTTER_SHEARS_SILK_TOUCH = BlockLootTablesAccessor.getSilkTouchOrShears().alternative(BRANCH_CUTTER);

    //public static final ILootCondition.IBuilder BRANCH_CUTTER_SHEARS_SILK_TOUCH_INVERTED = BRANCH_CUTTER_SHEARS_SILK_TOUCH.inverted();


    public AntimatterBlockLootProvider(String providerDomain, String providerName, DataGenerator gen) {
        generator = gen;
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    @Override
    public void run() {
        loot();
    }

    protected void loot() {
        AntimatterAPI.all(BlockMachine.class, providerDomain, this::add);
        AntimatterAPI.all(BlockMultiMachine.class,providerDomain, this::add);
        AntimatterAPI.all(BlockPipe.class,providerDomain, this::add);
        AntimatterAPI.all(BlockStorage.class,providerDomain, this::add);
        AntimatterAPI.all(BlockOre.class,providerDomain, this::add);
    }

    @Override
    public void onCompletion() {
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> e : tables.entrySet()) {
            DynamicResourcePack.addLootEntry(e.getKey().getRegistryName(), e.getValue().apply(e.getKey()).setParameterSet(LootParameterSets.BLOCK).build());
        }
    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        /*loot();
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> e : tables.entrySet()) {
            Path path = getPath(generator.getOutputFolder(), e.getKey().getRegistryName());
            IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().apply(e.getKey()).setParameterSet(LootParameterSets.BLOCK).build()), path);
        }*/
    }

    private static Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
    }

    protected void add(Block block) {
        tables.put(block, this::build);
    }

    protected LootTable.Builder build(Block block) {
        return dropping(block);
    }

    @Override
    public String getName() {
        return providerName;
    }


    protected static LootTable.Builder droppingWithBranchCutters(Block block, Block sapling, float... chances){
        return droppingWithChancesAndSticks(block, sapling, chances).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(AntimatterBlockLootProvider.BRANCH_CUTTER).addEntry(ItemLootEntry.builder(sapling)));
    }
}
