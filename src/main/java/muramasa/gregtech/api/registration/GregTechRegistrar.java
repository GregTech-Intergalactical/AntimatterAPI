package muramasa.gregtech.api.registration;

import net.minecraftforge.fml.common.Loader;

public abstract class GregTechRegistrar {

    public abstract String getId();

    public boolean isEnabled() {
        return Loader.isModLoaded(getId());
    }

    public void onCoverRegistration() {
        //NOOP
    }

    public void onMaterialRegistration() {
        //NOOP
    }

    public void onMaterialInit() {
        //NOOP
    }

    public void onCraftingRecipeRegistration() {
        //NOOP
    }

    public void onMaterialRecipeRegistration() {
        //NOOP
    }

    public void onMachineRecipeRegistration() {
        //NOOP
    }
}
