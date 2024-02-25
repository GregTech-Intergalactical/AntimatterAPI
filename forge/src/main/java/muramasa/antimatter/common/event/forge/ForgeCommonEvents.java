package muramasa.antimatter.common.event.forge;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterRemapping;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.TileTicker;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import static muramasa.antimatter.data.AntimatterMaterialTypes.DUST;
import static muramasa.antimatter.data.AntimatterMaterials.Stone;
import static muramasa.antimatter.material.Material.NULL;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open ev) {
        CommonEvents.onContainerOpen(ev.getPlayer(), ev.getContainer());
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
        CommonEvents.onItemCrafted(e.getInventory(), e.getPlayer());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        TileTicker.onServerWorldTick(ServerLifecycleHooks.getCurrentServer(), event.phase == TickEvent.Phase.START);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        CommonEvents.placeBlock(event.getPlacedAgainst(), event.getEntity(), event.getWorld(), event.getPos(), event.getPlacedBlock());
    }

    @SubscribeEvent
    public static void onAnvilUpdated(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (CommonEvents.anvilUpdate(left, right)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event) {
        CommonEvents.lootTableLoad(event.getTable(), event.getName());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        CommonEvents.getPLAYER_TICK_CALLBACKS().forEach(c -> {
            c.onTick(event.phase == TickEvent.Phase.END, event.side == LogicalSide.SERVER, event.player);
        });
    }

    @SubscribeEvent
    public static void remapMissingBlocks(final RegistryEvent.MissingMappings<Block> event) {
        for (String modid : AntimatterRemapping.getRemappingMap().keySet()) {
            for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getMappings(modid)) {
                var map = AntimatterRemapping.getRemappingMap().get(modid);
                if (map.containsKey(mapping.key.getPath())){
                    Block replacement = AntimatterAPI.get(Block.class, map.get(mapping.key.getPath()));
                    if (replacement != null){
                        mapping.remap(replacement);
                    }
                }
            }
        }
        event.getMappings(Ref.MOD_KJS).forEach(map -> {
            String domain = map.key.getNamespace();
            String id = map.key.getPath();
            if (id.startsWith("block_")) {
                Material mat = Material.get(id.replace("block_", ""));
                if (mat != NULL) {
                    map.remap(AntimatterMaterialTypes.BLOCK.get().get(mat).asBlock());
                    return;
                }
            }
            if (id.startsWith("ore_")) {
                Block replacement = AntimatterAPI.get(BlockOre.class, id);
                if (replacement != null) {
                    map.remap(replacement);
                    return;
                }
            }
            Block replacement = AntimatterAPI.get(Block.class, id, Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        });
        event.getMappings(Ref.SHARED_ID).forEach(map -> {
            String id = map.key.getPath();
            if (id.equals("basalt")){
                map.remap(Blocks.BASALT);
                return;
            }
            String replacement = "";
            if (id.startsWith("fluid_")){
                replacement = id.replace("fluid_", "fluid_pipe_");
            } else if (id.startsWith("item_")){
                replacement = id.replace("item_", "item_pipe_");
            } else if (id.contains("vanilla_basalt")){
                replacement = id.replace("vanilla_basalt", "basalt");
            } else if (id.contains("sad_red")){
                replacement = id.replace("sand_red", "red_sand");
            }
            if (id.contains("__")){
                replacement = replacement.isEmpty() ? id.replace("__", "_") : replacement.replace("__", "_");
            }
            if (!replacement.isEmpty()) {
                Block replacementBlock = AntimatterAPI.get(Block.class, replacement, Ref.SHARED_ID);
                if (replacementBlock != null){
                    map.remap(replacementBlock);
                }
            }
        });
    }

    @SubscribeEvent
    public static void remapMissingItems(final RegistryEvent.MissingMappings<Item> event) {
        for (String modid : AntimatterRemapping.getRemappingMap().keySet()) {
            for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings(modid)) {
                var map = AntimatterRemapping.getRemappingMap().get(modid);
                if (map.containsKey(mapping.key.getPath())){
                    Item replacement = AntimatterAPI.get(Item.class, map.get(mapping.key.getPath()));
                    if (replacement != null){
                        mapping.remap(replacement);
                    }
                }
            }
        }
        event.getMappings(Ref.ID).forEach(map -> {
            Item replacement = AntimatterAPI.get(Item.class, map.key.getPath(), Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        });

        event.getMappings(Ref.SHARED_ID).forEach(map -> {
            String id = map.key.getPath();
            if (id.equals("basalt")){
                map.remap(Items.BASALT);
                return;
            }
            if (id.equals("dust_gravel")){
                map.remap(DUST.get(Stone));
                return;
            }
            if (id.startsWith("rock_")){
                Item replacement = AntimatterAPI.get(Item.class, id.replace("rock_", "bearing_rock_"), Ref.SHARED_ID);
                if (replacement != null) {
                    map.remap(replacement);
                    return;
                }
            }
            if (id.contains("crushed_centrifuged")){
                Item replacement = AntimatterAPI.get(Item.class, id.replace("centrifuged", "refined"), Ref.SHARED_ID);
                if (replacement != null) {
                    map.remap(replacement);
                    return;
                }
            }
            String replacement = "";
            if (id.startsWith("fluid_")){
                replacement = id.replace("fluid_", "fluid_pipe_");
            } else if (id.startsWith("item_")){
                replacement = id.replace("item_", "item_pipe_");
            } else if (id.contains("vanilla_basalt")){
                replacement = id.replace("vanilla_basalt", "basalt");
            } else if (id.contains("sand_red")){
                replacement = id.replace("sand_red", "red_sand");
            }
            if (id.contains("__")){
                replacement = replacement.isEmpty() ? id.replace("__", "_") : replacement.replace("__", "_");
            }
            if (!replacement.isEmpty()) {
                Item replacementBlock = AntimatterAPI.get(Item.class, replacement, Ref.SHARED_ID);
                if (replacementBlock != null){
                    map.remap(replacementBlock);
                }
            }
        });
    }

    public static <T> LazyOptional<T> fromHolder(Holder<T, ?> holder, Direction side){
        if (!holder.isPresent()) return LazyOptional.empty();
        LazyOptional<T> opt = LazyOptional.of(() -> holder.side(side).get());
        boolean add = holder.addListener(side, opt::invalidate);
        if (!add) return LazyOptional.empty();
        return opt;
    }

    /**
     * Recipe event for local servers, builds recipes.
     * @param ev forge event callback.
     */
    @SubscribeEvent
    public static void recipeEvent(RecipesUpdatedEvent ev) {
       CommonEvents.recipeEvent(ev.getRecipeManager());
    }

    /**
     * Recipe event for online server, builds recipes.
     * @param ev forge event callback.
     */
    @SubscribeEvent
    public static void tagsEvent(TagsUpdatedEvent ev) {
        CommonEvents.tagsEvent();
    }

    @SubscribeEvent
    public static void biomeLoadEvent(BiomeLoadingEvent event){
        AntimatterWorldGenerator.reloadEvent(event.getName(),  event.getClimate(), event.getCategory(), event.getEffects(), event.getGeneration(), event.getSpawns());
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event){
        StructureCache.onWorldUnload(event.getWorld());
    }

}
