package muramasa.antimatter.recipe.serializer;

import com.google.gson.JsonObject;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.TagIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

//WIP
public class AntimatterIngredientSerializer implements IIngredientSerializer<AntimatterIngredient> {

    public static final AntimatterIngredientSerializer INSTANCE = new AntimatterIngredientSerializer();

    private static final ResourceLocation VANILLA_ING = new ResourceLocation("minecraft", "item");
    @Override
    public AntimatterIngredient parse(PacketBuffer buffer) {
        int count = buffer.readInt();
        if (buffer.readBoolean()) {
            return AntimatterIngredient.of(buffer.readResourceLocation(), count).get();
        }
        Ingredient i = CraftingHelper.getIngredient(VANILLA_ING, buffer);
        return defaultIng(count, i);
    }

    @Override
    public AntimatterIngredient parse(JsonObject json) {
        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        if (json.has("tag")) {
            return AntimatterIngredient.of(new ResourceLocation(json.get("tag").getAsString()), count).get();
        }
        return defaultIng(count,CraftingHelper.getIngredient(json));
    }

    private AntimatterIngredient defaultIng(int count, Ingredient i) {
        if (i.getMatchingStacks().length == 1) {
            i.getMatchingStacks()[0].setCount(count);
            return AntimatterIngredient.of(i.getMatchingStacks()[0]).get();
        }
        return AntimatterIngredient.of(count, i.getMatchingStacks()).get();
    }

    @Override
    public void write(PacketBuffer buffer, AntimatterIngredient ingredient) {
        buffer.writeInt(ingredient.count);
        if (ingredient instanceof TagIngredient) {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(((TagIngredient) ingredient).getTag());
            return;
        }
        ItemStack[] items = ingredient.getMatchingStacks();
        for (ItemStack stack : items)
            buffer.writeItemStack(stack);
    }
}
