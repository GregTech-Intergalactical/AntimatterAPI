package muramasa.antimatter.blockentity;

import it.unimi.dsi.fastutil.Pair;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.gt.IGTNode;

import java.util.List;

public abstract class BlockEntityStorage<T extends BlockEntityStorage<T>> extends BlockEntityMachine<T> {

    public BlockEntityStorage(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0L, (long) getMachineTier().getVoltage() * itemHandler.map(m -> m.getChargeHandler().getSlots()).orElse(1), getMachineTier().getVoltage(), getMachineTier().getVoltage(), 1,1) {
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
            public long getInputAmperage() {
                if (cachedItems != null && !cachedItems.isEmpty()){
                    return cachedItems.stream().map(Pair::right).mapToLong(IGTNode::getInputAmperage).sum();
                }
                return super.getInputAmperage();
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
    public void onLoad() {
        super.onLoad();
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
