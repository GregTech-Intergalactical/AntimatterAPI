package muramasa.gtu.api.cover;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.common.Data;
import net.minecraft.item.ItemStack;

public class CoverConveyor extends Cover {

    protected Tier tier;

    public CoverConveyor(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String getId() {
        return "conveyor";
    }

    @Override
    public ItemStack getDroppedStack() {
        switch (tier.getId()) { //TODO maybe a better way to do this? but for now, it works.
            case "lv": return Data.ConveyorLV.get(1);
            case "mv": return Data.ConveyorMV.get(1);
            case "hv": return Data.ConveyorHV.get(1);
            case "ev": return Data.ConveyorEV.get(1);
            case "iv": return Data.ConveyorIV.get(1);
            default: return Data.ConveyorLV.get(1);
        }
    }
}
