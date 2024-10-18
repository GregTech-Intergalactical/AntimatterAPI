package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.*;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static muramasa.antimatter.data.AntimatterMaterialTypes.*;

public class AntimatterBlockLootProvider extends BlockLoot implements DataProvider, IAntimatterProvider {
    protected final String providerDomain, providerName;
    protected final Map<Block, Function<Block, LootTable.Builder>> tables = new Object2ObjectOpenHashMap<>();
    protected static final Map<Block, Function<Block, LootTable.Builder>> GLOBAL_TABLES = new Object2ObjectOpenHashMap<>();

    public static final LootItemCondition.Builder BRANCH_CUTTER = MatchTool.toolMatches(ItemPredicate.Builder.item().of(AntimatterDefaultTools.BRANCH_CUTTER.getTag()));
    public static final LootItemCondition.Builder HAMMER = MatchTool.toolMatches(ItemPredicate.Builder.item().of(AntimatterDefaultTools.HAMMER.getTag()));
    public static final LootItemCondition.Builder SAW = MatchTool.toolMatches(ItemPredicate.Builder.item().of(AntimatterDefaultTools.SAW.getTag()).hasEnchantment(new EnchantmentPredicate() {
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
            AntimatterAPI.all(BlockStorage.class, block -> {
                if (block.getType() == RAW_ORE_BLOCK && block.getMaterial().has(CRUSHED)){
                    tables.put(block, b -> createOreDropWithHammer(block, block.asItem(), CRUSHED.get(block.getMaterial()), 9 * MaterialTags.ORE_MULTI.get(block.getMaterial())));
                } else {
                    add(block);
                }
            });
            AntimatterAPI.all(BlockFrame.class, this::add);
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
            AntimatterAPI.all(BlockSurfaceRock.class, b -> {
                ItemStack drop = b.getMaterial() != Material.NULL && b.getMaterial().has(BEARING_ROCK) ? BEARING_ROCK.get(b.getMaterial(), 1) : b.getStoneType().getMaterial().has(ROCK) ? ROCK.get(b.getStoneType().getMaterial(), 1) : ItemStack.EMPTY;
                if (!drop.isEmpty()) {
                    tables.put(b, b2 -> BlockLoot.createSingleItemTable(drop.getItem()));
                }
            });
        }
    }

    @Override
    public void onCompletion() {
        for (var e : tables.entrySet()) {
            if (!GLOBAL_TABLES.containsKey(e.getKey())){
                GLOBAL_TABLES.put(e.getKey(), e.getValue());
            }
        }
    }

    public static void afterCompletion(){
        for (var e : GLOBAL_TABLES.entrySet()) {
            LootTable table = e.getValue().apply(e.getKey()).setParamSet(LootContextParamSets.BLOCK).build();
            AntimatterDynamics.RUNTIME_DATA_PACK.addData(AntimatterDynamics.fix(AntimatterPlatformUtils.INSTANCE.getIdFromBlock(e.getKey()), "loot_tables/blocks", "json"), AntimatterDynamics.serialize(table));
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

    protected void overrideBlock(Block block, Function<Block, LootTable.Builder> builderFunction){
        GLOBAL_TABLES.put(block, builderFunction);
    }

    protected void overrideOre(Material ore, Function<BlockOre, LootTable.Builder> builderFunction){
        if (ore.has(ORE)){
            AntimatterAPI.all(StoneType.class).stream().filter(s -> s.doesGenerateOre() && s != AntimatterStoneTypes.BEDROCK).forEach(s -> {
                if (ORE.get().get(ore, s).asBlock() instanceof BlockOre blockOre) {
                    GLOBAL_TABLES.put(blockOre, b -> builderFunction.apply((BlockOre) b));
                }
            });
        }
    }

    protected void overrideSmallOre(Material ore, Function<BlockOre, LootTable.Builder> builderFunction){
        if (ore.has(ORE_SMALL)){
            AntimatterAPI.all(StoneType.class).stream().filter(s -> s.doesGenerateOre() && s != AntimatterStoneTypes.BEDROCK).forEach(s -> {
                if (ORE_SMALL.get().get(ore, s).asBlock() instanceof BlockOre blockOre) {
                    GLOBAL_TABLES.put(blockOre, b -> builderFunction.apply((BlockOre) b));
                }
            });
        }
    }

    protected void addToFortune(BlockOre block) {
        if (block.getMaterial().has(MaterialTags.CUSTOM_ORE_DROPS)){
            tables.put(block, b -> MaterialTags.CUSTOM_ORE_DROPS.getBuilderFunction(block.getMaterial()).apply(block));
            return;
        }
        tables.put(block, addToFortuneWithoutCustomDrops(block));
    }

    public static Function<Block, LootTable.Builder> addToFortuneWithoutCustomDrops(BlockOre block) {
        if (block.getOreType() == ORE_SMALL) {
            if (!block.getMaterial().has(AntimatterMaterialTypes.GEM) && !(block.getMaterial().has(AntimatterMaterialTypes.CRUSHED))) {
                if (block.getMaterial().has(DUST)){
                    return b -> BlockLoot.createSingleItemTable(DUST.get(block.getMaterial()));
                }
                return BlockLoot::createSingleItemTable;
            }
            Item item = block.getMaterial().has(AntimatterMaterialTypes.GEM) ? AntimatterMaterialTypes.GEM.get(block.getMaterial()) : null;
            int multiplier = MaterialTags.ORE_MULTI.getInt(block.getMaterial());
            LootPool.Builder builder;
            if (item != null) {
                builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(applyExplosionDecay(item, LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(multiplier))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))).setWeight(30));
            } else {
                builder = LootPool.lootPool();
            }
            if (block.getMaterial().has(AntimatterMaterialTypes.CRUSHED)) {
                Item crushed = AntimatterMaterialTypes.CRUSHED.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(crushed, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(crushed))));
                builder.add(applyExplosionDecay(crushed, LootItem.lootTableItem(crushed).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f * multiplier, 2.0f * multiplier))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)).setWeight(40)));
            }
            if (block.getMaterial().has(AntimatterMaterialTypes.DUST_IMPURE)) {
                Item dirty = AntimatterMaterialTypes.DUST_IMPURE.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(dirty, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dirty))));
                builder.add(applyExplosionDecay(dirty, LootItem.lootTableItem(dirty).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f * multiplier, 2.0f * multiplier))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))).setWeight(60));
            }
            return b -> LootTable.lootTable().withPool(builder);
        } else if (block.getOreType() == ORE) {
            Item drop;
            if (block.getMaterial().has(AntimatterMaterialTypes.CRUSHED) || block.getMaterial().has(AntimatterMaterialTypes.DUST)){
                drop = block.getMaterial().has(AntimatterMaterialTypes.CRUSHED) ? AntimatterMaterialTypes.CRUSHED.get(block.getMaterial()) : AntimatterMaterialTypes.DUST.get(block.getMaterial());
            } else {
                drop = null;
            }
            Item item = block.getStoneType().isSandLike() ? block.asItem() : AntimatterMaterialTypes.RAW_ORE.get(block.getMaterial());
            return b -> createOreDropWithHammer(b, item, drop, 1);
        }
        return BlockLoot::createSingleItemTable;
    }

    public static LootTable.Builder createOreDropWithHammer(Block block, Item primaryDrop, Item hammerDrop, int hammerAmount){
        LootTable.Builder builder = LootTable.lootTable();
        if (block.asItem() == primaryDrop){
            LootPool.Builder loot = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(primaryDrop));
            if (hammerDrop != null) loot.when(HAMMER.invert());
            builder.withPool(applyExplosionCondition(block, loot));
        } else {
            LootPoolSingletonContainer.Builder<?> pool = LootItem.lootTableItem(primaryDrop).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE));
            if (hammerDrop != null) pool.when(HAMMER.invert());
            builder = createSilkTouchDispatchTable(block, applyExplosionDecay(block, pool));
        }
        if (hammerDrop != null){
            builder.withPool(applyExplosionCondition(hammerDrop, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAMMER).add(LootItem.lootTableItem(hammerDrop).apply(SetItemCountFunction.setCount(ConstantValue.exactly(hammerAmount))))));
        }
        return builder;
    }

    protected void addToStone(BlockOreStone block) {
        if (block.getMaterial().has(MaterialTags.CUSTOM_ORE_STONE_DROPS)){
            tables.put(block, b -> MaterialTags.CUSTOM_ORE_STONE_DROPS.getBuilderFunction(block.getMaterial()).apply(block));
            return;
        }
        if (block.getMaterial().has(AntimatterMaterialTypes.RAW_ORE)) {
            Item item = AntimatterMaterialTypes.RAW_ORE.get(block.getMaterial());
            tables.put(block, b -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)))));
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
