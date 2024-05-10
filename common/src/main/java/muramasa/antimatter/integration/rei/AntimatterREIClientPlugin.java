package muramasa.antimatter.integration.rei;

import dev.architectury.fluid.FluidStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.settings.EntrySettingsAdapterRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.integration.rei.category.RecipeMapCategory;
import muramasa.antimatter.integration.rei.category.RecipeMapDisplay;
import muramasa.antimatter.integration.rei.extension.REIMaterialRecipeExtension;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class AntimatterREIClientPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Ref.ID + ":rei";
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        if (!AntimatterConfig.ADD_REI_GROUPS.get()) return;

        AntimatterAPI.all(MaterialType.class).stream().filter(t -> t instanceof MaterialTypeItem<?> || t instanceof MaterialTypeBlock<?>).forEach(t -> {
            if (t.get() instanceof MaterialTypeBlock.IOreGetter getter){
                AntimatterAPI.all(StoneType.class, s -> {
                    if (s != AntimatterStoneTypes.STONE && !AntimatterConfig.SHOW_ALL_ORES.get() && t != AntimatterMaterialTypes.BEARING_ROCK) return;
                    if (t == AntimatterMaterialTypes.BEARING_ROCK && !AntimatterConfig.SHOW_ROCKS.get()) return;
                    List<EntryStack<ItemStack>> entries = t.all().stream().map(m -> EntryStack.of(VanillaEntryTypes.ITEM, getter.get((Material) m, s).asStack())).toList();
                    registry.group(new ResourceLocation(Ref.SHARED_ID, t.getId() + "_" + s.getId()), Utils.translatable(Ref.ID + ".rei.group." + t.getId() + "." + s.getId()), entries);
                });
                if (t != AntimatterMaterialTypes.BEARING_ROCK){
                    return;
                }
            }
            if (AntimatterConfig.GROUP_ORES_ONLY.get()) return;
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
            registry.group(new ResourceLocation(Ref.SHARED_ID, t.getId()), Utils.translatable(Ref.ID + ".rei.group." + t.getId()), entries);
        });
        if (AntimatterConfig.GROUP_ORES_ONLY.get()) return;
        AntimatterAPI.all(StoneType.class, s -> {
            if (s instanceof CobbleStoneType cobble){
                List<EntryStack<ItemStack>> entries = cobble.getBlocks().values().stream().map(b -> EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(b.asItem()))).toList();
                registry.group(new ResourceLocation(Ref.SHARED_ID, s.getId()), Utils.translatable(Ref.ID + ".rei.group." + s.getId()), entries);
            }
        });
    }

    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
        List<ItemLike> list = new ArrayList<>();
        AntimatterJEIREIPlugin.getItemsToHide().forEach(c -> c.accept(list));
        list.forEach(i -> rule.hide(EntryStack.of(VanillaEntryTypes.ITEM, i.asItem().getDefaultInstance())));
        List<Fluid> fluidList = new ArrayList<>();
        AntimatterJEIREIPlugin.getFluidsToHide().forEach(c -> c.accept(fluidList));
        fluidList.forEach(f -> {
            rule.hide(EntryStack.of(VanillaEntryTypes.FLUID, FluidStack.create(f, 1)));
            rule.hide(EntryStack.of(VanillaEntryTypes.ITEM, f.getBucket().getDefaultInstance()));
        });
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                RecipeMapCategory category = new RecipeMapCategory(tuple.map, tuple.gui, tuple.tier, tuple.workstations.isEmpty() ? null : tuple.workstations.get(0));
                registry.add(category);
                if (!tuple.workstations.isEmpty()){
                    tuple.workstations.forEach(s -> {
                        ItemLike item = AntimatterPlatformUtils.getItemFromID(s);
                        if (item == Items.AIR) return;
                        registry.addWorkstations(category.getCategoryIdentifier(), EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(item)));
                    });
                }
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
        REIUtils.EXTRA_CATEGORIES.forEach(c -> c.accept(registry));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // regular recipes
        registry.registerRecipeFiller(IRecipe.class, type -> Objects.equals(Recipe.RECIPE_TYPE, type), r -> !r.isHidden(), RecipeMapDisplay::new);
        AntimatterJEIREIPlugin.getREGISTRY().values().forEach(t -> {
            var m = t.map;
            if (m instanceof RecipeMap<?> rm){
                if (m.getProxy() != null){
                    registry.registerRecipeFiller(net.minecraft.world.item.crafting.Recipe.class, m.getProxy().loc(), r -> {
                        IRecipe recipe = m.getProxy().handler().apply(r, rm.RB());
                        if (recipe == null) return null;
                        if (recipe.isHidden()) return null;
                        return new RecipeMapDisplay(recipe);
                    });
                }
            }
        });
        REIUtils.EXTRA_DISPLAYS.forEach(c -> c.accept(registry));
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (stage != ReloadStage.END || !manager.equals(PluginManager.getClientInstance())) return;
        CategoryRegistry.getInstance().get(BuiltinPlugin.CRAFTING).registerExtension(new REIMaterialRecipeExtension());
    }

    @Override
    public void registerEntrySettingsAdapters(EntrySettingsAdapterRegistry registry) {
        REIClientPlugin.super.registerEntrySettingsAdapters(registry);
    }
}
