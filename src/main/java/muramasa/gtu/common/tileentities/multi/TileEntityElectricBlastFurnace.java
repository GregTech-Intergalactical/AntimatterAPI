package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.tileentities.multi.TileEntityCoil;
import muramasa.gtu.api.tileentities.multi.TileEntityItemFluidMultiMachine;

import java.util.ArrayList;
import java.util.Map;

public class TileEntityElectricBlastFurnace extends TileEntityItemFluidMultiMachine {

    private int heatingCapacity;

    @Override
    public void onRecipeFound() {
        int heatDiv = (heatingCapacity - activeRecipe.getSpecialValue()) / 900;

    }

    @Override
    public void onStructureIntegrity(boolean valid) {
        if (valid) {
            heatingCapacity = 0;
            for (Map.Entry<String, ArrayList<IComponentHandler>> entry : components.entrySet()) {
                for (IComponentHandler component : entry.getValue()) {
                    if (component.getTile() instanceof TileEntityCoil) {
                        heatingCapacity += ((TileEntityCoil) component.getTile()).getHeatingCapacity();
                    }
                }
            }
            System.out.println("Heating Capacity: " + heatingCapacity);
        }
    }
}
