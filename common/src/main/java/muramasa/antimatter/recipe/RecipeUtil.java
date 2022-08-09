package muramasa.antimatter.recipe;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class RecipeUtil {
    @ExpectPlatform
    public static boolean isNBTIngredient(Class<? extends Ingredient> clazz){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompoundIngredient(Class<? extends Ingredient> clazz){
        throw new AssertionError();
    }

    public static void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName) {
        throw new AssertionError();
    }

    public static void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, String config, String configField, String recipeDomain, String recipeName) {
        throw new AssertionError();
    }

}
