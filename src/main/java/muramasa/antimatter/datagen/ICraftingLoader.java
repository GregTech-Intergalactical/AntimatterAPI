package muramasa.antimatter.datagen;

import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

public interface ICraftingLoader {
    void loadRecipes(Consumer<IFinishedRecipe> output, AntimatterRecipeProvider provider);
}
