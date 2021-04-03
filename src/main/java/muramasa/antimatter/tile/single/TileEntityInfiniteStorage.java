package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.IRefreshable;
import tesseract.util.Dir;

import java.util.List;

public class TileEntityInfiniteStorage extends TileEntityMachine {

    @Override
    public boolean setFacing(Direction side) {
        boolean ok = super.setFacing(side);
        if (ok) {
            energyHandler.ifPresent(IRefreshable::refreshNet);
        }
        return ok;
    }

    public TileEntityInfiniteStorage(Machine<?> type, int maxAmps) {
        super(type);
        this.energyHandler = LazyOptional.of(() -> new MachineEnergyHandler<TileEntityInfiniteStorage>(this, Long.MAX_VALUE, Long.MAX_VALUE, 0, getMachineTier().getVoltage(), 0, 1) {
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
        // TODO
        /*
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
         */


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