package muramasa.antimatter.capability.rf;

import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.energy.base.EnergySnapshot;

public class RFSnapshot implements EnergySnapshot {
    private final long energy, maxInput, maxOutput;

    public RFSnapshot(RFHandler container){
        this.energy = container.getStoredEnergy();
        this.maxInput = container.maxInsert();
        this.maxOutput = container.maxExtract();
    }
    @Override
    public void loadSnapshot(EnergyContainer container) {
        container.setEnergy(this.energy);
        if (container instanceof RFHandler handler){
            handler.setMaxInput(this.maxInput);
            handler.setMaxOutput(this.maxOutput);
        }
    }
}
