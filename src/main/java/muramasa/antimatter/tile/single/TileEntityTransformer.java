package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineInteractHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntToLongFunction;

import static muramasa.antimatter.Data.HAMMER;

public class TileEntityTransformer extends TileEntityMachine {

    protected int voltage, amperage;
    protected IntToLongFunction capFunc;

    public TileEntityTransformer(Machine<?> type, int amps) {
        this(type, amps, (v) -> (512L + v * 2L));
    }

    public TileEntityTransformer(Machine<?> type, int amps, IntToLongFunction capFunc) {
        super(type);
        this.voltage = getMachineTier().getVoltage();
        this.amperage = amps;
        this.capFunc = capFunc;
        energyHandler.setup((tile, tag) -> new MachineEnergyHandler<TileEntityMachine>(tile, tag, 0L, capFunc.applyAsLong(voltage), voltage, voltage / 4, amperage, amperage * 4)  {
            @Override
            public boolean canOutput(Dir direction) {
                return isDefaultMachineState() == (tile.getFacing().getIndex() == direction.getIndex());
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        });
        interactHandler.setup((tile, tag) -> new MachineInteractHandler<TileEntityMachine>(tile, tag) {
            @Override
            public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
                if (type == HAMMER && hand == Hand.MAIN_HAND) {
                    toggleMachine();
                    energyHandler.ifPresent(h -> {
                        int temp = h.getOutputAmperage();
                        h.setOutputAmperage(h.getInputAmperage());
                        h.setInputAmperage(temp);
                        temp = h.getOutputVoltage();
                        h.setOutputVoltage(h.getInputVoltage());
                        h.setInputVoltage(temp);
                        h.onReset();
                        player.sendMessage(new StringTextComponent((isDefaultMachineState() ? "Step Down, In: " : "Step Up, In") + h.getInputVoltage() + "V@" + h.getInputAmperage() + "Amp, Out: " + h.getOutputVoltage() + "V@" + h.getOutputAmperage() + "Amp"));
                    });
                    return true;
                }
                return super.onInteract(player, hand, side, type);
            }
        });
    }

    @Override
    public MachineState getDefaultMachineState() {
        return MachineState.ACTIVE;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        energyHandler.ifPresent(h -> {
            info.add("Voltage In: " + h.getInputVoltage());
            info.add("Voltage Out: " + h.getOutputVoltage());
            info.add("Amperage In: " + h.getInputAmperage());
            info.add("Amperage Out: " + h.getOutputAmperage());
        });
        return info;
    }
}