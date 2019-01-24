package muramasa.itech.common.tileentities.overrides;

public class TileEntitySteamMachine extends TileEntityBasicMachine {

    @Override
    public boolean hasResourceForRecipe() {
        return super.hasResourceForRecipe();
        //TODO check steam instead of energy
    }
}
