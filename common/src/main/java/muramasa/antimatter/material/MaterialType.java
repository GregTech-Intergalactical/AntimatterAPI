package muramasa.antimatter.material;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class MaterialType<T> implements IMaterialTag, ISharedAntimatterObject, IRegistryEntryProvider {

    protected final String id;
    protected int layers;
    protected long unitValue;
    protected boolean generating = true, blockType, visible, splitName;
    protected final Set<Material> materials = new ObjectLinkedOpenHashSet<>(); //Linked to preserve insertion order for JEI
    protected final Map<MaterialType<?>, TagKey<?>> tagMap = new Object2ObjectOpenHashMap<>();
    protected T getter;
    private boolean hidden = false;
    protected final BiMap<Material, Supplier<Item>> replacements = HashBiMap.create();
    protected final Set<IMaterialTag> dependents = new ObjectLinkedOpenHashSet<>();
    //since we have two instances stored in antimatter.
    protected boolean hasRegistered;
    protected boolean ignoreTextureSets = false;

    public MaterialType(String id, int layers, boolean visible, long unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        this.splitName = id.contains("_");
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
    public void replacement(Material mat, Supplier<Item> replacement) {
        if (!mat.enabled) return;
        replacements.put(mat, replacement);
        this.add(mat);
        AntimatterAPI.addReplacement(getMaterialTag(mat), replacement);
    }

    public Material getMaterialFromStack(ItemStack stack) {
        if (stack.getItem() instanceof MaterialItem item) {
            if (item.getType() == this) return item.getMaterial();
            return null;
        }

        //TODO better fix
        //return replacements.inverse().get(stack.getItem());
        for (Map.Entry<Material, Supplier<Item>> entry : replacements.entrySet()) {
            Item item = entry.getValue().get();
            if (item == stack.getItem()){
                return entry.getKey();
            }
        }
        // gets material from other mod items using the tags
        for (TagKey<Item> tagKey : stack.getItem().builtInRegistryHolder().tags().toList()){
            String prefix = this.getTag().location().getPath() + "/";
            if (tagKey.location().getNamespace().equals(this.getTag().location().getNamespace()) && tagKey.location().getPath().contains(prefix)){
                Material material = Material.get(tagKey.location().getPath().replace(prefix, ""));
                if (material != Material.NULL){
                    return material;
                }
            }
        }
        stack.getItem().builtInRegistryHolder().tags().forEach(t -> {

        });
        return null;
    }

    public boolean hidden() {
        return hidden;
    }

    public MaterialType<T> setHidden() {
        this.hidden = true;
        return this;
    }

    public boolean ignoreTextureSets(){
        return ignoreTextureSets;
    }

    public MaterialType<T> setIgnoreTextureSets() {
        ignoreTextureSets = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public TagKey<Item> getMaterialTag(Material m) {
        return (TagKey<Item>) tagFromString(String.join("", Utils.getConventionalMaterialType(this), "/", (getId().equals("raw_ore_block") ? "raw_" : ""), m.getId()));
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
        this.tagMap.put(this, tagFromString(Utils.getConventionalMaterialType(this)));
        return this;
    }

    @Override
    public void add(Material... m) {
        for (Material m2 : m) {
            if (m2.enabled) {
                all().add(m2);
                m2.types.add(this);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public long getUnitValue() {
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
        return visible || AntimatterConfig.SHOW_ALL_MATERIAL_ITEMS.get();
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

    public BiMap<Material, Supplier<Item>> getReplacements() {
        return replacements;
    }

    public static ImmutableMap<Item, Tuple<MaterialType, Material>> tooltipCache;

    @Environment(EnvType.CLIENT)
    public static void buildTooltips() {
        ImmutableMap.Builder<Item, Tuple<MaterialType, Material>> builder = ImmutableMap.builder();
        AntimatterAPI.all(MaterialType.class, t -> {
            BiMap<Supplier<Item>, Material> map = t.getReplacements().inverse();
            for (Map.Entry<Supplier<Item>, Material> entry : map.entrySet()) {
                builder.put(entry.getKey().get(), new Tuple<>(t, entry.getValue()));
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
