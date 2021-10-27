package muramasa.antimatter.cover;

import muramasa.antimatter.machine.Tier;

public interface IHaveCover {
    CoverFactory getCover();

    default Tier getTier() {
        return null;
    }
}
