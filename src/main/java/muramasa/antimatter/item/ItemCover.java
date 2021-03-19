package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.machine.Tier;

import java.util.Objects;

public class ItemCover extends ItemBasic<ItemCover> implements IHaveCover {

    private ICover cover;

    public ItemCover(String domain, String id, Properties properties) {
        super(domain, id, properties);
    }

    public ItemCover(String domain, String id) {
        super(domain, id);
        cover = Objects.requireNonNull(AntimatterAPI.get(ICover.class, this.getId()));
        if (cover instanceof CoverTiered) {
            throw new RuntimeException("Invalid non-tiered cover instantiation");
        }
        cover.setItem(this);
    }

    public ICover getCover() {
        return cover;
    }

    public ItemCover(String domain, String id, Tier tier) {
        super(domain,id + "_" + tier.getId());
        cover = Objects.requireNonNull(AntimatterAPI.get(ICover.class, this.getId()));
        cover.setItem(this);
    }
}
