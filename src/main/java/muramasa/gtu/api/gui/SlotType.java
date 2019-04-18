package muramasa.gtu.api.gui;

import muramasa.gtu.api.interfaces.IGregTechObject;

import java.util.Locale;

public enum SlotType implements IGregTechObject {

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
