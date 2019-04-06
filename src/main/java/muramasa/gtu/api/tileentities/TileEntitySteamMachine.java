package muramasa.gtu.api.tileentities;

public class TileEntitySteamMachine extends TileEntityItemFluidMachine {

    @Override
    public boolean consumeResourceForRecipe() {
        //TODO handle steam instead of energy
        return super.consumeResourceForRecipe();
    }
}
