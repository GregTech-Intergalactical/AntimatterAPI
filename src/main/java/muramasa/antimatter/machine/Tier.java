package muramasa.antimatter.machine;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class Tier implements IAntimatterObject {

    //TODO ->
    // refactor the concept of this class
    // Separate class to MachineTier and VoltageTier
    // Both of class should have a base texture, color
    // Colors for VoltageTier should be extracted from the texture at resources dir (Check gti:other/voltage/*)
    // move Tiers to GT?

    /** Electricity Tiers **/
    public static Tier ULV = new Tier("gti", "ulv", 8, 0x8b4513, TextFormatting.WHITE); //Tier 1
    public static Tier LV = new Tier("gti", "lv", 32, 0xafafaf, TextFormatting.WHITE); //Tier 2
    public static Tier MV = new Tier("gti", "mv", 128, 0xff6625, TextFormatting.WHITE); //Tier 3
    public static Tier HV = new Tier("gti", "hv", 512, 0xffaf2c, TextFormatting.YELLOW); //Tier 4
    public static Tier EV = new Tier("gti", "ev", 2048, 0x8ca4ff, TextFormatting.AQUA); //Tier 5
    public static Tier IV = new Tier("gti", "iv", 8192, 0xffffff, TextFormatting.LIGHT_PURPLE); //Tier 6
    public static Tier LUV = new Tier("gti", "luv", 32768, 0xffa7fe, TextFormatting.LIGHT_PURPLE); //Tier 7
    public static Tier ZPM = new Tier("gti", "zpm", 131072, 0xbeffbe, TextFormatting.LIGHT_PURPLE); //Tier 8
    public static Tier UV = new Tier("gti", "uv", 524288, 0xff0000, TextFormatting.LIGHT_PURPLE); //Tier 9
    public static Tier MAX = new Tier("gti", "max", 2147483647, 0x45d0, TextFormatting.LIGHT_PURPLE); //Tier 15

    /** Special Tiers **/
    //TODO make these Tier 0 and 1?
    public static Tier BRONZE = new Tier("gti", "bronze", 0, 0x3cdb37, TextFormatting.WHITE);
    public static Tier STEEL = new Tier("gti", "steel", 0, 0x838c62, TextFormatting.WHITE);

    private String domain, id;
    private int voltage, color;
    private TextFormatting rarityColor;
    private Texture baseTexture;

    public Tier(String domain, String id, int voltage, int color, TextFormatting rarityColor) {
        this.domain = domain;
        this.id = id;
        this.voltage = voltage;
        this.color = color;
        this.rarityColor = rarityColor;
        this.baseTexture = new Texture(domain, "block/machine/base/" + id);
        AntimatterAPI.register(Tier.class, getId(), this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getIntegerId() {
        switch (this.getId()) {
            case "ulv": return 0;
            case "lv": return 1;
            case "mv": return 2;
            case "hv": return 3;
            case "ev": return 4;
            case "iv": return 5;
            default: return 1;
        }
    }

    public int getVoltage() {
        return voltage;
    }

    public int getColor() {
        return color;
    }

    public TextFormatting getRarityFormatting() {
        return rarityColor;
    }

    public Texture getBaseTexture() {
        return baseTexture;
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

    public static Tier[] getBasic() {
        return new Tier[]{BRONZE, STEEL, LV, MV, HV, EV, IV};
    }

    public static Tier getMax() {
        return Tier.IV; //TODO update...
    }

    public static Texture[] getTextures(Tier... tiers) {
        Texture[] textures = new Texture[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            textures[i] = tiers[i].getBaseTexture();
        }
        return textures;
    }
}
