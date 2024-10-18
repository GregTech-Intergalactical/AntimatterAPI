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
@ZenCodeType.Name("mods.antimatter.RecipeManager")
@IRecipeHandler.For(IRecipe.class)
public class RecipeManager implements IRecipeManager<IRecipe>, IRecipeHandler<IRecipe> {

    @Override
    public String dumpToCommandString(IRecipeManager iRecipeManager, IRecipe recipe) {
        return recipe.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IRecipe> getAllRecipes() {
        return AntimatterAPI.all(IRecipeMap.class).stream().flatMap(t -> t.getRecipes(false).stream()).toList();
    }

    @Override
    public RecipeType<IRecipe> getRecipeType() {
        return Recipe.RECIPE_TYPE;
    }
    @ZenCodeType.Method
    public void addRecipe(String name, String map, IIngredient[] in, IItemStack[] out, IFluidStack[] fIn, IFluidStack[] fOut, long eu, int duration, int amps, int special) {
        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation(Ref.ID, name);
        List<Ingredient> input = in == null ? Collections.emptyList() : Arrays.stream(in).map(IIngredient::asVanillaIngredient).toList();
        ItemStack[] itemOut = out == null ? IRecipeMap.EMPTY_ITEM : Arrays.stream(out).map(IItemStack::getInternal).toArray(ItemStack[]::new);
        List<FluidIngredient> fluidIn = fIn == null ? Collections.emptyList() : Arrays.stream(fIn).map(t -> FluidIngredient.of(CrafttweakerUtils.INSTANCE.fromIFluidStack(t))).toList();
        FluidHolder[] fluidOut = fOut == null ? IRecipeMap.EMPTY_FLUID : Arrays.stream(fOut).map(CrafttweakerUtils.INSTANCE::fromIFluidStack).toArray(FluidHolder[]::new);
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