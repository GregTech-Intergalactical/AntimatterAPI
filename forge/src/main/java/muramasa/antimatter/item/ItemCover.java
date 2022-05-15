package muramasa.antimatter.item;

import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.texture.Texture;

import java.util.Objects;

public class ItemCover extends ItemBasic<ItemCover> implements IHaveCover {

    private final CoverFactory cover;

    private final Tier tier;

    private Texture[] override;

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

    public ItemCover texture(Texture... texture) {
        this.override = texture;
        return this;
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    public CoverFactory getCover() {
        return cover;
    }

    @Override
    public Texture[] getTextures() {
        if (override == null) return super.getTextures();
        return override;
    }
}
