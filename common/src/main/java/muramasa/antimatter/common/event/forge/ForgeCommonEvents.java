package muramasa.antimatter.common.event.forge;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
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

}
