package muramasa.antimatter.material;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MaterialType<T> implements IMaterialTag, IAntimatterObject {

    protected String id;
    protected int unitValue, layers;
    protected boolean generating = true, blockType, visible;
    protected Set<Material> materials = new LinkedHashSet<>(); //Linked to preserve insertion order for JEI
    protected Map<MaterialType<?>, ITag.INamedTag<?>> tagMap = new Object2ObjectOpenHashMap<>();
    protected T getter;
    protected BiMap<Material, Item> OVERRIDES = HashBiMap.create();

    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        this.tagMap.put(this, TagUtils.getForgeItemTag(Utils.getConventionalMaterialType(this)));
        register(MaterialType.class, getId());
    }

    public MaterialType<T> nonGen() {
        generating = false;
        return this;
    }

    /**
     * Forces these tags to not generate, assuming they have a replacement.
     */
    public void forceOverride(Material mat, Item replacement) {
        OVERRIDES.put(mat, replacement);
        this.add(mat);
        AntimatterAPI.addReplacement(getMaterialTag(mat), replacement);
    }

    public Material tryMaterialFromItem(ItemStack stack) {
        if (stack.getItem() instanceof MaterialItem) {
            return ((MaterialItem) stack.getItem()).getMaterial();
        }
        return OVERRIDES.inverse().get(stack.getItem());
    }

    public ITag.INamedTag<Item> getMaterialTag(Material m) {
        return TagUtils.getForgeItemTag(String.join("", Utils.getConventionalMaterialType(this), "/", m.getId()));
    }

    public RecipeIngredient getMaterialIngredient(Material m, int count) {
        return RecipeIngredient.of(getMaterialTag(m),count);
    }

    public MaterialType<T> blockType() {
        blockType = true;
        this.tagMap.put(this, TagUtils.getForgeBlockTag(Utils.getConventionalMaterialType(this)));
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

    @Override
    public String toString() {
        return getId();
    }
}
