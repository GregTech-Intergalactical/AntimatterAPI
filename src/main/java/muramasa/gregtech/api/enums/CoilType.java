package muramasa.gregtech.api.enums;

import net.minecraft.util.IStringSerializable;

public enum CoilType implements IStringSerializable {

    CUPRONICKEL("cupronickel"),
    KANTHAL("kanthal"),
    NICHROME("nichrome"),
    TUNGSTENSTEEL("tungstensteel"),
    HSSG("hssg"),
    NAQUADAH("naquadah"),
    NAQUADAH_ALLOY("naquadah_alloy"),
    FUSION("fusion"),
    SUPERCONDUCTOR("superconductor");

    private String name;

    CoilType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public static String[] getAllNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].getName();
        }
        return names;
    }
}
