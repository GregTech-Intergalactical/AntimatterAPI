package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.blocks.BlockMachine;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.recipe.RecipeMap;

public class RecipeMachine<B extends RecipeBuilder> extends Machine {

    public RecipeMachine(String id, BlockMachine block, B builder, Class tileClass) {
        super(id, block, tileClass);
        recipeMap = new RecipeMap(id, builder);
        addFlags(MachineFlag.RECIPE);
    }

    public void setRecipeMap(RecipeMap map) {
        this.recipeMap = map;
    }

    public B RB() {
        return (B) recipeMap.RB();
    }
}
