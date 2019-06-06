package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.ComponentHandler;
import muramasa.gtu.api.data.Coil;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.tileentities.TileEntityBase;
import muramasa.gtu.api.blocks.BlockCoil;

import java.util.List;

public class TileEntityCoil extends TileEntityBase implements IComponent {

    protected IComponentHandler componentHandler = new ComponentHandler("null", this) {
        @Override
        public String getId() {
            return ((BlockCoil) getState().getBlock()).getType().getName();
        }
    };

    public Coil getType() {
        return ((BlockCoil) getState().getBlock()).getType();
    }

    @Override
    public IComponentHandler getComponentHandler() {
        return componentHandler;
    }

    public int getHeatingCapacity() {
        return getType().getHeatingCapacity();
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Coil Type: " + getType().getName());
        return info;
    }
}
