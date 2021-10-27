package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.machine.Tier;

import java.util.Objects;

public class ItemCover extends ItemBasic<ItemCover> implements IHaveCover {

    private final CoverFactory cover;

    private final Tier tier;

    public ItemCover(String domain, String id) {
        super(domain, id);
        cover = Objects.requireNonNull(AntimatterAPI.get(CoverFactory.class, id, this.getDomain()));
        this.tier = null;
    }

    public ItemCover(String domain, String id, Tier tier) {
        super(domain, id + "_" + tier.getId());
        cover = Objects.requireNonNull(AntimatterAPI.get(CoverFactory.class, id, this.getDomain()));
        this.tier = tier;
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    public CoverFactory getCover() {
        return cover;
    }
}
