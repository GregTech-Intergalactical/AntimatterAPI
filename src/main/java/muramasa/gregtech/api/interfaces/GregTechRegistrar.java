package muramasa.gregtech.api.interfaces;

public abstract class GregTechRegistrar {

    public abstract boolean isEnabled();

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
