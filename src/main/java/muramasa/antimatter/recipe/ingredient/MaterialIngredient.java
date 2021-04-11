package muramasa.antimatter.recipe.ingredient;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.IMaterialTag;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTag;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StackList;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialIngredient extends Ingredient {

    private final MaterialTypeItem<?> type;
    private final String id;
    private final IMaterialTag[] tags;
    private final String[] optionalTools;
    private final boolean inverse;

    protected MaterialIngredient(MaterialTypeItem<?> type, String id, IMaterialTag[] tags, boolean inverse) {
        this(type, id, tags, inverse, new String[0]);
    }

    protected MaterialIngredient(MaterialTypeItem<?> type, String id, IMaterialTag[] tags, boolean inverse, String[] toolTypes) {
        super(Stream.of(new StackList(type.all().stream().filter(t -> {
            boolean ok = t.has(tags);
            boolean types = true;
            if (toolTypes.length > 0) {
                Set<String> set = t.getToolTypes().stream().map(AntimatterToolType::getId).collect(Collectors.toSet());
                for (String toolType : toolTypes) {
                    String[] vals = toolType.split("_");
                    String toolId = vals[0];
                    boolean which = Boolean.parseBoolean(vals[1]);
                    types &= which == set.contains(toolId);
                }
            }
            if (inverse) {
                return !ok && types;
            }
            return ok && types;
        }).map(t -> type.get(t, 1)).collect(Collectors.toList()))));
        this.type = type;
        this.id = id;
        this.tags = tags == null ? new MaterialTag[0] : tags;
        this.inverse = inverse;
        this.optionalTools = toolTypes;
    }

    public static MaterialIngredient of(MaterialTypeItem<?> type, String id, IMaterialTag... tags) {
        MaterialIngredient mat = new MaterialIngredient(type, id, tags, false);
        return mat;
    }

    public static MaterialIngredient of(MaterialTypeItem<?> type, String id, ImmutableMap<AntimatterToolType, Boolean> toolTypes, IMaterialTag... tags) {
        MaterialIngredient mat = new MaterialIngredient(type, id, tags, false, toolTypes.entrySet().stream().map(t -> t.getKey().getId() + "_" + t.getValue().toString()).toArray(String[]::new));
        return mat;
    }

    public static MaterialIngredient ofInverse(MaterialTypeItem<?> type, String id, IMaterialTag... tags) {
        MaterialIngredient mat = new MaterialIngredient(type, id, tags, true);
        return mat;
    }

    public static MaterialIngredient ofInverse(MaterialTypeItem<?> type, String id,  ImmutableMap<AntimatterToolType, Boolean> toolTypes, IMaterialTag... tags) {
        MaterialIngredient mat = new MaterialIngredient(type, id, tags, true, toolTypes.entrySet().stream().map(t -> t.getKey().getId() + "_" + t.getValue().toString()).toArray(String[]::new));
        return mat;
    }


    public MaterialTypeItem<?> getType() {
        return type;
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
        obj.addProperty("material_type", type.getId());
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
        if (optionalTools.length > 0) {
            JsonArray arr = new JsonArray();
            for (String t : optionalTools) {
                arr.add(t);
            }
            obj.add("tools", arr);
        }
        return obj;
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null || p_test_1_.isEmpty()) return false;
        if (!(p_test_1_.getItem() instanceof MaterialItem)) {
            return type.tryMaterialFromItem(p_test_1_) != null;
        }
        return ((MaterialItem)p_test_1_.getItem()).getType() == type;
    }

    public static class Serializer implements IIngredientSerializer<MaterialIngredient> {

        public static Serializer INSTANCE = new Serializer();

        @Override
        public MaterialIngredient parse(PacketBuffer buffer) {
            String id = buffer.readString();
            MaterialTypeItem<?> item = AntimatterAPI.get(MaterialTypeItem.class, id);
            String ingId = buffer.readString();
            boolean inverse = buffer.readBoolean();
            IMaterialTag[] tags = new IMaterialTag[buffer.readVarInt()];
            for (int i = 0; i < tags.length; i++) {
                tags[i] = AntimatterAPI.get(IMaterialTag.class, buffer.readString());
            }
            String[] types = new String[buffer.readVarInt()];
            for (int i = 0; i < tags.length; i++) {
                types[i] = buffer.readString();
            }
            return new MaterialIngredient(item, ingId, tags, inverse, types);
        }

        @Override
        public MaterialIngredient parse(JsonObject json) {
            String id = json.get("material_type").getAsString();
            String ingId = json.get("id").getAsString();
            boolean inverse = json.get("inverse").getAsBoolean();
            MaterialTypeItem<?> item = AntimatterAPI.get(MaterialTypeItem.class, id);
            IMaterialTag[] tags;
            if (json.has("tags")) {
                tags = Streams.stream(JSONUtils.getJsonArray(json, "tags")).map(t -> AntimatterAPI.get(IMaterialTag.class, t.getAsString())).toArray(IMaterialTag[]::new);
            } else {
                tags = new MaterialTag[0];
            }
            String[] types;
            if (json.has("tools")) {
                types = Streams.stream(JSONUtils.getJsonArray(json, "tools")).map(JsonElement::getAsString).toArray(String[]::new);
            } else {
                types = new String[0];
            }
            return new MaterialIngredient(item, ingId,tags, inverse, types);
        }

        @Override
        public void write(PacketBuffer buffer, MaterialIngredient ingredient) {
            buffer.writeString(ingredient.type.getId());
            buffer.writeString(ingredient.id);
            buffer.writeBoolean(ingredient.inverse);
            buffer.writeVarInt(ingredient.tags.length);
            for (IMaterialTag tag : ingredient.tags) {
                buffer.writeString(tag.getId());
            }
            buffer.writeVarInt(ingredient.optionalTools.length);
            for (String tag : ingredient.optionalTools) {
                buffer.writeString(tag);
            }
        }
    }
}
