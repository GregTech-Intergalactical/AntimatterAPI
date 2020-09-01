package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineInteractHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static muramasa.antimatter.Data.HAMMER;
import static muramasa.antimatter.machine.MachineFlag.CONFIGURABLE;
import static muramasa.antimatter.machine.MachineFlag.ENERGY;

public class TileTransformer extends TileEntityStorage {

    private int amps;

    public TileTransformer(Machine<?> type, int amps) {
        super(type);
        this.amps = amps;
    }

    @Override
    public void onLoad() {
        // Anonymous inherited classes are annoying since you have to rewrite code. probably move the energy handlers to an actual class.
        if (has(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this, 0, 521L * getMachineTier().getVoltage() * 2L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), amps,amps * 4) {

            @Override
            public boolean canOutput(Dir direction) {
                return tile.getFacing().getIndex() != direction.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        });
        if (has(CONFIGURABLE)) interactHandler = Optional.of(new MachineInteractHandler(this) {
            @Override
            public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
                if (type == HAMMER && hand == Hand.MAIN_HAND) {
                    toggleState(MachineState.ACTIVE);
                    return true;
                }
                return super.onInteract(player, hand, side, type);
            }
        });
        super.onLoad();
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Capacity: " + energyHandler.map(EnergyHandler::getCapacity).orElse(0L));
        info.add("Amperage in: " + energyHandler.map(EnergyHandler::getInputAmperage).orElse(0));
        info.add("Amperage out: " + energyHandler.map(EnergyHandler::getOutputAmperage).orElse(0));
        return info;
    }
}