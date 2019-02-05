package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.data.Materials;
import net.minecraft.item.ItemStack;

public enum StoneType {

    //TODO update dust
    STONE(Materials.Osmium.getDust(1)),
    NETHERRACK(Materials.Osmium.getDust(1)),
    ENDSTONE(Materials.Osmium.getDust(1)),
    REDGRANITE(Materials.Osmium.getDust(1)),
    BLACKGRANITE(Materials.Osmium.getDust(1)),
    MARBLE(Materials.Osmium.getDust(1)),
    BASALT(Materials.Osmium.getDust(1));

    private ItemStack droppedDust;

    StoneType(ItemStack droppedDust) {
       this.droppedDust = droppedDust;
    }

    public ItemStack getDroppedDust() {
        return droppedDust;
    }
}
