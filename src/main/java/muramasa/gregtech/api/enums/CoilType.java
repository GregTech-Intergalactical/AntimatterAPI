package muramasa.gregtech.api.enums;

import net.minecraft.util.IStringSerializable;

public enum CoilType implements IStringSerializable {

    CUPRONICKEL("cupronickel", 113), //1808
    KANTHAL("kanthal", 169), //2704
    NICHROME("nichrome", 225), //3600
    TUNGSTENSTEEL("tungstensteel", 282), //4512
    HSSG("hssg", 338), //5408
    NAQUADAH("naquadah", 450), //7200
    NAQUADAH_ALLOY("naquadah_alloy", 563), //9008
    FUSION("fusion", 563), //9008
    SUPERCONDUCTOR("superconductor", 563); //9008

    private String name;
    private int heatingCapacity;

    CoilType(String name, int heatingCapacity) {
        this.name = name;
        this.heatingCapacity = heatingCapacity;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getHeatingCapacity() {
        return heatingCapacity;
    }

    public static String[] getAllNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].getName();
        }
        return names;
    }
}
