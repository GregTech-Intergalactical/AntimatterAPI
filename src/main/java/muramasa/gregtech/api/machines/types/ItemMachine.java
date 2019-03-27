package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.api.tileentities.TileEntityItemMachine;

import static muramasa.gregtech.api.machines.MachineFlag.ITEM;

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
