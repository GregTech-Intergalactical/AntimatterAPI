package muramasa.gtu.api.recipe;

import com.google.common.collect.Lists;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RecipeHelper {

    private static HashMap<Character, String> REPLACEMENTS = new HashMap<>();

    public static boolean ALWAYS_USE_ORE_DICT = true;

    static {
        REPLACEMENTS.put('d', ToolType.SCREWDRIVER.getOreDict());
        REPLACEMENTS.put('f', ToolType.FILE.getOreDict());
        REPLACEMENTS.put('h', ToolType.HAMMER.getOreDict());
        REPLACEMENTS.put('k', ToolType.KNIFE.getOreDict());
        REPLACEMENTS.put('m', ToolType.MORTAR.getOreDict());
        REPLACEMENTS.put('s', ToolType.SAW.getOreDict());
        REPLACEMENTS.put('w', ToolType.WRENCH.getOreDict());
        REPLACEMENTS.put('x', ToolType.WIRE_CUTTER.getOreDict());
    }

    /**
     * The below are the valid chars to use in the recipes
     * <ul>
     * <li>d = Screwdriver</li>
     * <li>f = File</li>
     * <li>h = Hammer</li>
     * <li>k = Knife</li>
     * <li>m = Mortar</li>
     * <li>s = Saw</li>
     * <li>w = Wrench</li>
     * <li>x = Wire Cutter</li>
     * </ul>
     */
    public static void addShaped(String path, ItemStack result, Object... data) {
        if (result != null && !Utils.areItemsValid(result)) {
            Utils.onInvalidData("CRAFTING RECIPE ERROR: OUTPUT STACK INVALID!");
            return;
        }
        IRecipe recipe = new ShapedOreRecipe(null, Unifier.get(result), parse(data, true)).setRegistryName(path);
        ForgeRegistries.RECIPES.register(recipe);
    }

    /**
     * @see RecipeHelper#addShaped(String, ItemStack, Object...) for char references
     */
    public static void addShapeless(String path, ItemStack result, Object... data) {
        if (result != null && !Utils.areItemsValid(result)) {
            Utils.onInvalidData("CRAFTING RECIPE ERROR: OUTPUT STACK INVALID!");
            return;
        }
        IRecipe recipe = new ShapelessOreRecipe(null, Unifier.get(result), parse(data, false)).setRegistryName(path);
        ForgeRegistries.RECIPES.register(recipe);
    }

    public static Object[] parse(Object[] data, boolean shaped) {
        ArrayList<Object> dataList = Lists.newArrayList(data);
        if (shaped) { //Insert tool dataList replacements
            if (!(dataList.get(0) instanceof String) || !(dataList.get(1) instanceof String) || !(dataList.get(2) instanceof String)) {
                throw new IllegalArgumentException("Shaped recipes require 3 initial string args");
            }
            char[] shape = ((String) dataList.get(0) + dataList.get(1) + dataList.get(2)).toCharArray();
            String current;
            for (int c = 0; c < shape.length; c++) {
                current = REPLACEMENTS.get(shape[c]);
                if (current != null) {
                    dataList.add(shape[c]);
                    dataList.add(current);
                }
            }
        }
        //Format supported alternate entries into valid types
        int start = shaped ? 3 : 0;
        for (int i = start; i < dataList.size(); i++) {
            if (dataList.get(i) instanceof ToolType) {
                dataList.set(i, ((ToolType) dataList.get(i)).getOreDict());
            } else if (dataList.get(i) instanceof ItemStack) {
                Item item = ((ItemStack) dataList.get(i)).getItem();
                if (ALWAYS_USE_ORE_DICT && item instanceof MaterialItem) {
                    dataList.set(i, ((MaterialItem) item).getType().oreName(((MaterialItem) item).getMaterial()));
                }
            } /*else if (dataList.get(i) instanceof Character) {
                String replacement = REPLACEMENTS.get(dataList.get(i));
                if (replacement != null) dataList.set(i, replacement);
            }*/
        }

        return dataList.toArray();
    }

    public static void addSmelting(ItemStack input, ItemStack output, float xp) {
        if (input != null && !Utils.areItemsValid(input)) {
            Utils.onInvalidData("FURNACE RECIPE ERROR: INPUT STACK INVALID!");
            return;
        }
        GameRegistry.addSmelting(input, Unifier.get(output), xp);
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

    public static String[] getOres(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        String[] names = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            names[i] = OreDictionary.getOreName(ids[i]);
        }
        return names;
    }

    public static ArrayList<String> getOreNames(ItemStack stack) {
        ArrayList<String> names = new ArrayList<>();
        if (stack.isEmpty()) return names;
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ArrayUtils.isEmpty(ids)) return names;
        for (int id : ids) {
            names.add(OreDictionary.getOreName(id));
        }
        return names;
    }
}
