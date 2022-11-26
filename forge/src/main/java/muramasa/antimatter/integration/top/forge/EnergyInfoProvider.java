package muramasa.antimatter.integration.top.forge;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyInfoProvider implements IProbeInfoProvider {
    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(Ref.ID + ":energy_info");
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level,
                             BlockState blockState, IProbeHitData data) {

        if (blockState.hasBlockEntity()) {
            BlockEntity tile = Utils.getTile(level, data.getPos());

            if (tile == null) return;

            if (tile instanceof TileEntityMachine machine) {
                if(!machine.energyHandler.isPresent()) {
                    return;
                }

                IProbeConfig config = Config.getRealConfig();
                config.setRFMode(0);

                MachineEnergyHandler energyHandler = (MachineEnergyHandler) machine.energyHandler.get();

                long maxCapacity = energyHandler.getCapacity();
                if (maxCapacity == 0) return;

                probeInfo.progress(energyHandler.getEnergy(), maxCapacity, probeInfo.defaultProgressStyle()
                        .suffix(" / " + maxCapacity + " EU")
                        .filledColor(0xFFEEE600)
                        .alternateFilledColor(0xFFEEE600)
                        .borderColor(0xFF555555));

            }

        }

    }
}
