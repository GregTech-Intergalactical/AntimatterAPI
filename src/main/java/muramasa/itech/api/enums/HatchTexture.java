package muramasa.itech.api.enums;

import net.minecraft.util.IStringSerializable;

import java.util.HashMap;

public enum HatchTexture implements IStringSerializable {

    LV("lv"),
    MV("mv"),
    HV("hv"),
    EV("ev"),
    IV("iv"),
    BLAST_FURNACE("blastfurnace"),
    FUSION_REACTOR("fusionreactor");

    private String name;

    static class MapContainer {
        static HashMap<String, HatchTexture> lookup = new HashMap<>();
    }

    HatchTexture(String name) {
        this.name = name;
        MapContainer.lookup.put(name, this);
    }

    public static HatchTexture get(String name) {
        return MapContainer.lookup.get(name);
    }

    @Override
    public String getName() {
        return name;
    }
}
