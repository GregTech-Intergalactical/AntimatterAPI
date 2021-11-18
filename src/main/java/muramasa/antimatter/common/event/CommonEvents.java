package muramasa.antimatter.common.event;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static muramasa.antimatter.Data.NULL;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
        if (!AntimatterConfig.GAMEPLAY.PLAY_CRAFTING_SOUNDS) return;
        IInventory inv = e.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() instanceof IAntimatterTool) {
                IAntimatterTool tool = (IAntimatterTool) inv.getItem(i).getItem();
                SoundEvent type = tool.getAntimatterToolType().getUseSound();
                if (type != null) {
                    e.getPlayer().playSound(type, 0.75F, 0.75F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdated(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.getItem() == right.getItem()) {
            if (left.getItem() instanceof IAntimatterTool && right.getItem() instanceof IAntimatterTool) {
                IAntimatterTool leftTool = (IAntimatterTool) left.getItem();
                IAntimatterTool rightTool = (IAntimatterTool) right.getItem();
                if (leftTool.getPrimaryMaterial(left) != rightTool.getPrimaryMaterial(right) || leftTool.getSecondaryMaterial(left) != rightTool.getSecondaryMaterial(right)) {
                    event.setCanceled(true);
                }
            } else if (left.getItem() instanceof IAntimatterArmor && right.getItem() instanceof IAntimatterArmor) {
                IAntimatterArmor leftTool = (IAntimatterArmor) left.getItem();
                IAntimatterArmor rightTool = (IAntimatterArmor) right.getItem();
                if (leftTool.getMaterial(left) != rightTool.getMaterial(right)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event) {
        //Antimatter.LOGGER.info(event.getTable().getLootTableId().toString());
        if (event.getTable().getLootTableId().getPath().startsWith("blocks/")) {
            ResourceLocation blockId = new ResourceLocation(event.getTable().getLootTableId().getNamespace(), event.getName().getPath().replace("blocks/", ""));
            if (ForgeRegistries.BLOCKS.containsKey(blockId)) {
                Block block = ForgeRegistries.BLOCKS.getValue(blockId);
                //Antimatter.LOGGER.info(blockId.toString());
                if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE) {
                    event.getTable().addPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(AntimatterBlockLootProvider.SAW).add(ItemLootEntry.lootTableItem(block)).build());
                }
            }
        }
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
