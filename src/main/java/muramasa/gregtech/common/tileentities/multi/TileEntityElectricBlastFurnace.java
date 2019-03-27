package muramasa.gregtech.common.tileentities.multi;

import muramasa.gregtech.api.capability.IComponentHandler;
import muramasa.gregtech.api.tileentities.multi.TileEntityCoil;
import muramasa.gregtech.api.tileentities.multi.TileEntityMultiMachine;

import java.util.ArrayList;
import java.util.Map;

public class TileEntityElectricBlastFurnace extends TileEntityMultiMachine {

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
