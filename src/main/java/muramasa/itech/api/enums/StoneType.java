package muramasa.itech.api.enums;

import muramasa.itech.api.materials.Material;
import net.minecraft.item.ItemStack;

public enum StoneType {

    //TODO update dust
    STONE(Material.Osmium.getDust(1)),
    NETHERRACK(Material.Osmium.getDust(1)),
    ENDSTONE(Material.Osmium.getDust(1)),
    REDGRANITE(Material.Osmium.getDust(1)),
    BLACKGRANITE(Material.Osmium.getDust(1)),
    MARBLE(Material.Osmium.getDust(1)),
    BASALT(Material.Osmium.getDust(1));

    private ItemStack droppedDust;

    StoneType(ItemStack droppedDust) {
       this.droppedDust = droppedDust;
    }

    public ItemStack getDroppedDust() {
        return droppedDust;
    }
}
