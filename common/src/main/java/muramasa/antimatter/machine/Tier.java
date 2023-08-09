package muramasa.antimatter.machine;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.ChatFormatting;

public class Tier implements ISharedAntimatterObject {

    /**
     * Electricity Tiers
     **/
    public static Tier ULV = new Tier(Ref.ID, "ulv", 8, ChatFormatting.DARK_RED, 0); //Tier 1
    public static Tier LV = new Tier(Ref.ID, "lv", 32, ChatFormatting.GRAY, 1); //Tier 2
    public static Tier MV = new Tier(Ref.ID, "mv", 128, ChatFormatting.GOLD, 2); //Tier 3
    public static Tier HV = new Tier(Ref.ID, "hv", 512, ChatFormatting.YELLOW, 3); //Tier 4
    public static Tier EV = new Tier(Ref.ID, "ev", 2048, ChatFormatting.AQUA, 4); //Tier 5
    public static Tier IV = new Tier(Ref.ID, "iv", 8192, ChatFormatting.GREEN, 5); //Tier 6
    public static Tier LUV = new Tier(Ref.ID, "luv", 32768, ChatFormatting.LIGHT_PURPLE, 6); //Tier 7
    public static Tier ZPM = new Tier(Ref.ID, "zpm", 131072, ChatFormatting.BLUE, 7); //Tier 8
    public static Tier UV = new Tier(Ref.ID, "uv", 524288, ChatFormatting.DARK_GREEN, 8); //Tier 9
    public static Tier UHV = new Tier(Ref.ID, "uhv", 2_097_152, ChatFormatting.DARK_RED, 9); //Tier 10
    public static Tier UEV = new Tier(Ref.ID, "uev", 8_388_608, ChatFormatting.DARK_PURPLE, 10); //Tier 11
    public static Tier UIV = new Tier(Ref.ID, "uiv", 35_544_432, ChatFormatting.DARK_BLUE, 11); //Tier 12
    public static Tier UMV = new Tier(Ref.ID, "umv", 134_217_728, ChatFormatting.RED, 12); //Tier 13
    public static Tier UXV = new Tier(Ref.ID, "uxv", 536_870_912, ChatFormatting.DARK_RED, 13); //Tier 14
    public static Tier MAX = new Tier(Ref.ID, "max", 2_147_483_648L, ChatFormatting.WHITE, 14); //Tier 15

    /**
     * Special Tiers
     **/
    //TODO make these Tier 0 and 1?
    public static Tier BRONZE = new Tier(Ref.ID, "bronze", 0, ChatFormatting.WHITE);
    public static Tier STEEL = new Tier(Ref.ID, "steel", 0, ChatFormatting.WHITE);
    public static Tier NONE = new Tier(Ref.ID, "none", 0, ChatFormatting.WHITE);

    private final String domain, id;
    private final long voltage;

    private final int tierNumber;
    private final ChatFormatting rarityColor;
    private final String baseTexture;

    public Tier(String domain, String id, long voltage, ChatFormatting rarityColor){
        this(domain, id, voltage, rarityColor, 1);
    }

    public Tier(String domain, String id, long voltage, ChatFormatting rarityColor, int tierNumber) {
        this.domain = domain;
        this.id = id;
        this.voltage = voltage;
        this.rarityColor = rarityColor;
        this.baseTexture = "block/machine/base/" + id;
        this.tierNumber = tierNumber;
        AntimatterAPI.register(Tier.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getIntegerId() {
        return tierNumber;
    }

    public long getVoltage() {
        return voltage;
    }

    public ChatFormatting getRarityFormatting() {
        return rarityColor;
    }


    public Texture getBaseTexture(String domain) {
        return new Texture(domain, baseTexture);
    }

    public static Tier[] getSteam() {
        return new Tier[]{BRONZE, STEEL};
    }

    public static Tier[] getStandard() {
        return new Tier[]{LV, MV, HV, EV, IV};
    }

    public static Tier[] getAllElectric() {
        return new Tier[]{ULV, LV, MV, HV, EV, IV, LUV, ZPM, UV, UHV};
    }

    public static Tier[] getBasic() {
        return new Tier[]{BRONZE, STEEL, LV, MV, HV, EV, IV};
    }

    public static Tier getMax() {
        return Tier.IV; //TODO update...
    }

    public static Texture[] getTextures(String domain, Tier... tiers) {
        Texture[] textures = new Texture[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            textures[i] = tiers[i].getBaseTexture(domain);
        }
        return textures;
    }

    public static Tier getTier(long voltage) {
        for (Tier tier : getAllElectric()) {
            if (voltage <= tier.getVoltage()) {
                return tier;
            }
        }
        return ULV;
    }
}
