package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.*;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.pipe.BlockPipe;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import static muramasa.antimatter.Data.*;

public class AntimatterBlockLootProvider extends BlockLootTables implements IDataProvider, IAntimatterProvider {
    protected final String providerDomain, providerName;
    private final DataGenerator generator;
    protected final Map<Block, Function<Block, LootTable.Builder>> tables = new Object2ObjectOpenHashMap<>();

    public static final ILootCondition.IBuilder BRANCH_CUTTER = MatchTool.builder(ItemPredicate.Builder.create().item(Data.BRANCH_CUTTER.getToolStack(Data.NULL, Data.NULL).getItem()));
    public static final ILootCondition.IBuilder SAW = MatchTool.builder(ItemPredicate.Builder.create().item(Data.SAW.getToolStack(Data.NULL, Data.NULL).getItem()).enchantment(new EnchantmentPredicate() {
        @Override
        public boolean test(Map<Enchantment, Integer> enchantmentsIn) {
            return !enchantmentsIn.containsKey(Enchantments.SILK_TOUCH);
        }
    }));

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
        AntimatterAPI.all(BlockMultiMachine.class, providerDomain, this::add);
        if (providerDomain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockPipe.class, this::add);
            AntimatterAPI.all(BlockStorage.class, this::add);
            AntimatterAPI.all(BlockStone.class, b -> {
                if (b.getType() instanceof CobbleStoneType && b.getSuffix().isEmpty()) {
                    tables.put(b, b2 -> droppingWithSilkTouch(b, ((CobbleStoneType) b.getType()).getBlock("cobble")));
                    return;
                }
                this.add(b);
            });
            AntimatterAPI.all(BlockStoneSlab.class, b -> tables.put(b, BlockLootTables::droppingSlab));
            AntimatterAPI.all(BlockStoneStair.class, this::add);
            AntimatterAPI.all(BlockStoneWall.class, this::add);
            AntimatterAPI.all(BlockOre.class, this::addToFortune);
            AntimatterAPI.all(BlockOreStone.class, this::addToStone);
        }
    }

    @Override
    public void onCompletion() {
        for (Map.Entry<Block, Function<Block, LootTable.Builder>> e : tables.entrySet()) {
            DynamicResourcePack.addLootEntry(e.getKey().getRegistryName(), e.getValue().apply(e.getKey()).setParameterSet(LootParameterSets.BLOCK).build());
        }
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

    protected void addToFortune(BlockOre block) {
        if (block.getOreType() == Data.ORE_SMALL) {
            if (!block.getMaterial().has(GEM) && !(block.getMaterial().has(RAW_ORE))) return;
            Item item = block.getMaterial().has(GEM) ? GEM.get(block.getMaterial()) : null;
            LootPool.Builder builder;
            if (item != null) {
                builder = LootPool.builder().rolls(ConstantRange.of(1)).addEntry(withExplosionDecay(item, ItemLootEntry.builder(item)).weight(30));
            } else {
                builder = LootPool.builder();
            }
            if (block.getMaterial().has(CRUSHED)) {
                Item crushed = CRUSHED.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(crushed, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(crushed))));
                builder.addEntry(withExplosionDecay(crushed, ItemLootEntry.builder(crushed).acceptFunction(SetCount.builder(RandomValueRange.of(1.0f, 2.0f))).weight(40)));
            }
            if (block.getMaterial().has(DUST_IMPURE)) {
                Item dirty = DUST_IMPURE.get(block.getMaterial());
                //builder.addLootPool(withSurvivesExplosion(dirty, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(dirty))));
                builder.addEntry(withExplosionDecay(dirty, ItemLootEntry.builder(dirty).acceptFunction(SetCount.builder(RandomValueRange.of(1.0f, 2.0f)))).weight(60));
            }
            tables.put(block, b -> LootTable.builder().addLootPool(builder));
            return;
        } else if ((block.getMaterial().has(Data.RAW_ORE) || block.getMaterial().has(GEM)) && block.getOreType() == Data.ORE) {
            Item item = block.getMaterial().has(GEM) ? GEM.get(block.getMaterial()) : Data.RAW_ORE.get(block.getMaterial());
            tables.put(block, b -> droppingItemWithFortune(b, item));
            return;
        }
        add(block);
    }

    protected void addToStone(BlockOreStone block) {
        if (block.getMaterial() == Coal){
            tables.put(block, b -> droppingItemWithFortune(b, GEM.get(block.getMaterial())));
        } else {
            add(block);
        }
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


    protected static LootTable.Builder droppingWithBranchCutters(Block block, Block sapling, float... chances) {
        return droppingWithChancesAndSticks(block, sapling, chances).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(AntimatterBlockLootProvider.BRANCH_CUTTER).addEntry(ItemLootEntry.builder(sapling)));
    }
}
