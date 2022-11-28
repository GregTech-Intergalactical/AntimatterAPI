package muramasa.antimatter.item.interaction;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.material.MaterialItem;
import net.minecraft.core.cauldron.CauldronInteraction;

import static muramasa.antimatter.data.AntimatterMaterialTypes.DUST;
import static muramasa.antimatter.data.AntimatterMaterialTypes.DUST_IMPURE;

/**
 * All antimatter cauldron interactions.
 */
public class CauldronInteractions {

    public static void init() {
        DUST_IMPURE.all().stream().filter(t -> t.has(DUST)).map(t -> DUST_IMPURE.get(t)).forEach(stack -> CauldronInteraction.WATER.put(stack, MaterialItem::interactWithCauldron));
        AntimatterAPI.all(ItemFluidCell.class, t -> CauldronInteraction.WATER.put(t, ItemFluidCell::interactWithCauldron));
    }
}
