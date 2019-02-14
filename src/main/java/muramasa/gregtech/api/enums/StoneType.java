package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.data.Materials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum StoneType implements IStringSerializable {

    GRANITE_RED(Materials.GraniteRed.getDust(1)),
    GRANITE_BLACK(Materials.GraniteBlack.getDust(1)),
    MARBLE(Materials.Marble.getDust(1)),
    BASALT(Materials.Basalt.getDust(1));

    private ItemStack droppedDust;

    StoneType(ItemStack droppedDust) {
       this.droppedDust = droppedDust;
    }

    public ItemStack getDroppedDust() {
        return droppedDust.copy();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
