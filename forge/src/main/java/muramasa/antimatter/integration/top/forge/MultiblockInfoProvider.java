package muramasa.antimatter.integration.top.forge;

import mcjty.theoneprobe.api.*;
import muramasa.antimatter.Ref;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockInfoProvider implements IProbeInfoProvider {
    public ResourceLocation getID() {
        return new ResourceLocation(Ref.ID + ":multiblock_info");
    }

    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level,
                             BlockState blockState, IProbeHitData data) {

        if (blockState.hasBlockEntity()) {
            BlockEntity tile = Utils.getTile(level, data.getPos());

            if (tile instanceof TileEntityBasicMultiMachine machine) {

                IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                if (machine.isStructureValid()) {
                    horizontalPane.text(ChatFormatting.GREEN + "Structure Formed");
                } else {
                    horizontalPane.text(ChatFormatting.RED + "Structure Incomplete");
                }

            }
        }

    }
}
