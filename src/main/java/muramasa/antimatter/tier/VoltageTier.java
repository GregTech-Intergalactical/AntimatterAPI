package muramasa.antimatter.tier;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.texture.Texture;

public class VoltageTier implements IAntimatterTier {

    protected String domain, id;
    protected int voltage;
    protected Texture baseTexture;

    public VoltageTier(String domain, String id, int voltage) {
        this.domain = domain;
        this.id = id;
        this.voltage = voltage;
        this.baseTexture = new Texture(getDomain(), "tier/".concat(getId()));
        AntimatterAPI.register(getClass(), "voltage_".concat(getId()), this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public int getVoltage() {
        return voltage;
    }

    @Override
    public Texture getBaseTexture() {
        return baseTexture;
    }
}
