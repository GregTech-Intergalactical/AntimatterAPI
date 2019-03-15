package muramasa.gregtech.common.events;

import muramasa.gregtech.api.enums.ToolType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class RenderPlayerHandler {

    private static boolean doAnimation;

    //TODO avoid this by using NBT animations

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event){ //handles both 1st and 3rd person rendering, but in a cheatier way
        if (event.side.isServer() || event.phase == TickEvent.Phase.START || event.player == null) return;
        if (event.player.isSwingInProgress && event.player.getHeldItem(event.player.swingingHand) != ItemStack.EMPTY){
            ItemStack stack = event.player.getHeldItem(event.player.swingingHand);
            if (ToolType.hasBowAnimation(stack)) {
                doAnimation = true;
                event.player.setActiveHand(event.player.swingingHand);
                doAnimation = false;
                event.player.swingProgress = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onUseItem(LivingEntityUseItemEvent.Start event){
//        if (doAnimation && event.getDuration() == 0) {
//            System.out.println("Test");
//            event.setDuration(1);
//        }
    }
}
