package muramasa.gregtech.api.recipe;

import muramasa.gregtech.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;
import java.util.Map;

public class RecipeHelper {

    public static void addShaped(ItemStack output, Object... data) {
        if (output.getItem().getRegistryName() == null) throw new NullPointerException("addShaped: output registry name null");
        GameRegistry.addShapedRecipe(output.getItem().getRegistryName(), new ResourceLocation(Ref.MODID, "shaped"), output, data);
    }

    public static void addShapeless(ItemStack output, ItemStack... inputs) {
        if (output.getItem().getRegistryName() == null) throw new NullPointerException("addShapeless: output registry name null");
        GameRegistry.addShapelessRecipe(output.getItem().getRegistryName(), new ResourceLocation(Ref.MODID, "shapeless"), output, Ingredient.fromStacks(inputs));
    }

    public static void addSmelting(ItemStack input, ItemStack output, float xp) {
        GameRegistry.addSmelting(input, output, xp);
    }

    public static void addSmelting(ItemStack input, ItemStack output) {
        addSmelting(input, output, 1.0f);
    }


    public static void removeSmelting(ItemStack output) {
        ItemStack recipeResult;
        Map<ItemStack,ItemStack> recipes = FurnaceRecipes.instance().getSmeltingList();
        Iterator<ItemStack> iterator = recipes.keySet().iterator();
        while(iterator.hasNext()) {
            ItemStack tmpRecipe = iterator.next();
            recipeResult = recipes.get(tmpRecipe);
            if (ItemStack.areItemStacksEqual(output, recipeResult)) {
                iterator.remove();
            }
        }
    }

    public static ItemStack getFirstOreDict(String name) {
        NonNullList<ItemStack> stacks = OreDictionary.getOres(name);
        return stacks.get(0);
    }

    public static String getOreName(ItemStack stack) {
        return OreDictionary.getOreName(OreDictionary.getOreIDs(stack)[0]);
    }
}
