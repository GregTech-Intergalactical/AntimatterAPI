package muramasa.gtu.api.recipe;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeHelper {
	
	 /*
	 * TODO: Group argument null?
	 * 
	 * TODO: If we ever have an internal mutable unificator, we can apply it to the output.
	 * 
	 * TODO: Wouldn't necessarily apply here, but through an event we can apply the ToolType sounds to play after crafting
	 */

    public static void addShaped(ItemStack output, Object... data) {
        if (output.getItem().getRegistryName() == null) throw new NullPointerException("addShaped: output registry name null");
        GameRegistry.addShapedRecipe(output.getItem().getRegistryName(), new ResourceLocation(Ref.MODID, "shaped"), output, data);
    }

    public static void addShapeless(ItemStack output, ItemStack... inputs) {
        if (output.getItem().getRegistryName() == null) throw new NullPointerException("addShapeless: output registry name null");
        GameRegistry.addShapelessRecipe(output.getItem().getRegistryName(), new ResourceLocation(Ref.MODID, "shapeless"), output, Ingredient.fromStacks(inputs));
    }
    
    public static void addShaped(String regName, ItemStack result, Object... recipe) {
        boolean skip = false;
        if (result.isEmpty()) {
        	GregTech.LOGGER.error("addShaped: result cannot be an empty ItemStack. Recipe: {}", regName);
            skip = true;
        }
        
        skip |= validateRecipe(regName, recipe);

        IRecipe shapedOreRecipe = new ShapedOreRecipe(null, result.copy(), finalizeShapedRecipeInput(recipe))
            .setMirrored(false) //Use addMirroredShapedRecipe
            .setRegistryName(regName);
        ForgeRegistries.RECIPES.register(shapedOreRecipe);
    }
    
    public static void addMirroredShaped(String regName, ItemStack result, Object... recipe) {
        boolean skip = false;
        if (result.isEmpty()) {
        	GregTech.LOGGER.error("addMirroredShaped: result cannot be an empty ItemStack. Recipe: {}", regName);
            skip = true;
        }
        
        skip |= validateRecipe(regName, recipe);

        IRecipe shapedOreRecipe = new ShapedOreRecipe(new ResourceLocation(Ref.MODID, "shaped"), result.copy(), finalizeShapedRecipeInput(recipe))
            .setMirrored(true)
            .setRegistryName(regName);
        ForgeRegistries.RECIPES.register(shapedOreRecipe);
	}
    
    public static void addShapeless(String regName, ItemStack result, Object... recipe) {
    	boolean skip = false;
        if (result.isEmpty()) {
            GregTech.LOGGER.error("Result cannot be an empty ItemStack. Recipe: {}", regName);
            skip = true;
        }
        
        skip |= validateRecipe(regName, recipe);
        
        for (byte i = 0; i < recipe.length; i++) {
            if (recipe[i] instanceof Character) {
            	String toolName = getToolNameByCharacter((char) recipe[i]);
                if (toolName == null) {
                    throw new IllegalArgumentException("Tool name is not found for char " + recipe[i]);
                }
                recipe[i] = toolName;
            } else if (!(recipe[i] instanceof ItemStack
                || recipe[i] instanceof Item
                || recipe[i] instanceof Block
                || recipe[i] instanceof String)) {
                throw new IllegalArgumentException(recipe.getClass().getSimpleName() + " type is not suitable for crafting input.");
            }
        }

        IRecipe shapelessRecipe = new ShapelessOreRecipe(new ResourceLocation(Ref.MODID, "shapeless"), result.copy(), recipe)
            .setRegistryName(regName);        

        try {
        	//MC Bug to fix when somehow the recipe inputs become all enchanted
            Field field = ShapelessOreRecipe.class.getDeclaredField("isSimple");
            field.setAccessible(true);
            field.setBoolean(shapelessRecipe, false);
        } catch (ReflectiveOperationException exception) {
            GregTech.LOGGER.error("Failed to mark shapeless recipe as complex", exception);
        }
        ForgeRegistries.RECIPES.register(shapelessRecipe);
    }
    
    public static Object[] finalizeShapedRecipeInput(Object... recipe) {
        for (byte i = 0; i < recipe.length; i++) {
        	if (!(recipe[i] instanceof ItemStack
        			|| recipe[i] instanceof Item
        			|| recipe[i] instanceof Block
        			|| recipe[i] instanceof String
        			|| recipe[i] instanceof Character
        			|| recipe[i] instanceof Boolean)) {
        		throw new IllegalArgumentException(recipe.getClass().getSimpleName() + " type is not suitable for crafting input.");
            }
        }

        int idx = 0;
        ArrayList<Object> recipeList = new ArrayList<>(Arrays.asList(recipe));
        
        while (recipe[idx] instanceof String) {
            StringBuilder s = new StringBuilder((String) recipe[idx++]);
            while (s.length() < 3) s.append(" ");
            if (s.length() > 3) throw new IllegalArgumentException();
            for (char c : s.toString().toCharArray()) {
                String toolName = getToolNameByCharacter(c);
                if (toolName != null) {
                    recipeList.add(c);
                    recipeList.add(toolName);
                }
            }
        }
        return recipeList.toArray();
    }
    
    private static boolean validateRecipe(String regName, Object... recipe) {
        boolean skip = false;
        if (recipe == null) {
            GregTech.LOGGER.error("Recipe cannot be null", new IllegalArgumentException());
            skip = true;
        } else if (recipe.length == 0) {
            GregTech.LOGGER.error("Recipe cannot be empty", new IllegalArgumentException());
            skip = true;
        } else if (Arrays.asList(recipe).contains(null) || Arrays.asList(recipe).contains(ItemStack.EMPTY) || Arrays.asList(recipe).contains(Blocks.AIR)) { //I have seen Blocks.AIR happen before...
            GregTech.LOGGER.error("Recipe cannot contain null elements or empty ItemStacks. Recipe: {}",
                Arrays.stream(recipe)
                    .map(o -> o == null ? "NULL" : o)
                    .map(o -> o == ItemStack.EMPTY ? "EMPTY STACK" : o)
                    .map(Object::toString)
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", ")));
            GregTech.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        } else if (ForgeRegistries.RECIPES.containsKey(new ResourceLocation(Ref.MODID, regName))) {
            GregTech.LOGGER.error("Tried to register recipe, {}, with duplicate key. Recipe: {}", regName,
                Arrays.stream(recipe)
                    .map(Object::toString)
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", ")));
            GregTech.LOGGER.error("Stacktrace:", new IllegalArgumentException());
            //skip = true;
        }
        return skip;
    }
    
    //TODO: Reference ToolType maybe...
    //craftingToolForgeHammer = Hammer or Hard Hammer, apparently the convention was craftingToolForgeHammer and not just craftingToolHammer...
    private @Nullable static String getToolNameByCharacter(char character) {
        switch (character) {
            case 'd':
                return ToolType.SCREWDRIVER.getOreDict();
            case 'f':
                return ToolType.FILE.getOreDict();
            case 'h':
                return ToolType.HAMMER.getOreDict();
            case 'm':
                return ToolType.MORTAR.getOreDict();
            case 's':
                return ToolType.SAW.getOreDict();
            case 'w':
                return ToolType.WRENCH.getOreDict();
            case 'x':
                return ToolType.WIRE_CUTTER.getOreDict();
            default:
            	//Becomes empty
                return null;
        }
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

    public static List<String> getOreNames(ItemStack stack) {
        ArrayList<String> names = new ArrayList<>();
        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int i = 0; i < oreIds.length; i++) {
            names.add(OreDictionary.getOreName(oreIds[i]));
        }
        return names;
    }

    public static ItemStack getFirstOreDict(String name) {
        NonNullList<ItemStack> stacks = OreDictionary.getOres(name);
        return stacks.get(0);
    }

    public static String getOreName(ItemStack stack) {
        return OreDictionary.getOreName(OreDictionary.getOreIDs(stack)[0]);
    }
}
