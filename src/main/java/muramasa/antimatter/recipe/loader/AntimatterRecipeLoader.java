package muramasa.antimatter.recipe.loader;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.List;

public class AntimatterRecipeLoader implements IRecipeRegistrate {

    protected List<IRecipeLoader> loaders = new ObjectArrayList<>();

    boolean vanillaLoaded = false;
    boolean customLoaded = false;
    boolean loadedStart = false;

    @Override
    public void add(IRecipeLoader load) {
        loaders.add(load);
    }

    @SubscribeEvent
    public void TagsUpdatedEvent(final TagsUpdatedEvent.VanillaTagTypes event)
    {
        vanillaLoaded = true;
        load();
    }

    @SubscribeEvent
    public void TagsUpdatedEvent(final TagsUpdatedEvent.CustomTagTypes event)
    {
        customLoaded = true;
        load();
    }

    private void load() {
        if (!(vanillaLoaded && customLoaded)) return;
        AntimatterAPI.all(RecipeMap.class, RecipeMap::compile);
        AntimatterRecipeProvider.runRecipes();
    }

    public void loadRecipes() {
        if (loadedStart) return;
        loadedStart = true;
        long time = System.currentTimeMillis();
        loaders.forEach(IRecipeLoader::init);
        Antimatter.LOGGER.info("Time to load all AM recipes: " + (System.currentTimeMillis()-time) + " ms");
        long recipes = AntimatterAPI.all(RecipeMap.class).stream().mapToLong(rm -> {
            Collection<Recipe> rs = rm.getRecipes(false);
            return rs == null ? 0 : rs.size();
        }).sum();
        Antimatter.LOGGER.info("Total recipes " + recipes);
    }
}
