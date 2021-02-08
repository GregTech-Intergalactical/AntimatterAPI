package muramasa.antimatter.recipe.loader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeMap;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.List;

public class AntimatterRecipeLoader {
    protected List<IRecipeLoader> loaders = new ObjectArrayList<>();

    @SubscribeEvent
    public void TagsUpdatedEvent(TagsUpdatedEvent event)
    {
        long time = System.currentTimeMillis();
        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        loaders.forEach(IRecipeLoader::init);
        Antimatter.LOGGER.info("Time to load all AM recipes: " + (System.currentTimeMillis()-time) + " ms");
        long recipes = AntimatterAPI.all(RecipeMap.class).stream().mapToLong(rm -> {
            Collection<Recipe> rs = rm.getRecipes(false);
            return rs == null ? 0 : rs.size();
        }).sum();
        Antimatter.LOGGER.info("Total recipes " + recipes);
    }

    public void registerRecipeLoader(IRecipeLoader loader) {
        loaders.add(loader);
    }
}
