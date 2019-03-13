package muramasa.gregtech.api.machines;

import muramasa.gregtech.api.texture.Texture;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Tier implements IStringSerializable {

    private static HashMap<String, Tier> tierLookup = new HashMap<>();
    private static ArrayList<Tier> tierLookupArray = new ArrayList<>();

    public static int lastInternalId = 0;

    /** Electricity Tiers **/
    public static Tier ULV = new Tier("ulv", 8, TextFormatting.WHITE);
    public static Tier LV = new Tier("lv", 32, TextFormatting.WHITE);
    public static Tier MV = new Tier("mv", 128, TextFormatting.WHITE);
    public static Tier HV = new Tier("hv", 512, TextFormatting.YELLOW);
    public static Tier EV = new Tier("ev", 2048, TextFormatting.AQUA);
    public static Tier IV = new Tier("iv", 8192, TextFormatting.LIGHT_PURPLE);
    public static Tier LUV = new Tier("luv", 32768, TextFormatting.LIGHT_PURPLE);
    public static Tier ZPM = new Tier("zpm", 131072, TextFormatting.LIGHT_PURPLE);
    public static Tier UV = new Tier("uv", 524288, TextFormatting.LIGHT_PURPLE);
    public static Tier MAX = new Tier("max", 2147483647, TextFormatting.LIGHT_PURPLE);

    /** Special Tiers **/
    public static Tier BRONZE = new Tier("bronze", 0, TextFormatting.WHITE);
    public static Tier STEEL = new Tier("steel", 0, TextFormatting.WHITE);
    public static Tier MULTI = new Tier("multi", 0, TextFormatting.AQUA);

    private int internalId;
    private String name;
    private long voltage;
    private TextFormatting rarityColor;

    public Tier(String name, long voltage, TextFormatting rarityColor) {
        internalId = lastInternalId++;
        this.name = name;
        this.voltage = voltage;
        this.rarityColor = rarityColor;
        tierLookup.put(name, this);
        tierLookupArray.add(internalId, this);
    }

    public int getInternalId() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public long getVoltage() {
        return voltage;
    }

    public TextFormatting getRarityColor() {
        return rarityColor;
    }

    public Texture getBaseTexture() {
        return new Texture("blocks/machine/base/" + name);
    }

    public static int getCount() {
        return tierLookup.size();
    }

    public static Tier[] getSteam() {
        return new Tier[]{BRONZE, STEEL};
    }

    public static Tier[] getStandard() {
        return new Tier[]{LV, MV, HV, EV, IV};
    }

    public static Tier[] getAllElectric() {
        return new Tier[]{ULV, LV, MV, HV, EV, IV, LUV, ZPM, UV, MAX};
    }

    public static Tier[] getMulti() {
        return new Tier[]{MULTI};
    }

    public static Tier get(String name) {
        return tierLookup.get(name);
    }

    public static Tier get(int id) {
        return tierLookupArray.get(id);
    }

    public static Tier[] getBasic() {
        return new Tier[]{BRONZE, STEEL, LV, MV, HV, EV, IV};
    }

    public static Tier getMax() {
        return Tier.IV; //TODO update...
    }

    public static Collection<Tier> getAll() {
        return tierLookup.values();
    }

    public static Texture[] getTextures(Tier... tiers) {
        Texture[] textures = new Texture[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            textures[i] = tiers[i].getBaseTexture();
        }
        return textures;
    }

    public static Texture[] getAllTextures() {
        return getTextures(tierLookup.values().toArray(new Tier[0]));
    }
}
