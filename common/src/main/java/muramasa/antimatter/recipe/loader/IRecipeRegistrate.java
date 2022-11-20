package muramasa.antimatter.recipe.loader;

import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public interface IRecipeRegistrate {

    void add(String domain, String id, IRecipeLoader load);

    interface IRecipeLoader {
        void init(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider);
    }

}

