package muramasa.antimatter.common.event.forge;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static muramasa.antimatter.Data.NULL;

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
    public static void remapMissingBlocks(final RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> map : event.getMappings(Ref.MOD_KJS)) {
            String domain = map.key.getNamespace();
            String id = map.key.getPath();
            if (id.startsWith("block_")) {
                Material mat = Material.get(id.replace("block_", ""));
                if (mat != NULL) {
                    map.remap(Data.BLOCK.get().get(mat).asBlock());
                    continue;
                }
            }
            if (id.startsWith("ore_")) {
                Block replacement = AntimatterAPI.get(BlockOre.class, id);
                if (replacement != null) {
                    map.remap(replacement);
                    continue;
                }
            }
            Block replacement = AntimatterAPI.get(Block.class, id, Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        }
    }

    @SubscribeEvent
    public static void remapMissingItems(final RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> map : event.getMappings(Ref.ID)) {
            Item replacement = AntimatterAPI.get(Item.class, map.key.getPath(), Ref.SHARED_ID);
            if (replacement != null) {
                map.remap(replacement);
            }
        }
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
