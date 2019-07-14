package muramasa.gtu.api.recipe;

import com.google.common.collect.Sets;
import muramasa.gtu.Configs;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public class RecipeBuilder {

    private RecipeMap recipeMap;
    private ItemStack[] itemsInput, itemsOutput;
    private FluidStack[] fluidsInput, fluidsOutput;
    private int[] chances;
    private int duration, special;
    private long power;
    private boolean hidden;
    private Set<RecipeTag> tags;

    public void add() {
        if (itemsInput != null && !Utils.areItemsValid(itemsInput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - INPUT ITEMS INVALID!");
            return;
        }
        if (itemsOutput != null && !Utils.areItemsValid(itemsOutput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT ITEMS INVALID!");
            return;
        }
        if (fluidsInput != null && !Utils.areFluidsValid(fluidsInput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - INPUT FLUIDS INVALID!");
            return;
        }
        if (fluidsOutput != null && !Utils.areFluidsValid(fluidsOutput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT FLUIDS INVALID!");
            return;
        }

        if (Configs.RECIPE.ENABLE_RECIPE_UNIFICATION && itemsOutput != null) {
            for (int i = 0; i < itemsOutput.length; i++) {
                itemsOutput[i] = Unifier.get(itemsOutput[i]);
            }
        }

        //TODO validate item/fluid inputs/outputs do not exceed machine gui values
        //TODO get a recipe build method to machine type so it can be overriden?
        Recipe recipe = new Recipe(
            itemsInput != null ? itemsInput.clone() : null,
            itemsOutput != null ? itemsOutput.clone() : null,
            fluidsInput != null ? fluidsInput.clone() : null,
            fluidsOutput != null ? fluidsOutput.clone() : null,
            duration, power, special
        );
        if (chances != null) recipe.addChances(chances);
        recipe.setHidden(hidden);
        recipeMap.add(recipe);

        itemsInput = itemsOutput = null;
        fluidsInput = fluidsOutput = null;
        chances = null;
        duration = special = 0;
        power = 0;
        hidden = false;
        tags = Sets.newHashSet();
    }

    public void add(long duration, long power, long special) {
        this.duration = (int)duration;
        this.power = power;
        this.special = (int)special;
        add();
    }

    public void add(long duration, long power) {
        add(duration, power, 0);
    }

    public void add(int duration) {
        add(duration, 0, 0);
    }

    public RecipeBuilder ii(ItemStack... stacks) {
        itemsInput = stacks;
        return this;
    }

    public RecipeBuilder io(ItemStack... stacks) {
        itemsOutput = stacks;
        return this;
    }

    public RecipeBuilder fi(FluidStack... stacks) {
        fluidsInput = stacks;
        return this;
    }

    public RecipeBuilder fo(FluidStack... stacks) {
        fluidsOutput = stacks;
        return this;
    }

    /** 10 = 10%, 75 = 75% etc **/
    public RecipeBuilder chances(int... values) {
        chances = values;
        return this;
    }

    public RecipeBuilder hide() {
        hidden = true;
        return this;
    }

    public RecipeBuilder tags(RecipeTag... tags) {
        this.tags = Sets.newHashSet(tags);
        return this;
    }

    public RecipeMap getMap() {
        return recipeMap;
    }

    public void setMap(RecipeMap recipeMap) {
        this.recipeMap = recipeMap;
    }
}
