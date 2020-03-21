package muramasa.antimatter.item;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemBasic extends Item implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected String domain, id, tooltip = "";
    protected boolean enabled = true;
    protected Set<ItemTag> tags = new HashSet<>();

    public ItemBasic(String domain, String id, Properties properties) {
        super(properties);
        this.domain = domain;
        this.id = id;
        setRegistryName(domain, id);
        AntimatterAPI.register(ItemBasic.class, this);
    }

    public ItemBasic(String domain, String id) {
        this(domain, id, new Properties().group(Ref.TAB_ITEMS));
    }

    public ItemBasic(String domain, String id, String tooltip, Properties properties) {
        this(domain, id, properties);
        this.tooltip = tooltip;
    }

    public ItemBasic(String domain, String id, String tooltip) {
        this(domain, id, tooltip, new Properties().group(Ref.TAB_ITEMS));
    }

    public ItemBasic tags(ItemTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDomain() { return domain; }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEnabled() {
        return enabled || Configs.DATA.ENABLE_ALL_MATERIAL_ITEMS;
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

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return AntimatterAPI.getCoverFromCatalyst(stack) != null;
    }

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
