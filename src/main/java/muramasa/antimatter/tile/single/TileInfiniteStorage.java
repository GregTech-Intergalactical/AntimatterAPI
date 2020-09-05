package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineInteractHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static muramasa.antimatter.Data.ELECTRIC_SCREWDRIVER;
import static muramasa.antimatter.Data.SCREWDRIVER;
import static muramasa.antimatter.machine.MachineFlag.CONFIGURABLE;
import static muramasa.antimatter.machine.MachineFlag.ENERGY;

public class TileInfiniteStorage extends TileEntityStorage {

    private int maxAmps;

    public TileInfiniteStorage(Machine<?> type, int maxAmps) {
        super(type);
        this.maxAmps = maxAmps + 1;
    }

    @Override
    public void onFirstTick() {
        // Anonymous inherited classes are annoying since you have to rewrite code. probably move the energy handlers to an actual class.
        if (has(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this, Long.MAX_VALUE, Long.MAX_VALUE, 0, getMachineTier().getVoltage(), 0, 1) {
            @Override
            public long extract(long maxExtract, boolean simulate) {
                return maxExtract;
            }

            @Override
            public boolean canOutput(Dir direction) {
                return tile.getFacing().getIndex() == direction.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        });
        if (has(CONFIGURABLE)) interactHandler = Optional.of(new MachineInteractHandler(this) {
            @Override
            public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
                if ((type == SCREWDRIVER || type == ELECTRIC_SCREWDRIVER) && hand == Hand.MAIN_HAND) {
                    energyHandler.ifPresent(handler -> {
                        int amps = handler.getOutputAmperage();
                        amps = (amps + 1) % maxAmps;
                        handler.setOutputAmperage(amps);
                        player.sendMessage(new StringTextComponent(handler.getOutputVoltage() + "V@" + handler.getOutputAmperage() + "Amp"));
                    });
                    return true;
                }
                return super.onInteract(player, hand, side, type);
            }
        });
        super.onFirstTick();
    }
}