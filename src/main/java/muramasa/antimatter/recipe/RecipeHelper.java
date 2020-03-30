package muramasa.antimatter.recipe;

import com.google.common.collect.Lists;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeHelper {

    private static HashMap<Character, String> REPLACEMENTS = new HashMap<>();

    public static boolean ALWAYS_USE_ORE_DICT = true;


    /*
    static {
        REPLACEMENTS.put('d', AntimatterToolType.SCREWDRIVER.getOreDict());
        REPLACEMENTS.put('f', AntimatterToolType.FILE.getOreDict());
        REPLACEMENTS.put('h', AntimatterToolType.HAMMER.getOreDict());
        REPLACEMENTS.put('k', AntimatterToolType.KNIFE.getOreDict());
        REPLACEMENTS.put('m', AntimatterToolType.MORTAR.getOreDict());
        REPLACEMENTS.put('s', AntimatterToolType.SAW.getOreDict());
        REPLACEMENTS.put('w', AntimatterToolType.WRENCH.getOreDict());
        REPLACEMENTS.put('x', AntimatterToolType.WIRE_CUTTER.getOreDict());
    }

     */

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
        //TODO
//        if (result != null && !Utils.areItemsValid(result)) {
//            Utils.onInvalidData("CRAFTING RECIPE ERROR: OUTPUT STACK INVALID!");
//            return;
//        }
//        IRecipe recipe = new ShapedOreRecipe(null, Unifier.get(result), parse(data, true)).setRegistryName(path);
//        ForgeRegistries.RECIPES.register(recipe);
    }

    /**
     * @see RecipeHelper#addShaped(String, ItemStack, Object...) for char references
     */
    public static void addShapeless(String path, ItemStack result, Object... data) {
        //TODO
//        if (result != null && !Utils.areItemsValid(result)) {
//            Utils.onInvalidData("CRAFTING RECIPE ERROR: OUTPUT STACK INVALID!");
//            return;
//        }
//        IRecipe recipe = new ShapelessOreRecipe(null, Unifier.get(result), parse(data, false)).setRegistryName(path);
//        ForgeRegistries.RECIPES.register(recipe);
    }
    
    public static void removeRecipeByName(String location) {
        removeRecipeByName(new ResourceLocation(location));
    }
    
    /**
     * Providing removeRecipeByName only. As getting getRecipeOutput means looping through
     */
    public static void removeRecipeByName(ResourceLocation location) {
        //TODO
//        ForgeRegistries.RECIPES.register(new DummyRecipe().setRegistryName(location));
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
            if (dataList.get(i) instanceof AntimatterToolType) {
                dataList.set(i, ((AntimatterToolType) dataList.get(i)).getId());
            } else if (dataList.get(i) instanceof ItemStack) {
                Item item = ((ItemStack) dataList.get(i)).getItem();
                if (ALWAYS_USE_ORE_DICT && item instanceof MaterialItem) {
                    // dataList.set(i, ((MaterialItem) item).getType().oreName(((MaterialItem) item).getMaterial()));
                }
            } /*else if (dataList.get(i) instanceof Character) {
                String replacement = REPLACEMENTS.get(dataList.get(i));
                if (replacement != null) dataList.set(i, replacement);
            }*/
        }

        return dataList.toArray();
    }

    public static void addSmelting(ItemStack input, ItemStack output, float xp) {
        //TODO
//        if (input != null && !Utils.areItemsValid(input)) {
//            Utils.onInvalidData("FURNACE RECIPE ERROR: INPUT STACK INVALID!");
//            return;
//        }
//        GameRegistry.addSmelting(input, Unifier.get(output), xp);
    }

    public static void addSmelting(ItemStack input, ItemStack output) {
        addSmelting(input, output, 1.0f);
    }
    
    public static void removeSmelting(ItemStack output) {
        //TODO
//        ItemStack recipeResult;
//        Map<ItemStack,ItemStack> recipes = FurnaceRecipes.instance().getSmeltingList();
//        Iterator<ItemStack> iterator = recipes.keySet().iterator();
//        while(iterator.hasNext()) {
//            ItemStack tmpRecipe = iterator.next();
//            recipeResult = recipes.get(tmpRecipe);
//            if (ItemStack.areItemStacksEqual(output, recipeResult)) {
//                iterator.remove();
//            }
//        }
    }

    public static String[] getOres(ItemStack stack) {
        //TODO
//        if (stack.isEmpty()) return new String[0];
//        int[] ids = OreDictionary.getOreIDs(stack);
//        if (ids.length == 0) return new String[0];
//        String[] names = new String[ids.length];
//        for (int i = 0; i < ids.length; i++) {
//            names[i] = OreDictionary.getOreName(ids[i]);
//        }
//        return names;
        return new String[0];
    }
}
