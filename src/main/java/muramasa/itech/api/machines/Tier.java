package muramasa.itech.api.machines;

import muramasa.itech.ITech;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class Tier implements IStringSerializable {

    private static HashMap<String, Tier> tierLookup = new HashMap<>();

    public static Tier BRONZE = new Tier("bronze", 1);
    public static Tier STEEL = new Tier("steel", 2);
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
        baseTexture = new ResourceLocation(ITech.MODID, "blocks/machines/base/" + name);
        tierLookup.put(name, this);
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
        return new Tier[]{BRONZE, STEEL};
    }

    public static Tier[] getStandard() {
        return new Tier[]{LV, MV, HV, EV, IV};
    }

    public static Tier[] getMulti() {
        return new Tier[]{MULTI};
    }

    public static Tier get(String tier) {
        return tierLookup.get(tier);
    }

    public static Tier[] getBasic() {
        return new Tier[]{BRONZE, STEEL, LV, MV, HV, EV, IV};
    }

    public static ResourceLocation[] getTextures(Tier[] tiers) {
        ResourceLocation[] textures = new ResourceLocation[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            textures[i] = tiers[i].getBaseTexture();
        }
        return textures;
    }

    public static ResourceLocation[] getAllTextures() {
        return getTextures(tierLookup.values().toArray(new Tier[0]));
    }
}
