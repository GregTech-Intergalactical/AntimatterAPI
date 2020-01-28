package muramasa.antimatter.items;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BasicItem extends Item implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected String namespace, id, tooltip = "";
    protected boolean enabled = true;
    protected Set<ItemTag> tags = new HashSet<>();

    public BasicItem(String namespace, String id, Item.Properties properties) {
        super(properties);
        this.namespace = namespace;
        this.id = id;
        setRegistryName(getNamespace(), getId());
        AntimatterAPI.register(BasicItem.class, this);
    }

    public BasicItem(String namespace, String id) {
        this(namespace, id, new Item.Properties().group(Ref.TAB_ITEMS));
    }

    public BasicItem(String namespace, String id, String tooltip, Item.Properties properties) {
        this(namespace, id, properties);
        this.tooltip = tooltip;
    }

    public BasicItem(String namespace, String id, String tooltip) {
        this(namespace, id, tooltip, new Item.Properties());
    }

    public BasicItem tags(ItemTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEnabled() {
        return enabled || Configs.DATA.ENABLE_ALL_MATERIAL_ITEMS;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent("item." + getId());
    }

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
    public ItemStack asItemStack() {
        return get(1);
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(getNamespace(), "item/standard/" + getId())};
    }
}
