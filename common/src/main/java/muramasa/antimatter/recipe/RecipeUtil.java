package muramasa.antimatter.recipe;

import com.google.gson.JsonObject;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.util.ImplLoader;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import tesseract.FluidPlatformUtils;

import java.util.function.Consumer;

public interface RecipeUtil {
    RecipeUtil INSTANCE = ImplLoader.load(RecipeUtil.class);

    boolean isNBTIngredient(Class<? extends Ingredient> clazz);

    boolean isCompoundIngredient(Class<? extends Ingredient> clazz);

    void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName);

    void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, String config, String configField, String recipeDomain, String recipeName);

    ItemStack getItemStack(JsonObject object, boolean readNBT);

    JsonObject itemstackToJson(ItemStack stack);

    static JsonObject fluidstackToJson(FluidHolder stack){
        JsonObject object = new JsonObject();
        object.addProperty("fluid", FluidPlatformUtils.INSTANCE.getFluidId(stack.getFluid()).toString());
        object.addProperty("amount", stack.getFluidAmount());
        if (stack.getCompound() != null){
            object.addProperty("tag", stack.getCompound().toString());
        }
        return object;
    }

    <T extends Ingredient> void write(FriendlyByteBuf buffer, T ingredient);

    Ingredient fromNetwork(FriendlyByteBuf buffer);
}
