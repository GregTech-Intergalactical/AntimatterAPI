package muramasa.antimatter.recipe.loader;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.mixin.RecipeManagerMixin;
import muramasa.antimatter.recipe.RecipeMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AntimatterRecipeLoader {

    protected static final List<IRecipeRegistrate.IRecipeLoader> loaders = new ObjectArrayList<>();
    static boolean loadedStart = false;

    public void add(IRecipeRegistrate.IRecipeLoader load) {
        loaders.add(load);
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void getRecipes(RecipesUpdatedEvent event) {
        load(event.getRecipeManager());
    }
    /**
     * Rebuilds the internal recipe maps inside the datapackregistries, as new immutable maps. Called by mixin.
     * @param reg the registries.
     */
    public static void postTagReload(DataPackRegistries reg) {
        RecipeManager rm = reg.getRecipeManager();
        RecipeManagerMixin accessor = (RecipeManagerMixin)rm;
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = accessor.getRecipes();
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> newRecipes = new Object2ObjectOpenHashMap<>();
        AntimatterRecipeProvider.runRecipes(rec -> newRecipes.compute(rec.getType(), (k, v) -> {
            if (v == null) v = new Object2ObjectOpenHashMap<>();
            v.put(rec.getId(), rec);
            return v;
        }));
        ImmutableMap.Builder<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> rootBuilder = ImmutableMap.builder();
        Set<IRecipeType<?>> allKeys = new ObjectOpenHashSet<>(recipes.keySet());
        allKeys.addAll(newRecipes.keySet());

        allKeys.forEach(t -> {
            ImmutableMap.Builder<ResourceLocation, IRecipe<?>> builder = ImmutableMap.builder();
            Map<ResourceLocation, IRecipe<?>> map = recipes.get(t);
            if (map != null) builder.putAll(map);
            map = newRecipes.get(t);
            if (map != null) builder.putAll(map);
            rootBuilder.put(t, builder.build());
        });
        int size = newRecipes.values().stream().mapToInt(t -> t.values().size()).sum();
        Antimatter.LOGGER.info("Loaded " + size + " recipes into DataPackRegistries");
        accessor.setRecipes(rootBuilder.build());
        load(reg.getRecipeManager());
    }

    /**
     * Compiles all recipes, including proxies.
     * @param reg the registries.
     */
    private static void load(RecipeManager reg) {
        long time = System.currentTimeMillis();
        Antimatter.LOGGER.info("Time to compile all AM recipes: " + (System.currentTimeMillis()-time) + " ms");
        time = System.currentTimeMillis();
        AntimatterAPI.all(RecipeMap.class, rm -> rm.compile(reg));
        Antimatter.LOGGER.info("Time to compile all (crafting) AM recipes: " + (System.currentTimeMillis()-time) + " ms");
    }


    /**
     * Loads recipes into the map but does not compile them. Ensures tags are loaded properly.
     */
    public void loadRecipes() {
        if (loadedStart) return;
        loadedStart = true;
        long time = System.currentTimeMillis();
        loaders.forEach(IRecipeRegistrate.IRecipeLoader::init);
        Antimatter.LOGGER.info("Time to load all AM recipes: " + (System.currentTimeMillis()-time) + " ms");
        long recipes = AntimatterAPI.all(RecipeMap.class).stream().mapToLong(RecipeMap::uncompiledSize).sum();
        Antimatter.LOGGER.info("Total recipes " + recipes);
    }
}
