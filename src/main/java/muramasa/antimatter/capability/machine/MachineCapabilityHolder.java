package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.CapabilityHolder;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;

import java.util.function.Function;

public class MachineCapabilityHolder<T extends ICapabilityHandler> extends CapabilityHolder<TileEntityMachine, T> {

    private MachineFlag flag;

    public MachineCapabilityHolder(TileEntityMachine tile, MachineFlag flag, Dist side) {
        super(tile, side);
        this.flag = flag;
    }

    public MachineCapabilityHolder(TileEntityMachine tile, MachineFlag flag) {
        super(tile);
        this.flag = flag;
    }

    public MachineCapabilityHolder(TileEntityMachine tile) {
        this(tile, null);
    }

    @Override
    public boolean canInit() {
        boolean canInit = super.canInit();
        if (flag == null) return canInit;
        return tile.has(flag) && canInit;
    }
}
