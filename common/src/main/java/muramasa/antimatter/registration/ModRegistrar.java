package muramasa.antimatter.registration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;

/**
 * ModRegistrar is the base class for registering mod registrars.
 */
public abstract class ModRegistrar implements IAntimatterObject {

    public ModRegistrar() {
        AntimatterAPI.register(ModRegistrar.class, this);
    }

    /**
     * List of modids that need to be loaded to run this registrar.
     *
     * @return list of modids.
     */
    public abstract String[] modIds();

    /**
     * Called when GT maps are initiated. Either add loaders to the registrate
     * or simply use recipe maps manually.
     *
     * @param registrate recipe adder.
     */
    public abstract void antimatterRecipes(IRecipeRegistrate registrate);

    /**
     * Crafting recipes, or regular recipe provider.
     *
     * @param provider the AM provider.
     */
    public abstract void craftingRecipes(AntimatterRecipeProvider provider);
}
