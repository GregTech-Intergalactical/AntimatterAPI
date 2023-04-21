package muramasa.antimatter.recipe.fabric;

import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.crafting.NBTIngredient;
import io.github.fabricators_of_create.porting_lib.data.ConditionalRecipe;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.function.Consumer;

public class RecipeUtilImpl {
    public static boolean isNBTIngredient(Class<? extends Ingredient> clazz){
        return clazz == NBTIngredient.class;
    }

    public static boolean isCompoundIngredient(Class<? extends Ingredient> clazz){
        return false;
        //return clazz == CompoundIngredient.class;
    }

    public static void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(RecipeConditions.config(configClass, configFieldName))
                .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }

    public static void addConditionalRecipe(Consumer<FinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, String config, String configField, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(RecipeConditions.tomlConfig(config, configField))
                .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }

    public static ItemStack getItemStack(JsonObject object, boolean readNBT){
        return CraftingHelper.getItemStack(object, readNBT);
    }

    public static JsonObject itemstackToJson(ItemStack stack){
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
        if (stack.getCount() > 1) {
            resultObj.addProperty("count", stack.getCount());
        }
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            if (!nbt.contains("ForgeCaps") && stack.getCapNBT() != null){
                nbt.put("ForgeCaps", stack.getCapNBT());
            }
            resultObj.addProperty("nbt", nbt.toString());
        }
        return resultObj;
    }

    public static <T extends Ingredient> void write(FriendlyByteBuf buffer, T ingredient){
        ingredient.toNetwork(buffer);
    }

    public static Ingredient fromNetwork(FriendlyByteBuf buffer) {
        return IngredientDeserializer.tryDeserializeNetwork(buffer);
    }
}
