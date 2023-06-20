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
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.*;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyIngredient extends Ingredient {

    private static final Map<ResourceLocation, Getter> getters = new Object2ObjectOpenHashMap<>();

    public interface Getter {
        @Nullable
        Object get(@Nonnull ItemStack item);
    }

    private final Set<MaterialTypeItem<?>> type;
    private final Set<TagKey<Item>> itemTags;
    private final Set<ItemLike> items;
    private final String id;
    private final IMaterialTag[] tags;
    private final Object2BooleanMap<AntimatterToolType> optionalTools;
    private final Set<Material> fixedMats;
    private final boolean inverse;

    protected static PropertyIngredient build(Set<MaterialTypeItem<?>> type, Set<TagKey<Item>> itemTags, Set<ItemLike> items, String id, IMaterialTag[] tags, Set<Material> fixedMats, boolean inverse, Object2BooleanMap<AntimatterToolType> tools) {
        Stream<Value> stream = Stream.concat(Stream.concat(itemTags.stream().map(t -> new TagValue(t)), type.stream().map(i -> new MultiItemValue((fixedMats.size() == 0 ? i.all().stream() : fixedMats.stream()).filter(t -> {
            boolean ok = t.has(tags);
            boolean types = true;
            if (tools.size() > 0)
                if (t.has(MaterialTags.TOOLS)) {
                    Set<AntimatterToolType> set = new HashSet<>(MaterialTags.TOOLS.get(t).toolTypes());
                    for (Object2BooleanMap.Entry<AntimatterToolType> entry : tools.object2BooleanEntrySet()) {
                        types &= entry.getBooleanValue() == set.contains(entry.getKey());
                    }
                } else {
                    types = false;
                }
            if (inverse) {
                return !ok && types;
            }
            return ok && types;
        }).map(mat -> i.get(mat, 1)).collect(Collectors.toList())))), Stream.of((Value)new MultiItemValue(items.stream().map(ItemStack::new).collect(Collectors.toList()))));
        return new PropertyIngredient(stream, type, itemTags, items, id, tags, fixedMats, inverse, tools);
    }

    protected PropertyIngredient(Stream<Value> stream, Set<MaterialTypeItem<?>> type, Set<TagKey<Item>> itemTags, Set<ItemLike> items, String id, IMaterialTag[] tags, Set<Material> fixedMats, boolean inverse, Object2BooleanMap<AntimatterToolType> tools) {
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
        for (ItemLike item : this.items) {
            if (item.asItem() == stack.getItem()) {
                return stack;
            }
        }
        for (TagKey<Item> itemTag : this.itemTags) {
            Getter getter = getters.get(itemTag.location());
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

    public Set<TagKey<Item>> getTags() {
        return itemTags;
    }

    public String getId() {
        return id;
    }

    @Override
    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        JsonArray materialArr = new JsonArray();
        for (MaterialTypeItem<?> materialTypeItem : this.type) {
            materialArr.add(materialTypeItem.getId());
        }
        obj.add("material_type", materialArr);
        materialArr = new JsonArray();
        for (TagKey<Item> itemTag : this.itemTags) {
            materialArr.add(itemTag.location().toString());
        }
        obj.add("item_tags", materialArr);
        materialArr = new JsonArray();
        for (ItemLike item : this.items) {
            ResourceLocation name = AntimatterPlatformUtils.getIdFromItem(item.asItem());
            if (name != null) materialArr.add(name.toString());
        }
        obj.add("items", materialArr);
        obj.addProperty("type", Serializer.ID.toString());
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

    @Override
    public boolean test(@Nullable ItemStack test) {
        if (test == null || test.isEmpty()) return false;
        /*if (type.size() > 0) {
            if (test.getItem() instanceof MaterialItem) {
                MaterialItem item = ((MaterialItem) test.getItem());
                if (item.getType() instanceof MaterialTypeItem) {
                    return this.type.contains((MaterialTypeItem<?>)item.getType());
                }
            }
            for (MaterialTypeItem<?> materialTypeItem : getTypes()) {
                if (materialTypeItem.getMaterialFromStack(test) != null) {
                    return true;
                }
            }
        } else {

        }*/
        for (TagKey<Item> itemTag : this.itemTags) {
            if (test.is(itemTag)) {
                return true;
            }
        }
        return super.test(test);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Serializer implements IAntimatterIngredientSerializer<PropertyIngredient> {

        public static Serializer INSTANCE = new Serializer();

        public static final ResourceLocation ID = new ResourceLocation("antimatter", "material");

        public static void init(){
            AntimatterAPI.register(IAntimatterIngredientSerializer.class, "material", Ref.ID, INSTANCE);
        }

        @Override
        public PropertyIngredient parse(FriendlyByteBuf buffer) {
            String id = buffer.readUtf();
            int size = buffer.readVarInt();
            Set<MaterialTypeItem<?>> items = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                items.add(AntimatterAPI.get(MaterialTypeItem.class, buffer.readUtf()));
            }
            size = buffer.readVarInt();
            Set<TagKey<Item>> t = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                t.add(TagUtils.getItemTag(buffer.readResourceLocation()));
            }
            boolean inverse = buffer.readBoolean();
            IMaterialTag[] tags = new IMaterialTag[buffer.readVarInt()];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = AntimatterAPI.get(IMaterialTag.class, buffer.readUtf());
            }
            size = buffer.readVarInt();
            Object2BooleanMap<AntimatterToolType> map = new Object2BooleanOpenHashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put(AntimatterAPI.get(AntimatterToolType.class, buffer.readUtf()), buffer.readBoolean());
            }
            size = buffer.readVarInt();
            Set<Material> fixedMats = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                fixedMats.add(AntimatterAPI.get(Material.class, buffer.readUtf()));
            }
            size = buffer.readVarInt();
            Set<ItemLike> itemProviders = new ObjectArraySet<>(size);
            for (int i = 0; i < size; i++) {
                ResourceLocation name = new ResourceLocation(buffer.readUtf());
                if (AntimatterPlatformUtils.itemExists(name)) {
                    itemProviders.add(AntimatterPlatformUtils.getItemFromID(name));
                }
            }
            ItemStack[] stacks = new ItemStack[buffer.readVarInt()];
            for (int i = 0; i < stacks.length; i++) {
                stacks[i] = buffer.readItem();
            }
            return new PropertyIngredient(Stream.of(new MultiItemValue(Arrays.asList(stacks))), items, t, itemProviders, id, tags, fixedMats, inverse, map);
        }

        @Override
        public PropertyIngredient parse(JsonObject json) {
            JsonArray arr = json.getAsJsonArray("material_type");
            Set<MaterialTypeItem<?>> items = new ObjectArraySet<>(arr.size());
            arr.forEach(el -> items.add(AntimatterAPI.get(MaterialTypeItem.class, el.getAsString())));
            arr = json.getAsJsonArray("item_tags");
            Set<TagKey<Item>> itemTags = new ObjectArraySet<>(arr.size());
            arr.forEach(el -> itemTags.add(TagUtils.getItemTag(new ResourceLocation(el.getAsString()))));
            arr = json.getAsJsonArray("items");
            Set<ItemLike> items2 = new ObjectArraySet<>(arr.size());
            arr.forEach(el -> {
                if (AntimatterPlatformUtils.itemExists(new ResourceLocation(el.getAsString())))
                    items2.add(AntimatterPlatformUtils.getItemFromID(new ResourceLocation(el.getAsString())));
            });
            String ingId = json.get("id").getAsString();
            boolean inverse = json.get("inverse").getAsBoolean();
            IMaterialTag[] tags;
            if (json.has("tags")) {
                tags = Streams.stream(GsonHelper.getAsJsonArray(json, "tags")).map(t -> AntimatterAPI.get(IMaterialTag.class, t.getAsString())).toArray(IMaterialTag[]::new);
            } else {
                tags = new IMaterialTag[0];
            }
            Object2BooleanMap<AntimatterToolType> map = new Object2BooleanOpenHashMap<>();
            if (json.has("tools")) {
                for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "tools").entrySet()) {
                    map.put(AntimatterAPI.get(AntimatterToolType.class, entry.getKey()), entry.getValue().getAsBoolean());
                }
            }
            Set<Material> fixedMats = Collections.emptySet();
            if (json.has("fixed")) {
                fixedMats = Streams.stream(GsonHelper.getAsJsonArray(json, "fixed")).map(t -> AntimatterAPI.get(Material.class, t.getAsString())).collect(Collectors.toSet());
            }
            return PropertyIngredient.build(items, itemTags, items2, ingId, tags, fixedMats, inverse, map);
        }

        @Override
        public void write(FriendlyByteBuf buffer, PropertyIngredient ingredient) {
            buffer.writeUtf(ingredient.id);
            buffer.writeVarInt(ingredient.type.size());
            for (MaterialTypeItem<?> materialTypeItem : ingredient.type) {
                buffer.writeUtf(materialTypeItem.getId());
            }
            buffer.writeVarInt(ingredient.itemTags.size());
            for (TagKey<Item> itemTag : ingredient.itemTags) {
                buffer.writeResourceLocation(itemTag.location());
            }
            buffer.writeBoolean(ingredient.inverse);
            buffer.writeVarInt(ingredient.tags.length);
            for (IMaterialTag tag : ingredient.tags) {
                buffer.writeUtf(tag.getId());
            }
            buffer.writeVarInt(ingredient.optionalTools.size());
            for (Object2BooleanMap.Entry<AntimatterToolType> entry : ingredient.optionalTools.object2BooleanEntrySet()) {
                buffer.writeUtf(entry.getKey().getId());
                buffer.writeBoolean(entry.getBooleanValue());
            }
            buffer.writeVarInt(ingredient.fixedMats.size());
            for (Material fixedMat : ingredient.fixedMats) {
                buffer.writeUtf(fixedMat.getId());
            }
            buffer.writeVarInt(ingredient.items.size());
            for (ItemLike item : ingredient.items) {
                ResourceLocation name = AntimatterPlatformUtils.getIdFromItem(item.asItem());
                if (name != null) buffer.writeUtf(name.toString());
            }
            //Needed because tags might not be available on client.
            ItemStack[] items = ingredient.getItems();
            buffer.writeVarInt(items.length);

            for (ItemStack stack : items)
                buffer.writeItem(stack);


        }
    }

    public static class Builder {
        private Set<MaterialTypeItem<?>> type;
        private Set<TagKey<Item>> itemTags;
        private Set<ItemLike> items;
        private Set<Material> fixedMats;
        private final String id;
        private IMaterialTag[] tags = new IMaterialTag[0];
        private final Object2BooleanMap<AntimatterToolType> optionalTools = new Object2BooleanOpenHashMap<>();
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
        public final Builder itemTags(TagKey<Item>... tags) {
            this.itemTags = new ObjectArraySet<>(tags);
            return this;
        }

        public final Builder itemStacks(ItemLike... items) {
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
