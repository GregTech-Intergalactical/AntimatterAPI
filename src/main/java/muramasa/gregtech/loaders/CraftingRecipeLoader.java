package muramasa.gregtech.loaders;

import muramasa.gregtech.api.interfaces.GregTechRegistrar;

public class CraftingRecipeLoader {

    public static void init() {

        //TODO

        for (GregTechRegistrar registrar : GregTechRegistry.getRegistrars()) {
            registrar.onCraftingRecipeRegistration();
        }
    }
}
