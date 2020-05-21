package muramasa.antimatter.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.types.Component;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemComponent<T extends Component<?>> extends ItemBasic<ItemComponent<T>> implements IColorHandler {

    protected Component<?> type;
    protected Tier tier;

    public ItemComponent(Component<?> type, Tier tier, Properties properties) {
        super(type.getDomain(), type.getId() + '_' + tier.getId(), properties);
        this.type = type;
        this.tier = tier;
    }

    public ItemComponent(Component<?> type, Tier tier) {
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
        int layers = getType().getLayers();
        textures.add(new Texture(getDomain(), "item/component/".concat(type.getId())));
        if (layers == 1) textures.add(new Texture(getDomain(), "item/component/overlay/".concat(type.getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/component/overlay/", type.getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }
}
