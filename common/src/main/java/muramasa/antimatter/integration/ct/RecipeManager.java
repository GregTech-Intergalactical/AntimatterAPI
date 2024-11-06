package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.map.IRecipeMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.antimatter.Machines")
public class RecipeManager implements IRecipeManager<IRecipe> {

    @Override
    public RecipeType<IRecipe> getRecipeType() {
        return Recipe.RECIPE_TYPE;
    }

    @ZenCodeType.Method
    public CTRecipeBuilder recipeBuilder(String mapId){
        return new CTRecipeBuilder(mapId);
    }
}