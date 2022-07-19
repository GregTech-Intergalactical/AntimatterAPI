package muramasa.antimatter.recipe;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeUtil {
    @ExpectPlatform
    public static boolean isNBTIngredient(Class<? extends Ingredient> clazz){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompoundIngredient(Class<? extends Ingredient> clazz){
        throw new AssertionError();
    }

}
