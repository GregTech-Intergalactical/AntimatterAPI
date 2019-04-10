package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.machines.Tier;

public class SteamMachine extends ItemFluidMachine {

    public SteamMachine(String name, Tier... tiers) {
        super(name);
        setTiers(tiers != null ? tiers : Tier.getSteam());
    }

    public SteamMachine(String name, Class tileClass) {
        this(name);
        setTileClass(tileClass);
    }
}
