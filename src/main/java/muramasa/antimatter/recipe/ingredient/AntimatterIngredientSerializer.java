package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class AntimatterIngredientSerializer implements IIngredientSerializer<AntimatterIngredient> {
    @Override
    public AntimatterIngredient parse(PacketBuffer buffer) {
        return null;
    }

    @Override
    public AntimatterIngredient parse(JsonObject json) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, AntimatterIngredient ingredient) {

    }
}
