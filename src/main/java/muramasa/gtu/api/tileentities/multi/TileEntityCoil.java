package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.blocks.BlockCoil;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.ComponentHandler;
import muramasa.gtu.api.structure.IComponent;
import muramasa.gtu.api.tileentities.TileEntityBase;

import java.util.List;

public class TileEntityCoil extends TileEntityBase implements IComponent {

    private BlockCoil type;

    protected IComponentHandler componentHandler = new ComponentHandler("null", this) {
        @Override
        public String getId() {
            return ((BlockCoil) getState().getBlock()).getId();
        }
    };

    public BlockCoil getType() {
        return type != null ? type : (type = ((BlockCoil) getState().getBlock()));
    }

    @Override
    public IComponentHandler getComponentHandler() {
        return componentHandler;
    }

    public int getHeatCapacity() {
        return getType().getHeatCapacity();
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Coil Type: " + getType().getId());
        return info;
    }
}
