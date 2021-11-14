package muramasa.antimatter.recipe.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StackList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyIngredient extends Ingredient {

    private static final Map<ResourceLocation, Getter> getters = new Object2ObjectOpenHashMap<>();

    public interface Getter {
        @Nullable
        Object get(@Nonnull ItemStack item);
    }

    private final Set<MaterialTypeItem<?>> type;
    private final Set<ITag.INamedTag<Item>> itemTags;
    private final Set<IItemProvider> items;
    private final String id;
    private final IMaterialTag[] tags;
    private final Object2BooleanMap<AntimatterToolType> optionalTools;
    private final Set<Material> fixedMats;
    private final boolean inverse;

    protected static PropertyIngredient build(Set<MaterialTypeItem<?>> type, Set<ITag.INamedTag<Item>> itemTags, Set<IItemProvider> items, String id, IMaterialTag[] tags, Set<Material> fixedMats, boolean inverse, Object2BooleanMap<AntimatterToolType> tools) {
        Stream<IItemList> stream = Stream.concat(Stream.concat(itemTags.stream().map(t -> new StackList(TagUtils.nc(t).getAllElements().stream().map(ItemStack::new).collect(Collectors.toList()))), type.stream().map(i -> new StackList((fixedMats.size() == 0 ? i.all().stream() : fixedMats.stream()).filter(t -> {
            boolean ok = t.has(tags);
            boolean types = true;
            if (tools.size() > 0) {
                Set<AntimatterToolType> set = new HashSet<>(t.getToolTypes());
                for (Object2BooleanMap.Entry<AntimatterToolType> entry : tools.object2BooleanEntrySet()) {
                    types &= entry.getBooleanValue() == set.contains(entry.getKey());
                }
            }
            if (inverse) {
                return !ok && types;
            }
            return ok && types;
        }).map(mat -> i.get(mat, 1)).collect(Collectors.toList())))), Stream.of(new StackList(items.stream().map(ItemStack::new).collect(Collectors.toList()))));
        return new PropertyIngredient(stream, type, itemTags, items, id, tags, fixedMats, inverse, tools);
    }

    protected PropertyIngredient(Stream<IItemList> stream, Set<MaterialTypeItem<?>> type, Set<ITag.INamedTag<Item>> itemTags, Set<IItemProvider> items, String id, IMaterialTag[] tags, Set<Material> fixedMats, boolean inverse, Object2BooleanMap<AntimatterToolType> tools) {
        super(stream);
        this.type = type;
        this.id = id;
        this.tags = tags == null ? new MaterialTag[0] : tags;
        this.inverse = inverse;
        this.optionalTools = tools;
        this.itemTags = itemTags;
        this.items = items;
        this.fixedMats = fixedMats;
    }

    //Convenience
    public static PropertyIngredient of(MaterialTypeItem<?> type, String id) {
        return build(ImmutableSet.of(type), Collections.emptySet(), Collections.emptySet(), id, new IMaterialTag[0], Collections.emptySet(), false, new Object2BooleanOpenHashMap<>());
    }

    public Set<MaterialTypeItem<?>> getTypes() {
        return type;
    }

    public Object getMat(ItemStack stack) {
        for (MaterialTypeItem<?> materialType : getTypes()) {
            Material mat = materialType.getMaterialFromStack(stack);
            if (mat != null) {
                return mat;
            }
        }
        for (IItemProvider item : this.items) {
            if (item.asItem() == stack.getItem()) {
                return stack;
            }
        }
        for (ITag.INamedTag<Item> itemTag : this.itemTags) {
            Getter getter = getters.get(itemTag.getName());
            if (getter != null) {
                Object o = getter.get(stack);
                if (o != null) return o;
            }
        }
        return null;
    }

    public static void addGetter(ResourceLocation loc, Getter get) {
        getters.put(loc, get);
    }

    public Set<ITag.INamedTag<Item>> getTags() {
        return itemTags;
    }

    public String getId() {
        return id;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement serialize() {
        JsonObject obj = new JsonObject();
        JsonArray materialArr = new JsonArray();
        for (MaterialTypeItem<?> materialTypeItem : this.type) {
            materialArr.add(materialTypeItem.getId());
        }
        obj.add("material_type", materialArr);
        materialArr = new JsonArray();
        for (ITag.INamedTag<Item> itemTag : this.itemTags) {
            materialArr.add(itemTag.getName().toString());
        }
        obj.add("item_tags", materialArr);
        materialArr = new JsonArray();
        for (IItemProvider item : this.items) {
            ResourceLocation name = item.asItem().getRegistryName();
            if (name != null) materialArr.add(name.toString());
        }
        obj.add("items", materialArr);
        obj.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        obj.addProperty("id", id);
        obj.addProperty("inverse", inverse);
        if (tags.length > 0) {
            JsonArray arr = new JsonArray();
            for (IMaterialTag tag : tags) {
                arr.add(tag.getId());
            }
            obj.add("tags", arr);
        }
        if (optionalTools.size() > 0) {
            JsonObject map = new JsonObject();
            for (Object2BooleanMap.Entry<AntimatterToolType> entry : optionalTools.object2BooleanEntrySet()) {
                map.addProperty(entry.getKey().getId(), entry.getBooleanValue());
            }
            obj.add("tools", map);
        }
        if (fixedMats.size() > 0) {
            JsonArray arr = new JsonArray();
            for (Material mat : this.fixedMats) {
                arr.add(mat.getId());
            }
            obj.add("fixed", arr);
        }
        return obj;
    }

   /* @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null || p_test_1_.isEmpty()) return false;
        if (type.size() > 0) {
            if (p_test_1_.getItem() instanceof MaterialItem) {
                MaterialItem item = ((MaterialItem) p_test_1_.getItem());
                if (item.getType() instanceof MaterialTypeItem) {
                    return this.type.contains((MaterialTypeItem<?>)item.getType());
                }
            }
            for (MaterialTypeItem<?> materialTypeItem : getTypes()) {
                if (materialTypeItem.tryMaterialFromItem(p_test_1_) != null) {
                    return true;
                }
            }
        } else {
            for (ITag.INamedTag<Item> itemTag : this.itemTags) {
                if (itemTag.contains(p_test_1_.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }*/

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Serializer implements IIngredientSerializer<PropertyIngredient> {

        public static Serializer INSTANCE = new Serializer();

        @Override
        public PropertyIngredient parse(PacketBuffer buffer) {
            String id = buffer.readString();
            int size = buffer.readVarInt();
            Set<MaterialTypeItem<?>> items = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                items.add(AntimatterAPI.get(MaterialTypeItem.class, buffer.readString()));
            }
            size = buffer.readVarInt();
            Set<ITag.INamedTag<Item>> t = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                t.add(TagUtils.getItemTag(buffer.readResourceLocation()));
            }
            boolean inverse = buffer.readBoolean();
            IMaterialTag[] tags = new IMaterialTag[buffer.readVarInt()];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = AntimatterAPI.get(IMaterialTag.class, buffer.readString());
            }
            size = buffer.readVarInt();
            Object2BooleanMap<AntimatterToolType> map = new Object2BooleanOpenHashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put(AntimatterAPI.get(AntimatterToolType.class, buffer.readString()), buffer.readBoolean());
            }
            size = buffer.readVarInt();
            Set<Material> fixedMats = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                fixedMats.add(AntimatterAPI.get(Material.class, buffer.readString()));
            }
            size = buffer.readVarInt();
            Set<IItemProvider> itemProviders = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                ResourceLocation name = new ResourceLocation(buffer.readString());
                if (ForgeRegistries.ITEMS.containsKey(name)) {
                    itemProviders.add(ForgeRegistries.ITEMS.getValue(name));
                }
            }
            ItemStack[] stacks = new ItemStack[buffer.readVarInt()];
            for (int i = 0; i < stacks.length; i++) {
                stacks[i] = buffer.readItemStack();
            }
            return new PropertyIngredient(Stream.of(new StackList(Arrays.asList(stacks))), items, t, itemProviders, id, tags, fixedMats, inverse, map);
        }

        @Override
        public PropertyIngredient parse(JsonObject json) {
            JsonArray arr = json.getAsJsonArray("material_type");
            Set<MaterialTypeItem<?>> items = new ObjectArraySet<>(arr.size());
            arr.forEach(el -> items.add(AntimatterAPI.get(MaterialTypeItem.class, el.getAsString())));
            arr = json.getAsJsonArray("item_tags");
            Set<ITag.INamedTag<Item>> itemTags = new ObjectArraySet<>(arr.size());
            arr.forEach(el -> itemTags.add(TagUtils.getItemTag(new ResourceLocation(el.getAsString()))));
            arr = json.getAsJsonArray("items");
            Set<IItemProvider> items2 = new ObjectArraySet<>(arr.size());
            arr.forEach(el -> {
                if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation(el.getAsString())))
                    items2.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(el.getAsString())));
            });
            String ingId = json.get("id").getAsString();
            boolean inverse = json.get("inverse").getAsBoolean();
            IMaterialTag[] tags;
            if (json.has("tags")) {
                tags = Streams.stream(JSONUtils.getJsonArray(json, "tags")).map(t -> AntimatterAPI.get(IMaterialTag.class, t.getAsString())).toArray(IMaterialTag[]::new);
            } else {
                tags = new IMaterialTag[0];
            }
            Object2BooleanMap<AntimatterToolType> map = new Object2BooleanOpenHashMap<>();
            if (json.has("tools")) {
                for (Map.Entry<String, JsonElement> entry : JSONUtils.getJsonObject(json, "tools").entrySet()) {
                    map.put(AntimatterAPI.get(AntimatterToolType.class, entry.getKey()), entry.getValue().getAsBoolean());
                }
            }
            Set<Material> fixedMats = Collections.emptySet();
            if (json.has("fixed")) {
                fixedMats = Streams.stream(JSONUtils.getJsonArray(json, "fixed")).map(t -> AntimatterAPI.get(Material.class, t.getAsString())).collect(Collectors.toSet());
            }
            return PropertyIngredient.build(items, itemTags, items2, ingId, tags, fixedMats, inverse, map);
        }

        @Override
        public void write(PacketBuffer buffer, PropertyIngredient ingredient) {
            buffer.writeString(ingredient.id);
            buffer.writeVarInt(ingredient.type.size());
            for (MaterialTypeItem<?> materialTypeItem : ingredient.type) {
                buffer.writeString(materialTypeItem.getId());
            }
            buffer.writeVarInt(ingredient.itemTags.size());
            for (ITag.INamedTag<Item> itemTag : ingredient.itemTags) {
                buffer.writeResourceLocation(itemTag.getName());
            }
            buffer.writeBoolean(ingredient.inverse);
            buffer.writeVarInt(ingredient.tags.length);
            for (IMaterialTag tag : ingredient.tags) {
                buffer.writeString(tag.getId());
            }
            buffer.writeVarInt(ingredient.optionalTools.size());
            for (Object2BooleanMap.Entry<AntimatterToolType> entry : ingredient.optionalTools.object2BooleanEntrySet()) {
                buffer.writeString(entry.getKey().getId());
                buffer.writeBoolean(entry.getBooleanValue());
            }
            buffer.writeVarInt(ingredient.fixedMats.size());
            for (Material fixedMat : ingredient.fixedMats) {
                buffer.writeString(fixedMat.getId());
            }
            buffer.writeVarInt(ingredient.items.size());
            for (IItemProvider item : ingredient.items) {
                ResourceLocation name = item.asItem().getRegistryName();
                if (name != null) buffer.writeString(name.toString());
            }
            //Needed because tags might not be available on client.
            ItemStack[] items = ingredient.getMatchingStacks();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItemStack(stack);


        }
    }

    public static class Builder {
        private Set<MaterialTypeItem<?>> type;
        private Set<ITag.INamedTag<Item>> itemTags;
        private Set<IItemProvider> items;
        private Set<Material> fixedMats;
        private String id;
        private IMaterialTag[] tags = new IMaterialTag[0];
        private Object2BooleanMap<AntimatterToolType> optionalTools = new Object2BooleanOpenHashMap<>();
        private boolean inverse = false;

        private Builder(String id) {
            this.id = id;
        }

        public Builder types(MaterialTypeItem<?>... types) {
            this.type = new ObjectArraySet<>(types);
            return this;
        }

        public Builder tags(IMaterialTag... tags) {
            this.tags = tags;
            return this;
        }

        public Builder mats(Material... tags) {
            this.fixedMats = Sets.newHashSet(tags);
            return this;
        }

        @SafeVarargs
        public final Builder itemTags(ITag.INamedTag<Item>... tags) {
            this.itemTags = new ObjectArraySet<>(tags);
            return this;
        }

        public final Builder itemStacks(IItemProvider... items) {
            this.items = new ObjectArraySet<>(items);
            return this;
        }

        public Builder inverse() {
            this.inverse = true;
            return this;
        }

        public Builder tool(AntimatterToolType type, boolean has) {
            this.optionalTools.put(type, has);
            return this;
        }

        public PropertyIngredient build() {
            return PropertyIngredient.build(type == null ? Collections.emptySet() : type, itemTags == null ? Collections.emptySet() : itemTags, items == null ? Collections.emptySet() : items, id, tags, fixedMats == null ? Collections.emptySet() : fixedMats, inverse, optionalTools);
        }
    }
}
