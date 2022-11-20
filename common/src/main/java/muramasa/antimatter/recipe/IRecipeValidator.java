package muramasa.antimatter.recipe;

import muramasa.antimatter.tile.TileEntityMachine;

@FunctionalInterface
public interface IRecipeValidator {
    boolean validate(IRecipe recipe, TileEntityMachine<?> machine);

    default boolean tick(IRecipe recipe, TileEntityMachine<?> machine) {
        return true;
    }
}
