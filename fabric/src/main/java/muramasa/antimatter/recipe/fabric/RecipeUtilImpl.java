package muramasa.antimatter.recipe.fabric;

import io.github.fabricators_of_create.porting_lib.crafting.NBTIngredient;
import io.github.fabricators_of_create.porting_lib.data.ConditionalRecipe;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;

import java.util.function.Consumer;

public class RecipeUtilImpl {
    public static boolean isNBTIngredient(Class<? extends Ingredient> clazz){
        return clazz == NBTIngredient.class;
    }

    public static boolean isCompoundIngredient(Class<? extends Ingredient> clazz){
        return clazz == CompoundIngredient.class;
    }

    public static void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(RecipeConditions.config(configClass, configFieldName))
                .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }

    public static void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, String config, String configField, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(RecipeConditions.tomlConfig(config, configField))
                .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }
}
