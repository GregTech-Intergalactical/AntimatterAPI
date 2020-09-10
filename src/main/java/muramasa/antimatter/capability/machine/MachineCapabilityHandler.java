package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.CapabilityHandler;
import muramasa.antimatter.capability.CapabilitySide;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.capability.CapabilitySide.SERVER;

public class MachineCapabilityHandler<T extends ICapabilityHandler> extends CapabilityHandler<TileEntityMachine, T> {

    private MachineFlag flag;

    public MachineCapabilityHandler(TileEntityMachine tile, MachineFlag flag, CapabilitySide side) {
        super(tile, side);
        this.flag = flag;
    }

    public MachineCapabilityHandler(TileEntityMachine tile) {
        this(tile, null, SERVER);
    }

    @Override
    public boolean canInit() {
        boolean canInit = super.canInit();
        if (flag == null) return canInit;
        return tile.has(flag) && canInit;
    }
}
