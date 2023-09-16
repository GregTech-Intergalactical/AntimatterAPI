package muramasa.antimatter.integration.top;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

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

            if (tile instanceof BlockEntityMachine machine) {
                if(!machine.energyHandler.isPresent()) {
                    return;
                }

                List<IElement> elements = probeInfo.getElements();
                IElement rf = null;
                for (IElement e : elements) {
                    if (e instanceof ElementProgress progress) {
                        IProgressStyle style = progress.getStyle();
                        if (style.getSuffix().contains("FE")) {
                            rf = e;
                            break;
                        }
                    }
                }
                if (rf != null){
                    elements.remove(rf);
                }


                MachineEnergyHandler energyHandler = (MachineEnergyHandler) machine.energyHandler.get();

                long maxCapacity = energyHandler.getCapacity();
                if (maxCapacity == 0) return;

                IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                //horizontalPane.text("Energy: ");
                NumberFormat format = player.isCrouching() ? NumberFormat.FULL : NumberFormat.COMPACT;
                horizontalPane.progress(energyHandler.getEnergy(), maxCapacity, probeInfo.defaultProgressStyle()
                        .suffix(new TextComponent(" / ").append(ElementProgress.format(maxCapacity, format, new TextComponent(" EU"))))
                        .filledColor(0xFFEEE600)
                        .alternateFilledColor(0xFFEEE600)
                        .borderColor(0xFF555555).numberFormat(format));

            }

        }

    }
}
