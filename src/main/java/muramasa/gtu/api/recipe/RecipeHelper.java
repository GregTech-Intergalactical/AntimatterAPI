package muramasa.gtu.api.recipe;

import com.google.common.collect.Lists;
import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RecipeHelper {

    private static HashMap<Character, String> REPLACEMENTS = new HashMap<>();
    private static ArrayList<String> MOD_PRIORITY = new ArrayList<>(); //TODO config

    private static boolean ALWAYS_USE_ORE_DICT = true;
    private static boolean USE_MOD_PRIORITY = true;

    static {
        REPLACEMENTS.put('d', ToolType.SCREWDRIVER.getOreDict());
        REPLACEMENTS.put('f', ToolType.FILE.getOreDict());
        REPLACEMENTS.put('h', ToolType.HAMMER.getOreDict());
        REPLACEMENTS.put('m', ToolType.MORTAR.getOreDict());
        REPLACEMENTS.put('s', ToolType.SAW.getOreDict());
        REPLACEMENTS.put('w', ToolType.WRENCH.getOreDict());
        //REPLACEMENTS.put('x', ToolType.WIRE_CUTTER.getOreDict());
        REPLACEMENTS.put('x', Prefix.Plate.oreName(Materials.Osmium));

        MOD_PRIORITY.add("ic2");
        MOD_PRIORITY.add(Ref.MOD_TF);
    }

    public static void addShaped(String path, ItemStack result, Object... data) {
        IRecipe recipe = new ShapedOreRecipe(null, parseResult(result).copy(), parseData(data, true)).setRegistryName(path);
        ForgeRegistries.RECIPES.register(recipe);
    }

    public static void addShapeless(String path, ItemStack result, Object... data) {
        IRecipe recipe = new ShapelessOreRecipe(null, parseResult(result).copy(), parseData(data, false)).setRegistryName(path);
        ForgeRegistries.RECIPES.register(recipe);
    }

    public static ItemStack parseResult(ItemStack stack) {
        if (!USE_MOD_PRIORITY || !(stack.getItem() instanceof MaterialItem)) return stack;
        String dict = ((MaterialItem) stack.getItem()).getPrefix().oreName(((MaterialItem) stack.getItem()).getMaterial());
        NonNullList<ItemStack> matchingStacks = OreDictionary.getOres(dict);
        if (matchingStacks.size() == 0) return stack;
        for (int i = 0; i < MOD_PRIORITY.size(); i++) {
            for (int j = 0; j < matchingStacks.size(); j++) {
                if (matchingStacks.get(j).getItem().getRegistryName() == null) continue;
                if (matchingStacks.get(j).getItem().getRegistryName().getResourceDomain().equals(MOD_PRIORITY.get(i))) {
                    return matchingStacks.get(j);
                }
            }
        }
        return stack;
    }

    public static Object[] parseData(Object[] data, boolean shaped) {
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
                    dataList.set(i, ((MaterialItem) item).getPrefix().oreName(((MaterialItem) item).getMaterial()));
                }
            } /*else if (dataList.get(i) instanceof Character) {
                String replacement = REPLACEMENTS.get(dataList.get(i));
                if (replacement != null) dataList.set(i, replacement);
            }*/
        }

        return dataList.toArray();
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

    public static String[] getOreNames(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        String[] names = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            names[i] = OreDictionary.getOreName(ids[i]);
        }
        return names;
    }
}
