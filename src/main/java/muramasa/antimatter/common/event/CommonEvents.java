package muramasa.antimatter.common.event;

import com.google.common.eventbus.Subscribe;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
        if (!AntimatterConfig.GAMEPLAY.PLAY_CRAFTING_SOUNDS) return;
        IInventory inv = e.getInventory();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).getItem() instanceof IAntimatterTool) {
                IAntimatterTool tool = (IAntimatterTool) inv.getStackInSlot(i).getItem();
                SoundEvent type = tool.getType().getUseSound();
                if (type != null) {
                    e.getPlayer().playSound(type, 0.75F, 0.75F);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event){
        //Antimatter.LOGGER.info(event.getTable().getLootTableId().toString());
        if (event.getTable().getLootTableId().getPath().startsWith("blocks/")){
            ResourceLocation blockId = new ResourceLocation(event.getTable().getLootTableId().getNamespace(), event.getName().getPath().replace("blocks/", ""));
            if (ForgeRegistries.BLOCKS.containsKey(blockId)){
                Block block = ForgeRegistries.BLOCKS.getValue(blockId);
                //Antimatter.LOGGER.info(blockId.toString());
                if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE){
                    event.getTable().addPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(AntimatterBlockLootProvider.SAW).addEntry(ItemLootEntry.builder(block)).build());
                }
            }
        }
    }

}
