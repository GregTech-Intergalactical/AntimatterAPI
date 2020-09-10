package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineInteractHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import tesseract.util.Dir;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.Data.ELECTRIC_SCREWDRIVER;
import static muramasa.antimatter.Data.SCREWDRIVER;

public class TileEntityInfiniteStorage extends TileEntityMachine {

    public TileEntityInfiniteStorage(Machine<?> type, int maxAmps) {
        super(type);
        int amperage = maxAmps + 1;
        energyHandler.setup((tile, tag) -> new MachineEnergyHandler<TileEntityMachine>(tile, tag, Long.MAX_VALUE, Long.MAX_VALUE, 0, tile.getMachineTier().getVoltage(), 0, 1) {
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
        interactHandler.setup((tile, tag) -> new MachineInteractHandler<TileEntityMachine>(tile, tag) {
            @Override
            public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
                if ((type == SCREWDRIVER || type == ELECTRIC_SCREWDRIVER) && hand == Hand.MAIN_HAND) {
                    energyHandler.ifPresent(h -> {
                        int amps = h.getOutputAmperage();
                        amps = (amps + 1) % amperage;
                        h.setOutputAmperage(amps);
                        // TODO: Replace by new TranslationTextComponent()
                        player.sendMessage(new StringTextComponent(h.getOutputVoltage() + "V@" + h.getOutputAmperage() + "Amp"));
                    });
                    return true;
                }
                return super.onInteract(player, hand, side, type);
            }
        });
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        energyHandler.ifPresent(h -> {
            info.add("Amperage Out: " + h.getOutputAmperage());
        });
        return info;
    }
}