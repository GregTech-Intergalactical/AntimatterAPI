package muramasa.antimatter.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.ChatFormatting;

public class Tier implements ISharedAntimatterObject {

    /**
     * Electricity Tiers
     **/
    public static Tier ULV = new Tier(Ref.ID, "ulv", 8, ChatFormatting.WHITE); //Tier 1
    public static Tier LV = new Tier(Ref.ID, "lv", 32, ChatFormatting.WHITE); //Tier 2
    public static Tier MV = new Tier(Ref.ID, "mv", 128, ChatFormatting.WHITE); //Tier 3
    public static Tier HV = new Tier(Ref.ID, "hv", 512, ChatFormatting.YELLOW); //Tier 4
    public static Tier EV = new Tier(Ref.ID, "ev", 2048, ChatFormatting.AQUA); //Tier 5
    public static Tier IV = new Tier(Ref.ID, "iv", 8192, ChatFormatting.LIGHT_PURPLE); //Tier 6
    public static Tier LUV = new Tier(Ref.ID, "luv", 32768, ChatFormatting.LIGHT_PURPLE); //Tier 7
    public static Tier ZPM = new Tier(Ref.ID, "zpm", 131072, ChatFormatting.LIGHT_PURPLE); //Tier 8
    public static Tier UV = new Tier(Ref.ID, "uv", 524288, ChatFormatting.LIGHT_PURPLE); //Tier 9
    public static Tier MAX = new Tier(Ref.ID, "max", 2147483647, ChatFormatting.LIGHT_PURPLE); //Tier 15

    /**
     * Special Tiers
     **/
    //TODO make these Tier 0 and 1?
    public static Tier BRONZE = new Tier(Ref.ID, "bronze", 0, ChatFormatting.WHITE);
    public static Tier STEEL = new Tier(Ref.ID, "steel", 0, ChatFormatting.WHITE);

    private final String domain, id;
    private final int voltage;
    private final ChatFormatting rarityColor;
    private final String baseTexture;

    public Tier(String domain, String id, int voltage, ChatFormatting rarityColor) {
        this.domain = domain;
        this.id = id;
        this.voltage = voltage;
        this.rarityColor = rarityColor;
        this.baseTexture = "block/machine/base/" + id;
        AntimatterAPI.register(Tier.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getIntegerId() {
        switch (this.getId()) {
            case "ulv":
                return 0;
            case "lv":
                return 1;
            case "mv":
                return 2;
            case "hv":
                return 3;
            case "ev":
                return 4;
            case "iv":
                return 5;
            default:
                return 1;
        }
    }

    public int getVoltage() {
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
        return new Tier[]{ULV, LV, MV, HV, EV, IV, LUV, ZPM, UV, MAX};
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

    public static Tier getTier(int voltage) {
        for (Tier tier : getAllElectric()) {
            if (voltage <= tier.getVoltage()) {
                return tier;
            }
        }
        return ULV;
    }
}
