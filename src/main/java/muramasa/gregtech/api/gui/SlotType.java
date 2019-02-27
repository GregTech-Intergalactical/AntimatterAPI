package muramasa.gregtech.api.gui;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum SlotType implements IStringSerializable {

    IT_IN,
    IT_OUT,
    FL_IN,
    FL_OUT,
    CELL_IN,
    CELL_OUT;

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
