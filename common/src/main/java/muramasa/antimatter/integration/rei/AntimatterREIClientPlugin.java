package muramasa.antimatter.integration.rei;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.integration.rei.category.RecipeMapCategory;
import muramasa.antimatter.integration.rei.category.RecipeMapDisplay;
import muramasa.antimatter.integration.rei.extension.REIMaterialRecipeExtension;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Set;

public class AntimatterREIClientPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Ref.ID + ":rei";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                RecipeMapCategory category = new RecipeMapCategory(tuple.map, tuple.gui, tuple.tier, tuple.model);
                registry.add(category);
                registry.addWorkstations(category.getCategoryIdentifier(), (EntryStack<?>) category.getIcon());
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // regular recipes
        registry.registerRecipeFiller(IRecipe.class, type -> Objects.equals(Recipe.RECIPE_TYPE, type), r -> !r.isHidden(), RecipeMapDisplay::new);
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (stage != ReloadStage.END || !manager.equals(PluginManager.getClientInstance())) return;
        CategoryRegistry.getInstance().get(BuiltinPlugin.CRAFTING).registerExtension(new REIMaterialRecipeExtension());
    }

}
