package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.CapabilityHolder;
import muramasa.antimatter.capability.CapabilitySide;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityMachine;

import java.util.function.Function;

public class MachineCapabilityHolder<T extends ICapabilityHandler> extends CapabilityHolder<TileEntityMachine, T> {

    private MachineFlag flag;

    public MachineCapabilityHolder(TileEntityMachine tile, MachineFlag flag, CapabilitySide side) {
        super(tile, side);
        this.flag = flag;
    }

    @Override
    public void init(Function<TileEntityMachine, T> capFunc) {
        if (canInit && (flag != null && tile.has(flag))) {
            handler = capFunc.apply(tile);
        }
    }
}
