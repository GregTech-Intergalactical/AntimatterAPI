package muramasa.gtu.client.events;

import muramasa.gtu.Ref;
import muramasa.gtu.api.recipe.RecipeHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent e) {
        if (Ref.SHOW_STACK_ORE_DICT) {
            String[] names = RecipeHelper.getOreNames(e.getItemStack());
            if (names.length > 0) e.getToolTip().addAll(Arrays.asList(names));
        }
    }
}
