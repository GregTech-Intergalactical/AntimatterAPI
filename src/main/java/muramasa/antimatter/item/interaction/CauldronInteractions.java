package muramasa.antimatter.item.interaction;

import muramasa.antimatter.item.ItemFluidCell;
import net.minecraft.core.cauldron.CauldronInteraction;

import static muramasa.antimatter.Data.DUST;

/**
 * All antimatter cauldron interactions.
 */
public class CauldronInteractions {

    public static void init() {
        DUST.all().stream().map(t -> DUST.get(t)).forEach(stack -> {
            CauldronInteraction.WATER.put(stack, ItemFluidCell::interactWithCauldron);
        });
    }
}
