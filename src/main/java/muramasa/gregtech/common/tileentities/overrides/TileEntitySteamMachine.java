package muramasa.gregtech.common.tileentities.overrides;

public class TileEntitySteamMachine extends TileEntityBasicMachine {

    @Override
    public boolean consumeResourceForRecipe() {
        //TODO handle steam instead of energy
        return super.consumeResourceForRecipe();
    }
}
