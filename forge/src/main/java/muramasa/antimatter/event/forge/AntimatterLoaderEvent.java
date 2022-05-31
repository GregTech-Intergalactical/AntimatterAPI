package muramasa.antimatter.event.forge;

import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;

public class AntimatterLoaderEvent extends AntimatterEvent {

    public final IRecipeRegistrate registrat;

    public AntimatterLoaderEvent(IAntimatterRegistrar registrar, IRecipeRegistrate reg) {
        super(registrar);
        this.registrat = reg;
    }
}
