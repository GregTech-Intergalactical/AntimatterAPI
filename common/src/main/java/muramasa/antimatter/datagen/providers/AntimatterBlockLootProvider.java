package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static muramasa.antimatter.Data.*;

public class AntimatterBlockLootProvider extends BlockLoot implements DataProvider, IAntimatterProvider {
    protected final String providerDomain, providerName;
    protected final Map<Block, Function<Block, LootTable.Builder>> tables = new Object2ObjectOpenHashMap<>();

    public static final LootItemCondition.Builder BRANCH_CUTTER = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Data.BRANCH_CUTTER.getToolStack(Data.NULL, Data.NULL).getItem()));
    public static final LootItemCondition.Builder SAW = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Data.SAW.getToolStack(Data.NULL, Data.NULL).getItem()).hasEnchantment(new EnchantmentPredicate() {
        @Override
        public boolean containedIn(Map<Enchantment, Integer> enchantmentsIn) {
            return !enchantmentsIn.containsKey(Enchantments.SILK_TOUCH);
        }
    }));

    //public static final ILootCondition.IBuilder BRANCH_CUTTER_SHEARS_SILK_TOUCH = BlockLootTablesAccessor.getSilkTouchOrShears().alternative(BRANCH_CUTTER);

    //public static final ILootCondition.IBuilder BRANCH_CUTTER_SHEARS_SILK_TOUCH_INVERTED = BRANCH_CUTTER_SHEARS_SILK_TOUCH.inverted();


    public AntimatterBlockLootProvider(String providerDomain, String providerName) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    @Override
    public void run() {
        loot();
    }

    protected void loot() {
        AntimatterAPI.all(BlockMachine.class, providerDomain, this::add);
        AntimatterAPI.all(BlockMultiMachine.class, providerDomain, this::add);
        if (providerDomain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockPipe.class, this::add);
            AntimatterAPI.all(BlockStorage.class, this::add);
            AntimatterAPI.all(BlockStone.class, b -> {
                /*if (b.getType() instanceof CobbleStoneType && b.getSuffix().isEmpty()) {
                    tables.put(b, b2 -> createSingleItemTableWithSilkTouch(b, ((CobbleStoneType) b.getType()).getBlock("cobble")));
                    return;
                }*/
                this.add(b);
            });
            AntimatterAPI.all(BlockStoneSlab.class, b -> tables.put(b, BlockLoot::createSlabItemTable));
            AntimatterAPI.all(BlockStoneStair.class, this::add);
            AntimatterAPI.all(BlockStoneWall.class, this::add);
            AntimatterAPI.all(BlockOre.class, this::addToFortune);
            AntimatterAPI.all(BlockOreStone.class, this::addToStone);
        }
    }

    @Override
    public void onCompletion() {
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> e : tables.entrySet()) {
            LootTable table = e.getValue().apply(e.getKey()).setParamSet(LootContextParamSets.BLOCK).build();
            AntimatterDynamics.DYNAMIC_RESOURCE_PACK.addData(AntimatterDynamics.fix(AntimatterPlatformUtils.getIdFromBlock(e.getKey()), "loot_tables/blocks", "json"), AntimatterDynamics.serialize(table));
        }
    }

    @Override
    public void run(HashCache cache) throws IOException {
        /*loot();
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> e : tables.entrySet()) {
            Path path = getPath(generator.getOutputFolder(), e.getKey().getRegistryName());
            IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().apply(e.getKey()).setParameterSet(LootParameterSets.BLOCK).build()), path);
        }*/
    }

    protected void addToFortune(BlockOre block) {
        if (block.getOreType() == Data.ORE_SMALL) {
            if (!block.getMaterial().has(GEM) && !(block.getMaterial().has(RAW_ORE))) return;
            Item item = block.getMaterial().has(GEM) ? GEM.get(block.getMaterial()) : null;
            LootPool.Builder builder;
            if (item != null) {
                builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(applyExplosionDecay(item, LootItem.lootTableItem(item)).setWeight(30));
            } else {
                builder = LootPool.lootPool();
            }
            if (block.getMaterial().has(CRUSHED)) {
                Item crushed = CRUSHED.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(crushed, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(crushed))));
                builder.add(applyExplosionDecay(crushed, LootItem.lootTableItem(crushed).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))).setWeight(40)));
            }
            if (block.getMaterial().has(DUST_IMPURE)) {
                Item dirty = DUST_IMPURE.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(dirty, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dirty))));
                builder.add(applyExplosionDecay(dirty, LootItem.lootTableItem(dirty).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f)))).setWeight(60));
            }
            tables.put(block, b -> LootTable.lootTable().withPool(builder));
            return;
        } else if ((block.getMaterial().has(Data.RAW_ORE) || block.getMaterial().has(GEM)) && block.getOreType() == Data.ORE) {
            Item item = block.getMaterial().has(GEM) ? GEM.get(block.getMaterial()) : Data.RAW_ORE.get(block.getMaterial());
            tables.put(block, b -> createOreDrop(b, item));
            return;
        }
        add(block);
    }

    protected void addToStone(BlockOreStone block) {
        if (block.getMaterial() == Coal) {
            tables.put(block, b -> createOreDrop(b, GEM.get(block.getMaterial())));
        } else {
            add(block);
        }
    }

    protected void add(Block block) {
        tables.put(block, this::build);
    }

    protected LootTable.Builder build(Block block) {
        return createSingleItemTable(block);
    }

    @Override
    public String getName() {
        return providerName;
    }


    protected static LootTable.Builder droppingWithBranchCutters(Block block, Block sapling, float... chances) {
        return createLeavesDrops(block, sapling, chances).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(AntimatterBlockLootProvider.BRANCH_CUTTER).add(LootItem.lootTableItem(sapling)));
    }
}
