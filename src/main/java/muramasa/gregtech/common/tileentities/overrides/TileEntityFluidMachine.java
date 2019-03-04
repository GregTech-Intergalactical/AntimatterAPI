package muramasa.gregtech.common.tileentities.overrides;

public class TileEntityFluidMachine extends TileEntityBasicMachine {

    @Override
    public void consumeInputs() {

    }

    @Override
    public boolean canOutput() {
        return false;
    }

    @Override
    public void addOutputs() {

    }

    @Override
    public boolean canRecipeContinue() {
        return false;
    }
}
