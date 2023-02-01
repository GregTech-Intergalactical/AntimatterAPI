package muramasa.antimatter.integration.rei;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.integration.rei.category.RecipeMapCategory;
import muramasa.antimatter.integration.rei.category.RecipeMapDisplay;
import muramasa.antimatter.integration.rei.extension.REIMaterialRecipeExtension;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class AntimatterREIClientPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Ref.ID + ":rei";
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        AntimatterAPI.all(MaterialType.class).stream().filter(t -> t instanceof MaterialTypeItem<?> || t instanceof MaterialTypeBlock<?>).forEach(t -> {
            if (t.get() instanceof MaterialTypeBlock.IOreGetter getter){
                AntimatterAPI.all(StoneType.class, s -> {
                    List<EntryStack<ItemStack>> entries = t.all().stream().map(m -> EntryStack.of(VanillaEntryTypes.ITEM, getter.get((Material) m, s).asStack())).toList();
                    registry.group(new ResourceLocation(Ref.SHARED_ID, t.getId() + "_" + s.getId()), new TranslatableComponent(Ref.ID + ".rei.group." + t.getId() + "." + s.getId()), entries);
                });
                if (t != AntimatterMaterialTypes.ROCK){
                    return;
                }
            }
            Function<Material, ItemStack> func = null;
            if (t instanceof MaterialTypeItem<?> typeItem){
                func = m -> typeItem.get(m, 1);
            }
            if (t instanceof MaterialTypeBlock<?> typeBlock){
                Object func2 = typeBlock.get();
                if (func2 instanceof MaterialTypeBlock.IBlockGetter getter){
                    func = m -> getter.get(m).asStack();
                }
            }
            if (func == null) return;
            Function<Material, ItemStack> finalFunc = func;
            List<EntryStack<ItemStack>> entries = t.all().stream().map(m -> EntryStack.of(VanillaEntryTypes.ITEM, finalFunc.apply((Material) m))).toList();
            registry.group(new ResourceLocation(Ref.SHARED_ID, t.getId()), new TranslatableComponent(Ref.ID + ".rei.group." + t.getId()), entries);
        });
        AntimatterAPI.all(StoneType.class, s -> {
            if (s instanceof CobbleStoneType cobble){
                List<EntryStack<ItemStack>> entries = cobble.getBlocks().values().stream().map(b -> EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(b.asItem()))).toList();
                registry.group(new ResourceLocation(Ref.SHARED_ID, s.getId()), new TranslatableComponent(Ref.ID + ".rei.group." + s.getId()), entries);
            }
        });
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                RecipeMapCategory category = new RecipeMapCategory(tuple.map, tuple.gui, tuple.tier, tuple.model);
                registry.add(category);
                Machine<?> machine = AntimatterAPI.get(Machine.class, tuple.model.getPath(), tuple.model.getNamespace());
                if (machine != null){
                    machine.getTiers().forEach(t -> {
                        registry.addWorkstations(category.getCategoryIdentifier(), EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(machine.getItem(t))));
                    });
                } else {
                    registry.addWorkstations(category.getCategoryIdentifier(), (EntryStack<?>) category.getIcon());
                }
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // regular recipes
        registry.registerRecipeFiller(IRecipe.class, type -> Objects.equals(Recipe.RECIPE_TYPE, type), r -> !r.isHidden(), RecipeMapDisplay::new);
        AntimatterAPI.all(RecipeMap.class, m -> {
            if (m.getProxy() != null){
                registry.registerRecipeFiller(net.minecraft.world.item.crafting.Recipe.class, m.getProxy().loc(), r -> new RecipeMapDisplay(m.getProxy().handler().apply(r, m.RB())));
            }
        });
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (stage != ReloadStage.END || !manager.equals(PluginManager.getClientInstance())) return;
        CategoryRegistry.getInstance().get(BuiltinPlugin.CRAFTING).registerExtension(new REIMaterialRecipeExtension());
    }

}
