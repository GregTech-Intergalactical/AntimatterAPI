package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.CobbleStoneType;
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

public class AntimatterBlockLootProvider extends BlockLoot implements DataProvider, IAntimatterProvider {
    protected final String providerDomain, providerName;
    protected final Map<Block, Function<Block, LootTable.Builder>> tables = new Object2ObjectOpenHashMap<>();

    public static final LootItemCondition.Builder BRANCH_CUTTER = MatchTool.toolMatches(ItemPredicate.Builder.item().of(AntimatterDefaultTools.BRANCH_CUTTER.getToolStack(Material.NULL, Material.NULL).getItem()));
    public static final LootItemCondition.Builder SAW = MatchTool.toolMatches(ItemPredicate.Builder.item().of(AntimatterDefaultTools.SAW.getToolStack(Material.NULL, Material.NULL).getItem()).hasEnchantment(new EnchantmentPredicate() {
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

    public static void init(){}

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
                if (b.getType() instanceof CobbleStoneType && b.getSuffix().isEmpty()) {
                    tables.put(b, b2 -> createSingleItemTableWithSilkTouch(b, ((CobbleStoneType) b.getType()).getBlock("cobble")));
                    return;
                }
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
            AntimatterDynamics.RUNTIME_DATA_PACK.addData(AntimatterDynamics.fix(AntimatterPlatformUtils.getIdFromBlock(e.getKey()), "loot_tables/blocks", "json"), AntimatterDynamics.serialize(table));
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
        if (block.getMaterial().has(MaterialTags.CUSTOM_ORE_DROPS)){
            tables.put(block, b -> MaterialTags.CUSTOM_ORE_DROPS.getBuilderFunction(block.getMaterial()).apply(block));
            return;
        }
        tables.put(block, addToFortuneWithoutCustomDrops(block));
    }

    public static Function<Block, LootTable.Builder> addToFortuneWithoutCustomDrops(BlockOre block) {
        if (block.getOreType() == AntimatterMaterialTypes.ORE_SMALL) {
            if (!block.getMaterial().has(AntimatterMaterialTypes.GEM) && !(block.getMaterial().has(AntimatterMaterialTypes.RAW_ORE))) return BlockLoot::createSingleItemTable;
            Item item = block.getMaterial().has(AntimatterMaterialTypes.GEM) ? AntimatterMaterialTypes.GEM.get(block.getMaterial()) : null;
            LootPool.Builder builder;
            if (item != null) {
                builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(applyExplosionDecay(item, LootItem.lootTableItem(item)).setWeight(30));
            } else {
                builder = LootPool.lootPool();
            }
            if (block.getMaterial().has(AntimatterMaterialTypes.CRUSHED)) {
                Item crushed = AntimatterMaterialTypes.CRUSHED.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(crushed, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(crushed))));
                builder.add(applyExplosionDecay(crushed, LootItem.lootTableItem(crushed).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))).setWeight(40)));
            }
            if (block.getMaterial().has(AntimatterMaterialTypes.DUST_IMPURE)) {
                Item dirty = AntimatterMaterialTypes.DUST_IMPURE.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(dirty, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dirty))));
                builder.add(applyExplosionDecay(dirty, LootItem.lootTableItem(dirty).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f)))).setWeight(60));
            }
            return b -> LootTable.lootTable().withPool(builder);
        } else if (block.getOreType() == AntimatterMaterialTypes.ORE) {
            if (block.getStoneType() == AntimatterStoneTypes.GRAVEL || block.getStoneType() == AntimatterStoneTypes.SAND || block.getStoneType() == AntimatterStoneTypes.SAND_RED){
                return BlockLoot::createSingleItemTable;
            }
            if (block.getMaterial().has(AntimatterMaterialTypes.RAW_ORE)) {
                Item item = AntimatterMaterialTypes.RAW_ORE.get(block.getMaterial());
                return b -> createOreDrop(b, item);
            }
        }
        return BlockLoot::createSingleItemTable;
    }

    protected void addToStone(BlockOreStone block) {
        if (block.getMaterial().has(MaterialTags.CUSTOM_ORE_STONE_DROPS)){
            tables.put(block, b -> MaterialTags.CUSTOM_ORE_STONE_DROPS.getBuilderFunction(block.getMaterial()).apply(block));
            return;
        }
        if (block.getMaterial().has(AntimatterMaterialTypes.RAW_ORE)) {
            Item item = AntimatterMaterialTypes.RAW_ORE.get(block.getMaterial());
            tables.put(block, b -> createOreDrop(b, item));
            return;
        }
        add(block);
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
