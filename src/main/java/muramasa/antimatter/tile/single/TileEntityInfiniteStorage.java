package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;

import java.util.List;

public class TileEntityInfiniteStorage<T extends TileEntityInfiniteStorage<T>> extends TileEntityMachine<T> {

    public TileEntityInfiniteStorage(Machine<?> type, int maxAmps) {
        super(type);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T)this, Long.MAX_VALUE, Long.MAX_VALUE, 0, getMachineTier().getVoltage(), 0, 1) {
            @Override
            public long extract(long maxExtract, boolean simulate) {
                if (simulate && !getState().extract(true, 1, maxExtract)) {
                    return 0;
                }
                if (!simulate) {
                    getState().extract(false, 1, maxExtract);
                }
                return maxExtract;
            }

            @Override
            public boolean canOutput(Direction direction) {
                return tile.getFacing() == direction;
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