package muramasa.antimatter.material;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaterialType<T> implements IMaterialTag, ISharedAntimatterObject, IRegistryEntryProvider {

    protected final String id;
    protected int unitValue, layers;
    protected boolean generating = true, blockType, visible, splitName;
    protected final Set<Material> materials = new ObjectLinkedOpenHashSet<>(); //Linked to preserve insertion order for JEI
    protected final Map<MaterialType<?>, TagKey<?>> tagMap = new Object2ObjectOpenHashMap<>();
    protected T getter;
    private boolean hidden = false;
    protected final BiMap<Material, Item> replacements = HashBiMap.create();
    protected final Set<IMaterialTag> dependents = new ObjectLinkedOpenHashSet<>();
    //since we have two instances stored in antimatter.
    protected boolean hasRegistered;

    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        this.tagMap.put(this, tagFromString(Utils.getConventionalMaterialType(this)));
        register(MaterialType.class, getId());
    }

    protected TagKey<?> tagFromString(String name) {
        return TagUtils.getForgelikeItemTag(name);
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
    public void replacement(Material mat, Item replacement) {
        replacements.put(mat, replacement);
        this.add(mat);
        AntimatterAPI.addReplacement(getMaterialTag(mat), replacement);
    }

    public Material getMaterialFromStack(ItemStack stack) {
        if (stack.getItem() instanceof MaterialItem) {
            MaterialItem item = (MaterialItem) stack.getItem();
            if (item.getType() == this) return item.getMaterial();
            return null;
        }
        return replacements.inverse().get(stack.getItem());
    }

    public boolean hidden() {
        return hidden;
    }

    public MaterialType<T> setHidden() {
        this.hidden = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public TagKey<Item> getMaterialTag(Material m) {
        return (TagKey<Item>) tagFromString(String.join("", Utils.getConventionalMaterialType(this), "/", m.getId()));
    }

    public RecipeIngredient getMaterialIngredient(Material m, int count) {
        return RecipeIngredient.of(getMaterialTag(m), count);
    }

    public MaterialType<T> blockType() {
        blockType = true;
        this.tagMap.put(this, TagUtils.getForgelikeBlockTag(Utils.getConventionalMaterialType(this)));
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

    public <T> TagKey<T> getTag() {
        return (TagKey<T>) tagMap.get(this);
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

    public BiMap<Material, Item> getReplacements() {
        return replacements;
    }

    public static ImmutableMap<Item, Tuple<MaterialType, Material>> tooltipCache;

    @OnlyIn(Dist.CLIENT)
    public static void buildTooltips() {
        ImmutableMap.Builder<Item, Tuple<MaterialType, Material>> builder = ImmutableMap.builder();
        AntimatterAPI.all(MaterialType.class, t -> {
            BiMap<Item, Material> map = t.getReplacements().inverse();
            for (Map.Entry<Item, Material> entry : map.entrySet()) {
                builder.put(entry.getKey(), new Tuple<>(t, entry.getValue()));
            }
        });
        tooltipCache = builder.build();
    }

    public static void addTooltip(ItemStack stack, List<Component> tooltips, Player player, TooltipFlag flag){
        if (player == null) return;
        if (tooltipCache == null) return;
        var mat = tooltipCache.get(stack.getItem());
        if (mat == null) {
            if (stack.getItem() instanceof MaterialItem item) {
                MaterialItem.addTooltipsForMaterialItems(stack, item.material, item.type, player.level, tooltips, flag);
            }
            return;
        }
        MaterialItem.addTooltipsForMaterialItems(stack, mat.getB(), mat.getA(), player.level, tooltips, flag);
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {

    }

    protected boolean doRegister() {
        boolean old = hasRegistered;
        hasRegistered = true;
        return !old;
    }
}
