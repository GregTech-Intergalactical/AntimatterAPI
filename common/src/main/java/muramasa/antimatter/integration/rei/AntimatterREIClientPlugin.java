package muramasa.antimatter.integration.rei;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
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
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Function;

public class AntimatterREIClientPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Ref.ID + ":rei";
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        if (!AntimatterConfig.CLIENT.ADD_REI_GROUPS) return;

        AntimatterAPI.all(MaterialType.class).stream().filter(t -> t instanceof MaterialTypeItem<?> || t instanceof MaterialTypeBlock<?>).forEach(t -> {
            if (t.get() instanceof MaterialTypeBlock.IOreGetter getter){
                AntimatterAPI.all(StoneType.class, s -> {
                    if (s != AntimatterStoneTypes.STONE && !AntimatterConfig.CLIENT.SHOW_ALL_ORES && t != AntimatterMaterialTypes.ROCK) return;
                    if (t == AntimatterMaterialTypes.ROCK && !AntimatterConfig.CLIENT.SHOW_ROCKS) return;
                    List<EntryStack<ItemStack>> entries = t.all().stream().map(m -> EntryStack.of(VanillaEntryTypes.ITEM, getter.get((Material) m, s).asStack())).toList();
                    registry.group(new ResourceLocation(Ref.SHARED_ID, t.getId() + "_" + s.getId()), new TranslatableComponent(Ref.ID + ".rei.group." + t.getId() + "." + s.getId()), entries);
                });
                if (t != AntimatterMaterialTypes.ROCK){
                    return;
                }
            }
            if (AntimatterConfig.CLIENT.GROUP_ORES_ONLY) return;
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
        if (AntimatterConfig.CLIENT.GROUP_ORES_ONLY) return;
        AntimatterAPI.all(StoneType.class, s -> {
            if (s instanceof CobbleStoneType cobble){
                List<EntryStack<ItemStack>> entries = cobble.getBlocks().values().stream().map(b -> EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(b.asItem()))).toList();
                registry.group(new ResourceLocation(Ref.SHARED_ID, s.getId()), new TranslatableComponent(Ref.ID + ".rei.group." + s.getId()), entries);
            }
        });
    }

    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
        if (!AntimatterConfig.CLIENT.SHOW_ALL_ORES){
            AntimatterMaterialTypes.ORE.all().forEach(m -> {
                AntimatterAPI.all(StoneType.class, s -> {
                    if (s != AntimatterStoneTypes.STONE && s != AntimatterStoneTypes.SAND && s.generateOre){
                        Block ore = AntimatterMaterialTypes.ORE.get().get(m, s).asBlock();
                        if (ore instanceof BlockOre){
                            rule.hide(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(ore)));
                        }
                        if (m.has(AntimatterMaterialTypes.ORE_SMALL)){
                            ore = AntimatterMaterialTypes.ORE_SMALL.get().get(m, s).asBlock();
                            if (ore instanceof BlockOre){
                                rule.hide(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(ore)));
                            }
                        }
                    }
                });
            });
        }
        if (!AntimatterConfig.CLIENT.SHOW_ROCKS){
            AntimatterMaterialTypes.ROCK.all().forEach(m -> {
                AntimatterAPI.all(StoneType.class, s -> {
                    if (s.generateOre) {
                        rule.hide(EntryStack.of(VanillaEntryTypes.ITEM, AntimatterMaterialTypes.ROCK.get().get(m, s).asStack()));
                    }
                });
            });
        }
        List<ItemLike> list = new ArrayList<>();
        AntimatterJEIREIPlugin.getItemsToHide().forEach(c -> c.accept(list));
        list.forEach(i -> rule.hide(EntryStack.of(VanillaEntryTypes.ITEM, i.asItem().getDefaultInstance())));
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                RecipeMapCategory category = new RecipeMapCategory(tuple.map, tuple.gui, tuple.tier, tuple.model);
                registry.add(category);
                /*Machine<?> machine = tuple.model == null ? null : AntimatterAPI.get(Machine.class, tuple.model.getPath(), tuple.model.getNamespace());
                if (machine != null){
                    machine.getTiers().forEach(t -> {
                        registry.addWorkstations(category.getCategoryIdentifier(), EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(machine.getItem(t))));
                    });
                } else {
                    registry.addWorkstations(category.getCategoryIdentifier(), (EntryStack<?>) category.getIcon());
                }*/
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
        AntimatterAPI.all(Machine.class, machine -> {

            ((Machine<?>)machine).getTiers().forEach(t -> {
                IRecipeMap map = machine.getRecipeMap(t);
                if (map == null) return;
                ItemStack stack = new ItemStack(machine.getItem(t));
                if (!stack.isEmpty()) {
                    registry.addWorkstations(CategoryIdentifier.of(map.getLoc()), EntryStack.of(VanillaEntryTypes.ITEM, stack));
                }
            });
        });
        REIUtils.EXTRA_CATEGORIES.forEach(c -> c.accept(registry));
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
