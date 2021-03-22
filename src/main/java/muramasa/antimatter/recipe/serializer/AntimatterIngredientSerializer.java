package muramasa.antimatter.recipe.serializer;

import com.google.gson.JsonObject;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

//WIP
public class AntimatterIngredientSerializer implements IIngredientSerializer<AntimatterIngredient> {

    public static final AntimatterIngredientSerializer INSTANCE = new AntimatterIngredientSerializer();
    @Override
    public AntimatterIngredient parse(PacketBuffer buffer) {
        return null;
    }

    @Override
    public AntimatterIngredient parse(JsonObject json) {
        return null;//AntimatterIngredient.of(Ingredient.deserializeItemList(json).getStacks());
    }

    @Override
    public void write(PacketBuffer buffer, AntimatterIngredient ingredient) {
        ItemStack[] items = ingredient.getMatchingStacks();
        buffer.writeVarInt(items.length);

        for (ItemStack stack : items)
            buffer.writeItemStack(stack);
    }
}
