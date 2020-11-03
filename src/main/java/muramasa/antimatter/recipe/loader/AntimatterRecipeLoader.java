package muramasa.antimatter.recipe.loader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.RecipeMap;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class AntimatterRecipeLoader {
    protected List<IRecipeLoader> loaders = new ObjectArrayList<>();

    @SubscribeEvent
    public void TagsUpdatedEvent(TagsUpdatedEvent event)
    {
        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        loaders.forEach(IRecipeLoader::init);
    }

    public void registerRecipeLoader(IRecipeLoader loader) {
        loaders.add(loader);
    }
}
