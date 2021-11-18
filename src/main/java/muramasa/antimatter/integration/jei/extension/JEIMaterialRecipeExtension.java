package muramasa.antimatter.integration.jei.extension;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.stream.Collectors;

public class JEIMaterialRecipeExtension implements ICustomCraftingCategoryExtension {

    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;

    public static final int width = 116;
    public static final int height = 54;

    protected final MaterialRecipe recipe;
    protected final ICraftingGridHelper helper;

    public JEIMaterialRecipeExtension(MaterialRecipe recipe) {
        this.recipe = recipe;
        this.helper = AntimatterJEIPlugin.helpers().getGuiHelper().createCraftingGridHelper(craftInputSlot1);
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.stacksToLookup());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IFocus<?> focus = recipeLayout.getFocus();

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> newInputs = new ObjectArrayList<>(inputs);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        boolean shouldReplace = true;
        if (focus != null) {
            ItemStack stack = (ItemStack) focus.getValue();
            if (focus.getMode() == IFocus.Mode.OUTPUT) {
                Map<String, Object> m = recipe.builder.getFromResult(stack);
                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    Ingredient j = recipe.getIngredients().get(i);
                    if (!(j instanceof PropertyIngredient)) continue;
                    PropertyIngredient inner = (PropertyIngredient) j;
                    Object o = m.get(inner.getId());
                    if (o == null) continue;
                    List<ItemStack> st = Arrays.stream(inner.getItems()).filter(t -> MaterialRecipe.getMat(inner, t).equals(o)).collect(Collectors.toList());
                    if (st.size() > 0) {
                        newInputs.set(i, st);
                        final Item item = stack.getItem();
                        Optional<ItemStack> optionalStack = outputs.stream().flatMap(t -> t.stream()).filter(t -> t.getItem() == item).findAny();
                        if (optionalStack.isPresent()) {
                            stack = stack.copy();
                            stack.setCount(optionalStack.get().getCount());
                        }
                    } else {
                        shouldReplace = false;
                        break;
                    }
                }
                guiItemStacks.set(0, Collections.singletonList(stack));
            } else if (focus.getMode() == IFocus.Mode.INPUT) {
                Map<String, Object> out = new Object2ObjectOpenHashMap<>();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (!(ingredient instanceof PropertyIngredient)) continue;
                    PropertyIngredient prop = (PropertyIngredient) ingredient;
                    if (prop.test(stack)) {
                        Object obj = MaterialRecipe.getMat(prop, stack);
                        int i = 0;
                        for (Ingredient innerIngredient : recipe.getIngredients()) {
                            if (!(innerIngredient instanceof PropertyIngredient)) {
                                i++;
                                continue;
                            }
                            PropertyIngredient inner = (PropertyIngredient) innerIngredient;
                            if (inner.getId().equals(prop.getId())) {
                                List<ItemStack> st = Arrays.stream(inner.getItems()).filter(t -> MaterialRecipe.getMat(inner, t).equals(obj)).collect(Collectors.toList());
                                if (st.size() > 0) {
                                    newInputs.set(i, st);
                                    out.put(prop.getId(), obj);
                                } else {
                                    shouldReplace = false;
                                    break;
                                }
                            }
                            i++;
                        }
                        break;
                    }
                }
                List<ItemStack> result = outputs.stream().map(t -> t.get(0)).filter(t -> {
                    Map<String, Object> o = recipe.builder.getFromResult(t);
                    boolean ok = true;
                    for (Map.Entry<String, Object> objectEntry : o.entrySet()) {
                        Object inner = out.get(objectEntry.getKey());
                        ok &= inner != null && inner.equals(objectEntry.getValue());
                    }
                    return ok;
                }).collect(Collectors.toList());
                if (result.size() > 0) {
                    guiItemStacks.set(craftOutputSlot, result);
                } else {
                    guiItemStacks.set(craftOutputSlot, outputs.stream().flatMap(Collection::stream).collect(Collectors.toList()));
                }
            }
        } else {
            guiItemStacks.set(craftOutputSlot, outputs.stream().flatMap(Collection::stream).collect(Collectors.toList()));
        }

        guiItemStacks.addTooltipCallback((a, b, c, d) -> {
            if (b) {
              /*  Ingredient i = recipe.getIngredients().get(a-craftInputSlot1);
                if (i instanceof PropertyIngredient) {
                    PropertyIngredient p = (PropertyIngredient) i;
                    d.add(new StringTextComponent("Property: ").append(new StringTextComponent(p.getId().substring(0,1).toUpperCase() + p.getId().substring(1)).mergeStyle(TextFormatting.GOLD)));
                }*/
            }
        });
        guiItemStacks.addTooltipCallback((a, b, c, d) -> {
            if (!b) {
                Map<String, Object> o = recipe.builder.getFromResult(c);
                d.add(new StringTextComponent("Properties:").withStyle(TextFormatting.GOLD));
                o.forEach((k, v) -> d.add(new StringTextComponent(k.substring(0, 1).toUpperCase() + k.substring(1)).append(new StringTextComponent(" - " + v.toString()))));
            }
        });
        helper.setInputs(guiItemStacks, shouldReplace ? newInputs : inputs, recipe.getWidth(), recipe.getHeight());
    }
}
