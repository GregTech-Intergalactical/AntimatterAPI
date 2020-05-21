package muramasa.antimatter.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTiered<T extends ItemFactory<?>> extends Item implements IAntimatterObject, ITextureProvider, IModelProvider, IColorHandler {

    protected ItemFactory<?> type;
    protected Tier tier;

    public ItemTiered(ItemFactory<?> type, Tier tier, Properties properties) {
        super(properties);
        this.type = type;
        this.tier = tier;
    }

    public ItemTiered(ItemFactory<?> type, Tier tier) {
        this(type, tier, new Properties().group(Ref.TAB_ITEMS));
    }

    @Override
    public String getDomain() {
        return type.getDomain();
    }

    @Override
    public String getId() {
        return type.getId();
    }

    public T getType() {
        return (T) type;
    }

    public Tier getTier() {
        return tier;
    }

    public String getTooltip() {
        return "";//type.getTooltip();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? tier.getColor() : -1;
    }

    @Override
    public Texture[] getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        int layers = getType().getLayers();
        textures.add(new Texture(getDomain(), "item/component/".concat(getId())));
        if (layers == 1) textures.add(new Texture(getDomain(), "item/component/overlay/".concat(getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/component/overlay/", getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }
}
