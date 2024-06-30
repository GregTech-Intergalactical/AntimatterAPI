package muramasa.antimatter.blockentity.single;

import it.unimi.dsi.fastutil.Pair;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.blockentity.BlockEntityStorage;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.gt.IGTNode;

import java.util.List;


public class BlockEntityBatteryBuffer<T extends BlockEntityBatteryBuffer<T>> extends BlockEntityMachine<T> {

    public BlockEntityBatteryBuffer(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0L, (long) getMachineTier().getVoltage() * itemHandler.map(m -> m.getChargeHandler().getSlots()).orElse(1), getMachineTier().getVoltage(), getMachineTier().getVoltage(), 1,0) {
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
                if (this.energy > 0 && this.getEnergy() > this.voltageOut * 2 && !cachedItems.isEmpty()){
                    long energyToInsert = this.energy % cachedItems.size() == 0 ? this.energy / cachedItems.size() : this.energy;
                    cachedItems.forEach(h ->{
                        long toAdd = Math.min(this.energy, Math.min(energyToInsert, h.right().getCapacity() - h.right().getEnergy()));
                        if (toAdd > 0 && Utils.addEnergy(h.right(), toAdd)){
                            h.left().setTag(h.right().getContainer().getTag());
                            this.energy -= toAdd;
                        }
                    });
                }

                if (this.energy < this.voltageOut && this.getBatteryEnergy() < this.voltageOut){
                    cachedItems.forEach(h ->{
                        long toRemove = Math.min(this.capacty - this.energy, h.right().getEnergy());
                        if (toRemove > 0 && Utils.removeEnergy(h.right(), toRemove)){
                            h.left().setTag(h.right().getContainer().getTag());
                            this.energy += toRemove;
                        }
                    });
                }

            }

            @Override
            public long getInputAmperage() {
                if (cachedItems != null && !cachedItems.isEmpty()){
                    return cachedItems.stream().map(Pair::right).mapToLong(IGTNode::getInputAmperage).sum();
                }
                return 0;
            }

            @Override
            public long getOutputAmperage() {
                if (cachedItems != null && !cachedItems.isEmpty()){
                    return cachedItems.stream().map(Pair::right).mapToLong(IGTNode::getOutputAmperage).sum();
                }
                return super.getOutputAmperage();
            }
        });
    }

    @Override
    public List<String> getInfo(boolean simple) {
        List<String> info = super.getInfo(simple);
        energyHandler.ifPresent(h -> {
            info.add("Amperage In: " + h.availableAmpsInput(this.getMaxInputVoltage()));
            info.add("Amperage Out: " + h.availableAmpsOutput());
        });
        return info;
    }
}