package muramasa.antimatter.item;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ItemBasic<T extends ItemBasic<T>> extends Item implements IAntimatterObject, ITextureProvider, IModelProvider {

    @Getter
    protected String domain, id, tooltip = "", subDir = "";
    protected boolean enabled = true;
    protected Set<ItemTag> tags = new ObjectOpenHashSet<>();

    public ItemBasic(String domain, String id, String subDir, Properties properties) {
        super(properties);
        this.domain = domain;
        this.id = id;
        this.subDir = subDir;
        AntimatterAPI.register(getClass(), this);
        if (getClass() != ItemBasic.class){
            AntimatterAPI.register(ItemBasic.class, this);
        }
    }

    public ItemBasic(String domain, String id, Properties properties) {
        this(domain, id, "", properties);
    }

    public ItemBasic(String domain, String id, Class clazz, Properties properties) {
        super(properties);
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(clazz, this);
    }

    public ItemBasic(String domain, String id) {
        this(domain, id, "", new Properties().tab(Ref.TAB_ITEMS));
    }

    public ItemBasic(String domain, String id, String subDir) {
        this(domain, id, subDir, new Properties().tab(Ref.TAB_ITEMS));
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
        return new Texture[]{new Texture(domain, "item/basic/" + subDir + getId())};
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (!tooltip.isEmpty()){
            tooltipComponents.add(Utils.translatable("tooltip." + getDomain() + "." + getId().replace("/", ".")));
        }
    }
}
