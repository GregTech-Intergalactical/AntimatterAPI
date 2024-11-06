package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeBuilder;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.antimatter.Machines")
public class CTRecipeBuilder {
    RecipeBuilder recipeBuilder;



    public CTRecipeBuilder(String mapId) {
        IRecipeMap map = AntimatterAPI.get(IRecipeMap.class, mapId);
        if(!(map instanceof RecipeMap<?> recipeMap)) {
            throw new IllegalArgumentException("Invalid recipe map: " + mapId);
        }
        recipeBuilder = recipeMap.RB();
    }

    public CTRecipeBuilder ii(IIngredient... itemInput) {
        List<Ingredient> input = itemInput == null ? Collections.emptyList() : Arrays.stream(itemInput).map(IIngredient::asVanillaIngredient).toList();
        recipeBuilder.ii(input);
        return this;
    }

    public CTRecipeBuilder io(IItemStack... itemOutput){
        ItemStack[] outputs = itemOutput == null || itemOutput.length == 0 ? IRecipeMap.EMPTY_ITEM : Arrays.stream(itemOutput).map(IItemStack::getInternal).toArray(ItemStack[]::new);
        recipeBuilder.io(outputs);
        return this;
    }

    public CTRecipeBuilder fi(IFluidStack... fluidInput){
        List<FluidIngredient> fluidIn = fluidInput == null ? Collections.emptyList() : Arrays.stream(fluidInput).map(t -> FluidIngredient.of(CrafttweakerUtils.INSTANCE.fromIFluidStack(t))).toList();
        recipeBuilder.fi(fluidIn.toArray(new FluidIngredient[0]));
        return this;
    }

    public CTRecipeBuilder fo(IFluidStack... fluidOutput){
        FluidHolder[] fluidOut = fluidOutput == null ? IRecipeMap.EMPTY_FLUID : Arrays.stream(fluidOutput).map(CrafttweakerUtils.INSTANCE::fromIFluidStack).toArray(FluidHolder[]::new);
        recipeBuilder.fo(fluidOut);
        return this;
    }

    public CTRecipeBuilder outputChances(double... chances){
        recipeBuilder.outputChances(chances);
        return this;
    }

    public CTRecipeBuilder inputChances(double... chances){
        recipeBuilder.inputChances(chances);
        return this;
    }

    public CTRecipeBuilder hide() {
        recipeBuilder.hide();
        return this;
    }

    public CTRecipeBuilder fake(){
        recipeBuilder.fake();
        return this;
    }

    public void build(String id, long duration, long power, long special) {
        build(id, duration, power, special, 1);
    }

    public void build(String domain, String id, long duration, long power, long special, int amps) {
        recipeBuilder.add(domain, id, duration, power, special, amps);
    }

    public void build(String id, long duration, long power, long special, int amps) {
        build(CraftTweakerConstants.MOD_ID, id, duration, power, special, amps);
    }

    public void build(String id, long duration, long power) {
        build(id, duration, power, 0);
    }

    public void build(String id, long duration) {
        build(id, duration, 0, 0);
    }
}
