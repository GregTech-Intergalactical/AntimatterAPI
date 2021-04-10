package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTypeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.crafting.StackList;

import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialIngredient extends Ingredient {

    private final MaterialTypeItem<?> type;

    protected MaterialIngredient(MaterialTypeItem<?> type, Stream<? extends IItemList> itemLists) {
        super(itemLists);
        this.type = type;
    }

    public static MaterialIngredient of(MaterialTypeItem<?> type) {
        MaterialIngredient mat = new MaterialIngredient(type, Stream.of(new StackList(type.all().stream().map(t -> type.get(t, 1)).collect(Collectors.toList()))));
        return mat;
    }

    public MaterialTypeItem<?> getType() {
        return type;
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
        return obj;
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null || p_test_1_.isEmpty()) return false;
        if (!(p_test_1_.getItem() instanceof MaterialItem)) return false;
        return ((MaterialItem)p_test_1_.getItem()).getType() == type;
    }

    public static class Serializer implements IIngredientSerializer<MaterialIngredient> {

        public static Serializer INSTANCE = new Serializer();

        @Override
        public MaterialIngredient parse(PacketBuffer buffer) {
            String id = buffer.readString();
            MaterialTypeItem<?> item = AntimatterAPI.get(MaterialTypeItem.class, id);
            return MaterialIngredient.of(item);
        }

        @Override
        public MaterialIngredient parse(JsonObject json) {
            String id = json.get("material_type").getAsString();
            MaterialTypeItem<?> item = AntimatterAPI.get(MaterialTypeItem.class, id);
            return MaterialIngredient.of(item);
        }

        @Override
        public void write(PacketBuffer buffer, MaterialIngredient ingredient) {
            buffer.writeString(ingredient.type.getId());
        }
    }
}
