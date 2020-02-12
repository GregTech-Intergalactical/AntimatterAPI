package muramasa.antimatter.advancement;

import muramasa.antimatter.Ref;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static muramasa.antimatter.advancement.trigger.AntimatterTriggers.TAG_SENSITIVE_INVENTORY_CHANGED_TRIGGER;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class AdvancementEventHandler {

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent e) {
        TAG_SENSITIVE_INVENTORY_CHANGED_TRIGGER.trigger((ServerPlayerEntity) e.getPlayer(), e.getStack());
    }

}
