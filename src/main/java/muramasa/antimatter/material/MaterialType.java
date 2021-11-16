package muramasa.antimatter.material;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MaterialType<T> implements IMaterialTag, ISharedAntimatterObject {

    protected final String id;
    protected int unitValue, layers;
    protected boolean generating = true, blockType, visible, splitName;
    protected final Set<Material> materials = new ObjectLinkedOpenHashSet<>(); //Linked to preserve insertion order for JEI
    protected final Map<MaterialType<?>, ITag.INamedTag<?>> tagMap = new Object2ObjectOpenHashMap<>();
    protected T getter;
    protected final BiMap<Material, Item> OVERRIDES = HashBiMap.create();
    protected final Set<IMaterialTag> dependents = new ObjectLinkedOpenHashSet<>();


    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        this.tagMap.put(this, tagFromString(Utils.getConventionalMaterialType(this)));
        register(MaterialType.class, getId());
    }

    protected ITag.INamedTag<?> tagFromString(String name) {
        return TagUtils.getForgeItemTag(name);
    }

    public MaterialType<T> nonGen() {
        generating = false;
        return this;
    }

    /**
     * Adds a list of dependent flags, that is all of these flags are added as well.
     *
     * @param tags the list of tags.
     * @return this
     */
    public void dependents(IMaterialTag... tags) {
        dependents.addAll(Arrays.asList(tags));
    }

    /**
     * Forces these tags to not generate, assuming they have a replacement.
     */
    public void forceOverride(Material mat, Item replacement) {
        OVERRIDES.put(mat, replacement);
        this.add(mat);
        AntimatterAPI.addReplacement(getMaterialTag(mat), replacement);
    }

    public Material getMaterialFromStack(ItemStack stack) {
        if (stack.getItem() instanceof MaterialItem) {
            return ((MaterialItem) stack.getItem()).getMaterial();
        }
        return OVERRIDES.inverse().get(stack.getItem());
    }

    @SuppressWarnings("unchecked")
    public ITag.INamedTag<Item> getMaterialTag(Material m) {
        return (ITag.INamedTag<Item>) tagFromString(String.join("", Utils.getConventionalMaterialType(this), "/", m.getId()));
    }

    public RecipeIngredient getMaterialIngredient(Material m, int count) {
        return RecipeIngredient.of(getMaterialTag(m), count);
    }

    public MaterialType<T> blockType() {
        blockType = true;
        this.tagMap.put(this, TagUtils.getForgeBlockTag(Utils.getConventionalMaterialType(this)));
        return this;
    }

    public MaterialType<T> unSplitName() {
        splitName = false;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public int getLayers() {
        return layers;
    }

    public <T> ITag.INamedTag<T> getTag() {
        return (ITag.INamedTag<T>) tagMap.get(this);
    }

    public MaterialType<T> set(T getter) {
        this.getter = getter;
        return this;
    }

    @Override
    public Set<IMaterialTag> dependents() {
        return this.dependents;
    }

    public T get() {
        return getter;
    }

    @Override
    public Set<Material> all() {
        return materials;
    }

    public boolean isVisible() {
        return visible || AntimatterConfig.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public boolean allowGen(Material material) {
        return generating && materials.contains(material) && AntimatterAPI.getReplacement(this, material) == null;
    }

    public boolean isSplitName() {
        return splitName;
    }

    @Override
    public String toString() {
        return getId();
    }

    public BiMap<Material, Item> getOVERRIDES() {
        return OVERRIDES;
    }

    private static ImmutableMap<Item, Material> tooltipCache;

    @OnlyIn(Dist.CLIENT)
    public static void buildTooltips() {
        ImmutableMap.Builder<Item, Material> builder = ImmutableMap.builder();
        AntimatterAPI.all(MaterialType.class, t -> {
            BiMap<Item, Material> map = t.getOVERRIDES().inverse();
            for (Map.Entry<Item, Material> entry : map.entrySet()) {
                builder.put(entry.getKey(), entry.getValue());
            }
        });
        tooltipCache = builder.build();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    protected static void onTooltipAdd(final ItemTooltipEvent ev) {
        if (ev.getPlayer() == null) return;
        if (tooltipCache == null) return;
        if (ev.getItemStack().getItem() instanceof MaterialItem) return;
        if (ev.getItemStack().getItem().getRegistryName().getNamespace().equals(Ref.ID)) return;

        Material mat = tooltipCache.get(ev.getItemStack().getItem());
        if (mat == null) return;
        if (!Screen.hasShiftDown()) {
            ev.getToolTip().add(new StringTextComponent("Hold Shift to show formula").mergeStyle(TextFormatting.AQUA).mergeStyle(TextFormatting.ITALIC));
        } else {
            ev.getToolTip().add(new StringTextComponent(mat.getChemicalFormula()).mergeStyle(TextFormatting.DARK_AQUA));
        }
    }
}
