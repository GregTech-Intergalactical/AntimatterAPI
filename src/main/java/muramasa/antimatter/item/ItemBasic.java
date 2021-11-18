package muramasa.antimatter.item;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Set;

public class ItemBasic<T extends ItemBasic<T>> extends Item implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected String domain, id, tooltip = "";
    protected boolean enabled = true;
    protected Set<ItemTag> tags = new ObjectOpenHashSet<>();

    public ItemBasic(String domain, String id, Properties properties) {
        super(properties);
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(getClass(), this);
    }

    public ItemBasic(String domain, String id, Class clazz, Properties properties) {
        super(properties);
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(clazz, this);
    }

    public ItemBasic(String domain, String id) {
        this(domain, id, new Properties().tab(Ref.TAB_ITEMS));
    }

    public T tip(String tooltip) {
        this.tooltip = tooltip;
        return (T) this;
    }

    public T tags(ItemTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return (T) this;
    }

    @Override
    public String getDomain() {
        return this instanceof ISharedAntimatterObject ? Ref.SHARED_ID : domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEnabled() {
        return enabled || AntimatterConfig.DATA.ALL_MATERIAL_ITEMS;
    }

    /*
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("item." + getId());
    }
     */

    public boolean isEqual(ItemStack stack) {
        return stack.getItem() == this;
    }

//    public static boolean doesShowExtendedHighlight(ItemStack stack) {
//        return AntimatterAPI.getCoverFromCatalyst(stack) != null; //TODO: reimplement?
//    }

    public ItemStack get(int count) {
        //TODO replace consumeTag with flag system
        if (count == 0) return Utils.addNoConsumeTag(new ItemStack(this, 1));
        return new ItemStack(this, count);
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(domain, "item/basic/" + getId())};
    }
}
