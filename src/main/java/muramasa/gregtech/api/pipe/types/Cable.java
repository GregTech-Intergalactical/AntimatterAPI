package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.api.data.Cables;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.materials.Material;

public class Cable extends Pipe {

    private int loss, lossInsulated, baseAmps;
    private Tier tier;

    public Cable(Material material, int loss, int lossInsulated, int amps, Tier tier) {
        super(material);
        this.loss = loss;
        this.lossInsulated = lossInsulated;
        this.baseAmps = amps;
        this.tier = tier;
        Cables.add(this);
    }

    public int getLoss() {
        return loss;
    }

    public int getLossInsulated() {
        return lossInsulated;
    }

    public int getBaseAmps() {
        return baseAmps;
    }

    public Tier getTier() {
        return tier;
    }

    public long getVoltage() {
        return tier.getVoltage();
    }
}
