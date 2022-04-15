package muramasa.antimatter.integration.jei.extension;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraft.ChatFormatting.GOLD;

public record JEIMaterialRecipeExtension(MaterialRecipe recipe) implements ICraftingCategoryExtension {

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder recipeLayout, @NotNull ICraftingGridHelper helper, IFocusGroup focuses) {
        if (focuses.isEmpty()) {
            helper.setInputs(recipeLayout, VanillaTypes.ITEM, recipe.getIngredients().stream().map(t -> Arrays.asList(t.getItems())).toList(), recipe.getWidth(), recipe.getHeight());
            helper.setOutputs(recipeLayout, VanillaTypes.ITEM, recipe.outputs);
            return;
        }
        focuses.getFocuses(VanillaTypes.ITEM).forEach(focus -> {
            if (focus.getRole() == RecipeIngredientRole.CATALYST || focus.getRole() == RecipeIngredientRole.RENDER_ONLY)
                return;
            List<List<ItemStack>> inputs = recipe.getIngredients().stream().map(t -> Arrays.asList(t.getItems())).toList();
            List<ItemStack> outputs = recipe.outputs;
            List<List<ItemStack>> newInputs = new ObjectArrayList<>(inputs);
            ItemStack stack = focus.getTypedValue().getIngredient();

            if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
                Map<String, Object> m = recipe.builder.getFromResult(stack);
                int i = 0;

                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (!(ingredient instanceof PropertyIngredient ing)) {
                        i++;
                        continue;
                    }
                    String id = ing.getId();
                    Object mat = m.get(id);
                    if (mat == null) {
                        i++;
                        continue;
                    }
                    List<ItemStack> st = Arrays.stream(ing.getItems()).filter(t -> Objects.equals(ing.getMat(t), mat)).collect(Collectors.toList());
                    if (st.size() > 0) {
                        newInputs.set(i, st);
                    }
                    i++;
                }

                IRecipeSlotBuilder outputSlot = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 95, 19);
                outputSlot.addTooltipCallback((a, b) -> {
                    if (a.isEmpty()) return;
                    a.getDisplayedIngredient().flatMap(t -> t.getIngredient(VanillaTypes.ITEM)).ifPresent(ing -> {
                        Map<String, Object> o = recipe.builder.getFromResult(ing);
                        b.add(new TextComponent("Properties:").withStyle(GOLD));
                        o.forEach((k, v) -> b.add(new TextComponent(k.substring(0, 1).toUpperCase() + k.substring(1)).append(new TextComponent(" - " + v.toString()))));
                    });
                });
                outputSlot.addIngredients(VanillaTypes.ITEM, Collections.singletonList(stack));
            } else if (focus.getRole() == RecipeIngredientRole.INPUT) {
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
                                newInputs.set(i, st);
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

                IRecipeSlotBuilder outputSlot = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 95, 19);
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
                }
            }

            int i = 0;
            for (int y = 0; y < recipe.getHeight(); ++y) {
                for (int x = 0; x < 3; ++x) {
                    IRecipeSlotBuilder slot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
                    if (x >= recipe.getWidth()) {
                        //Outside height, insert air.
                        slot.addIngredient(VanillaTypes.ITEM, ItemStack.EMPTY);
                        continue;
                    }
                    slot.addIngredients(VanillaTypes.ITEM, newInputs.get(i++));
                    IntSet set = new IntOpenHashSet();
                    recipe.materialSlots.values().forEach(set::addAll);
                    if (set.contains(i - 1)) {
                        final int j = i;
                        slot.addTooltipCallback((a, b) -> {
                            if (recipe.getIngredients().get(j - 1) instanceof PropertyIngredient p) {
                                b.add(new TextComponent("Property: ").append(new TextComponent(p.getId().substring(0, 1).toUpperCase() + p.getId().substring(1)).withStyle(GOLD)));
                            }
                        });
                    }
                }
            }

        });


    }
}
