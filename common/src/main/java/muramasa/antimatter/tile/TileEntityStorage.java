package muramasa.antimatter.tile;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTNode;

import java.util.List;

public abstract class TileEntityStorage<T extends TileEntityStorage<T>> extends TileEntityMachine<T> {

    public TileEntityStorage(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0L, (long) getMachineTier().getVoltage() * itemHandler.map(m -> m.getChargeHandler().getSlots()).orElse(1), getMachineTier().getVoltage(), getMachineTier().getVoltage(), 0,0) {
            @Override
            public boolean canOutput(Direction direction) {
                Direction dir = tile.getFacing();
                return dir != null && dir.get3DDataValue() == direction.get3DDataValue();
            }

            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                super.onMachineEvent(event, data);
            }

            @Override
            public void onUpdate() {
                super.onUpdate();
                long energyToInsert = (cachedItems.size() > 0 && (this.energy % cachedItems.size()) == 0) ? this.energy / cachedItems.size() : this.energy;
                cachedItems.forEach(h ->{
                    long toAdd = Math.min(this.energy, Math.min(energyToInsert, h.right().getCapacity() - h.right().getEnergy()));
                    if (toAdd > 0 && Utils.addEnergy(h.right(), toAdd)){
                        h.left().setTag(h.right().getContainer().getTag());
                        this.energy -= toAdd;
                    }
                });
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        energyHandler.ifPresent(h -> {
            info.add("Amperage In: " + h.availableAmpsInput());
            info.add("Amperage Out: " + h.availableAmpsOutput());
        });
        return info;
    }
}
