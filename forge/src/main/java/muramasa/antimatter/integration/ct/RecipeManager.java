package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.antimatter.RecipeManager")
@IRecipeHandler.For(Recipe.class)
public class RecipeManager implements IRecipeManager<Recipe>, IRecipeHandler<Recipe> {

    @Override
    public String dumpToCommandString(IRecipeManager iRecipeManager, Recipe recipe) {
        return recipe.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Recipe> getAllRecipes() {
        return AntimatterAPI.all(RecipeMap.class).stream().flatMap(t -> t.getRecipes(false).stream()).toList();
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return Recipe.RECIPE_TYPE;
    }
    @ZenCodeType.Method
    public void addRecipe(String name, String map, IIngredient[] in, IItemStack[] out, IFluidStack[] fIn, IFluidStack[] fOut, long eu, int duration, int amps, int special) {
        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation(Ref.ID, name);
        List<RecipeIngredient> input = in == null ? Collections.emptyList() : Arrays.stream(in).map(inputs -> RecipeIngredient.of(inputs.asVanillaIngredient(), 1)).toList();
        ItemStack[] itemOut = out == null ? RecipeMap.EMPTY_ITEM : Arrays.stream(out).map(IItemStack::getInternal).toArray(ItemStack[]::new);
        List<FluidIngredient> fluidIn = fIn == null ? Collections.emptyList() : Arrays.stream(fIn).map(t -> FluidIngredient.of(t.getInternal())).toList();
        FluidStack[] fluidOut = fOut == null ? RecipeMap.EMPTY_FLUID : Arrays.stream(fOut).map(IFluidStack::getInternal).toArray(FluidStack[]::new);
        Recipe recipe = new Recipe(input, itemOut, fluidIn, fluidOut, duration, eu, special, amps);
        recipe.setIds(resourceLocation, map);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe));
    }
    @ZenCodeType.Method
    public void addRecipeSimpleFluid(String name, String map, IIngredient[] in, IItemStack[] out, IFluidStack[] fIn, IFluidStack[] fOut, long eu, int duration) {
        addRecipe(name, map, in, out , fIn, fOut, eu, duration, 1, 0);
    }
    @ZenCodeType.Method
    public void addRecipeSimple(String name, String map, IIngredient[] in, IItemStack[] out, long eu, int duration) {
        addRecipe(name, map, in, out , null, null, eu, duration, 1, 0);
    }
}