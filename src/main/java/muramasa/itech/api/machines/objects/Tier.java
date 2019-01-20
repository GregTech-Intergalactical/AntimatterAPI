package muramasa.itech.api.machines.objects;

import muramasa.itech.ITech;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class Tier {

    private static HashMap<String, Tier> tierLookup = new HashMap<>();

    public static Tier STEAM = new Tier("steam", 1);
    public static Tier HPSTEAM = new Tier("hpsteam", 2);
    public static Tier LV = new Tier("lv", 1);
    public static Tier MV = new Tier("mv", 2);
    public static Tier HV = new Tier("hv", 3);
    public static Tier EV = new Tier("ev", 4);
    public static Tier IV = new Tier("iv", 5);
    public static Tier MULTI = new Tier("multi", 1);

    private String name;
    private int level;
    private ResourceLocation baseTexture;

    public Tier(String name, int level) {
        this.name = name;
        this.level = level;
        baseTexture = new ResourceLocation(ITech.MODID, "blocks/machines/base/" + getName());
        tierLookup.put(getName(), this);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    public static Tier[] getSteam() {
        return new Tier[]{STEAM, HPSTEAM};
    }

    public static Tier[] getElectric() {
        return new Tier[]{LV, MV, HV, EV, IV};
    }

    public static Tier[] getMulti() {
        return new Tier[]{MULTI};
    }

    public static Tier get(String tier) {
        return tierLookup.get(tier);
    }

    public static Collection<Tier> getAllBasic() {
        return Arrays.asList(STEAM, HPSTEAM, LV, MV, HV, EV, IV);
    }
}
