package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.tileentities.TileEntityItemMachine;

import static muramasa.gtu.api.machines.MachineFlag.ITEM;

public class ItemMachine extends BasicMachine {

    public ItemMachine(String name) {
        super(name, TileEntityItemMachine.class);
        addFlags(ITEM);
    }

    public ItemMachine(String name, Class tileClass) {
        this(name);
        setTileClass(tileClass);
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return RecipeMap.findRecipeItem(recipeMap, stackHandler.getInputs());
    }
}
