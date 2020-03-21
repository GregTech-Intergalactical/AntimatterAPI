package muramasa.antimatter.common.event;

import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
        if (!Configs.GAMEPLAY.PLAY_CRAFTING_SOUNDS) return;
        IInventory inv = e.getInventory();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i).getItem() instanceof IAntimatterTool) {
                IAntimatterTool tool = (IAntimatterTool) inv.getStackInSlot(i).getItem();
                if (tool.getType().getUseSound() != null) {
                    SoundEvent type = tool.getType().getUseSound();
                    e.getPlayer().playSound(type, 0.75F, 0.75F);
                }
            }
        }
    }

}
