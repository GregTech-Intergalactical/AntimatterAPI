package muramasa.antimatter.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.types.ItemType;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemComponent<T extends ItemType<?>> extends ItemBasic<ItemComponent<T>> implements IColorHandler {

    protected ItemType<?> type;
    protected Tier tier;
    protected boolean layered;

    private ItemComponent(ItemType<?> type, Tier tier, Properties properties) {
        super(type.getDomain(), type.getId() + '_' + tier.getId(), properties);
        this.type = type;
        this.tier = tier;
        this.tooltip = getType().getTooltip(tier);
        this.layered = getType().isLayered(tier);
    }

    public ItemComponent(ItemType<?> type, Tier tier) {
        this(type, tier, new Properties().group(Ref.TAB_ITEMS));
    }

    public T getType() {
        return (T) type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? tier.getColor() : -1;
    }

    @Override
    public Texture[] getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        textures.add(new Texture(getDomain(), "item/component/".concat(type.getId())));
        textures.add(new Texture(getDomain(), "item/component/overlay/".concat(type.getId())));
        if (isLayered()) textures.add(new Texture(getDomain(), "item/component/overlay/" + type.getId() + '_' + tier.getId()));
        return textures.toArray(new Texture[textures.size()]);
    }

    public boolean isLayered() {
        return layered;
    }
}
