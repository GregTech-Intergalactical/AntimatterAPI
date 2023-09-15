package muramasa.antimatter.integration.rei.extension;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.registry.category.extension.CategoryExtensionProvider;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayCategoryView;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.ChatFormatting.GOLD;

@SuppressWarnings("UnstableApiUsage")
public class REIMaterialRecipeExtension implements CategoryExtensionProvider<DefaultCraftingDisplay<?>> {
    @Override
    public DisplayCategoryView<DefaultCraftingDisplay<?>> provide(DefaultCraftingDisplay<?> display, DisplayCategory<DefaultCraftingDisplay<?>> category, DisplayCategoryView<DefaultCraftingDisplay<?>> lastView) {
        if (Minecraft.getInstance().screen instanceof DisplayScreen displayScreen){
            Optional<?> optionalO = display.getOptionalRecipe();
            if (optionalO.isEmpty()) return lastView;
            Object r = optionalO.get();
            if (display.getOutputEntries().size() > 1) return lastView;
            if (r instanceof MaterialRecipe recipe){
                List<EntryIngredient> ingredientList = display.getInputEntries();
                List<EntryIngredient> outputList = display.getOutputEntries();
                List<ItemStack> outputs = recipe.outputs;
                boolean isOutput = displayScreen.getIngredientsToNotice().isEmpty();
                List<EntryStack<?>> list = isOutput ? displayScreen.getResultsToNotice() : displayScreen.getIngredientsToNotice();
                if (list.size() != 1) return lastView;
                EntryStack<?> e = list.get(0);
                if (e.getType() != VanillaEntryTypes.ITEM) return lastView;
                EntryStack<ItemStack> entryStack = e.cast();
                ItemStack stack = entryStack.getValue();
                if (isOutput){
                    boolean isNull = false;
                    Map<String, Object> m = recipe.builder.getFromResult(stack);
                    int i = 0;

                    for (Ingredient ingredient : recipe.getIngredients()) {
                        if (!(ingredient instanceof PropertyIngredient ing)) {
                            i++;
                            continue;
                        }
                        String id = ing.getId();
                        Object mat = m.get(id);
                        ingredientList.set(i, EntryIngredients.ofIngredient(ingredient));
                        if (mat == null || mat == Material.NULL) {
                            isNull = true;
                            i++;
                            continue;
                        }
                        List<ItemStack> st = Arrays.stream(ing.getItems()).filter(t -> Objects.equals(ing.getMat(t), mat)).collect(Collectors.toList());
                        if (st.size() > 0) {
                            ingredientList.set(i, ingredient(st, ing));
                        }
                        i++;
                    }
                    if (!isNull) {
                        outputList.set(0, EntryIngredient.of(EntryStacks.of(stack)));
                    } else {
                        outputList.set(0, EntryIngredients.of(outputs.get(0)));
                    }
                    /*IRecipeSlotBuilder outputSlot = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 95, 19);
                    outputSlot.addTooltipCallback((a, b) -> {
                        if (a.isEmpty()) return;
                        a.getDisplayedIngredient().flatMap(t -> t.getIngredient(VanillaTypes.ITEM)).ifPresent(ing -> {
                            Map<String, Object> o = recipe.builder.getFromResult(ing);
                            b.add(new TextComponent("Properties:").withStyle(GOLD));
                            o.forEach((k, v) -> b.add(new TextComponent(k.substring(0, 1).toUpperCase() + k.substring(1)).append(new TextComponent(" - " + v.toString()))));
                        });
                    });
                    outputSlot.addIngredients(VanillaTypes.ITEM, Collections.singletonList(stack));*/
                } else {
                    Map<String, Object> out = new Object2ObjectOpenHashMap<>();

                    recipe.getIngredients().stream().filter(t -> t.test(stack) && t instanceof PropertyIngredient).map(t -> ((PropertyIngredient) t)).findAny().ifPresent(ing -> {
                        String id = ing.getId();
                        Object mat = ing.getMat(stack);
                        int i = 0;
                        for (Ingredient innerIngredient : recipe.getIngredients()) {
                            if (!(innerIngredient instanceof PropertyIngredient inner)) {
                                i++;
                                continue;
                            }
                            if (inner.getId().equals(id)) {
                                List<ItemStack> st = Arrays.stream(inner.getItems()).filter(t -> Objects.equals(inner.getMat(t), mat)).collect(Collectors.toList());
                                if (st.size() > 0) {
                                    ingredientList.set(i, ingredient(st, inner));
                                    out.put(id, mat);
                                }
                            }
                            i++;
                        }
                    });
                    List<ItemStack> result;
                    ItemStack build;
                    try {
                        build = recipe.builder.build(null, new MaterialRecipe.Result(out, Collections.emptyMap()));
                    } catch (Exception ex) {
                        build = ItemStack.EMPTY;
                    }
                    if (!build.isEmpty()) {
                        result = Collections.singletonList(build);
                    } else {
                        result = outputs.stream().filter(t -> {
                            Map<String, Object> o = recipe.builder.getFromResult(t);
                            boolean ok = true;
                            for (Map.Entry<String, Object> objectEntry : o.entrySet()) {
                                Object inner = out.get(objectEntry.getKey());
                                ok &= inner != null && inner.equals(objectEntry.getValue());
                            }
                            return ok;
                        }).collect(Collectors.toList());
                    }

                    /*IRecipeSlotBuilder outputSlot = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 95, 19);
                    outputSlot.addTooltipCallback((a, b) -> {
                        if (a.isEmpty()) return;
                        a.getDisplayedIngredient().flatMap(t -> t.getIngredient(VanillaTypes.ITEM)).ifPresent(ing -> {
                            Map<String, Object> o = recipe.builder.getFromResult(ing);
                            b.add(new TextComponent("Properties: ").withStyle(GOLD));
                            o.forEach((k, v) -> b.add(new TextComponent(k.substring(0, 1).toUpperCase() + k.substring(1)).append(new TextComponent(" - " + v.toString()))));
                        });
                    });
                    if (result.size() > 0) {
                        outputSlot.addIngredients(VanillaTypes.ITEM, result);
                    } else {
                        outputSlot.addIngredients(VanillaTypes.ITEM, outputs);
                    }*/
                }
                //display = new DefaultCustomShapedDisplay(display.getDisplayLocation().orElse(null), recipe, ingredientList, outputList, display.getWidth(), display.getHeight());
            }

        }
        return lastView;
    }

    private EntryIngredient ingredient(List<ItemStack> list, PropertyIngredient p){
        List<EntryStack<ItemStack>> entries = list.stream().map(stack -> EntryStacks.of(stack).setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, f -> List.of(new TextComponent("Property: ").append(new TextComponent(p.getId().substring(0, 1).toUpperCase() + p.getId().substring(1)).withStyle(GOLD))))).toList();
        return EntryIngredient.of(entries);
    }
}
