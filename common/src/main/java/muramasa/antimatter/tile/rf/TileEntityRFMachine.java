package muramasa.antimatter.tile.rf;

import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineRFHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.gt.IEnergyHandler;

import java.util.List;

public class TileEntityRFMachine<T extends TileEntityRFMachine<T>>  extends TileEntityMachine<T> {
    public Holder<EnergyContainer, MachineRFHandler<T>> rfHandler = new Holder<>(EnergyContainer.class, dispatch);
    public TileEntityRFMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        if (type.has(MachineFlag.ENERGY)){
            energyHandler.set(() -> null);
            rfHandler.set(() -> new MachineRFHandler<>((T)this, this.getMachineTier().getVoltage() * 100, type.has(MachineFlag.GENERATOR)));
        }
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (isServerSide()){
            rfHandler.ifPresent(MachineRFHandler::init);
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        rfHandler.ifPresent(MachineRFHandler::update);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        rfHandler.ifPresent(MachineRFHandler::onRemove);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (this.getLevel() != null && !this.getLevel().isClientSide) {
            rfHandler.ifPresent(e -> e.onMachineEvent(event, data));
            markDirty();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(Ref.KEY_MACHINE_ENERGY))
            rfHandler.ifPresent(e -> e.deserialize(tag.getCompound(Ref.KEY_MACHINE_ENERGY)));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        rfHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_ENERGY, e.serialize(new CompoundTag())));
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        rfHandler.ifPresent(h -> info.add("RF: " + h.getStoredEnergy() + " / " + h.getMaxCapacity()));
        return info;
    }
}
