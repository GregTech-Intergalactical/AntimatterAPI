package muramasa.gregtech.api.machines;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashMap;

public class Tier implements IStringSerializable {

    //TODO This entire class needs to be re-thought

    private static HashMap<String, Tier> tierLookup = new HashMap<>();
    private static ArrayList<Tier> tierLookupArray = new ArrayList<>();

    public static Tier[] VALUES;

    public static Tier BRONZE = new Tier(0, "bronze", 0, TextFormatting.WHITE);
    public static Tier STEEL = new Tier(1, "steel", 0, TextFormatting.WHITE);

    public static Tier LV = new Tier(2, "lv", 32, TextFormatting.WHITE);
    public static Tier MV = new Tier(3, "mv", 128, TextFormatting.WHITE);
    public static Tier HV = new Tier(4, "hv", 512, TextFormatting.YELLOW);
    public static Tier EV = new Tier(5, "ev", 2048, TextFormatting.AQUA);
    public static Tier IV = new Tier(6, "iv", 8192, TextFormatting.LIGHT_PURPLE);
    public static Tier MULTI = new Tier(7, "multi", 0, TextFormatting.AQUA);

    static {

    }

    private int id;
    private String name;
    private long voltage;
    private TextFormatting rarityColor;

    public Tier(int id, String name, long voltage, TextFormatting rarityColor) {
        this.id = id;
        this.name = name;
        this.voltage = voltage;
        this.rarityColor = rarityColor;
        tierLookup.put(name, this);
        tierLookupArray.add(id, this);
    }

    public int getId() {
        return id;
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

    public ResourceLocation getBaseTexture() {
        return new ResourceLocation(Ref.MODID, "blocks/machine/base/" + name);
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

    public static Tier[] getMulti() {
        return new Tier[]{MULTI};
    }

    public static Tier get(String tier) {
        return tierLookup.get(tier);
    }

    public static Tier get(int id) {
        return tierLookupArray.get(id);
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
