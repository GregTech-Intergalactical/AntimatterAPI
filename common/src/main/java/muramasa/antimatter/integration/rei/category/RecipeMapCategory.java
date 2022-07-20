package muramasa.antimatter.integration.rei.category;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;

public class RecipeMapCategory implements DisplayCategory<RecipeMapDisplay> {


    @Override
    public Renderer getIcon() {
        return null;
    }

    @Override
    public Component getTitle() {
        return null;
    }

    @Override
    public CategoryIdentifier<? extends RecipeMapDisplay> getCategoryIdentifier() {
        return null;
    }
}
