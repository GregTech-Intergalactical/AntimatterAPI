package muramasa.antimatter.tier;

import muramasa.antimatter.texture.Texture;

public class MachineTier extends VoltageTier {

    public MachineTier(String domain, String id, int voltage) {
        super(domain, id, voltage);
        this.baseTexture = new Texture(getDomain(), "block/machine/base/".concat(getId()));
    }
}
