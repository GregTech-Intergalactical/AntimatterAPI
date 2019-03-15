package muramasa.gregtech.common.tileentities.overrides.multi;

import muramasa.gregtech.api.capability.IComponentHandler;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCoil;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;

import java.util.ArrayList;
import java.util.Map;

public class TileEntityElectricBlastFurnace extends TileEntityMultiMachine {

    private int heatingCapacity;

    @Override
    public void onRecipeFound() {
        int heatDiv = (heatingCapacity - activeRecipe.getSpecialValue()) / 900;

    }

    @Override
    public void onValidStructure() {
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
