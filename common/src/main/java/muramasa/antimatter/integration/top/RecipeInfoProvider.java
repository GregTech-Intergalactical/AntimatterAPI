package muramasa.antimatter.integration.top;

import mcjty.theoneprobe.api.*;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RecipeInfoProvider implements IProbeInfoProvider {
    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(Ref.ID + ":recipe_info");
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level,
                             BlockState blockState, IProbeHitData data) {

        if (blockState.hasBlockEntity()) {
            BlockEntity tile = Utils.getTile(level, data.getPos());

            if (tile instanceof BlockEntityMachine machine) {
                if(!machine.recipeHandler.isPresent()) {
                    return;
                }

                MachineRecipeHandler recipeHandler = (MachineRecipeHandler) machine.recipeHandler.get();

                int currentProgress = recipeHandler.getCurrentProgress();
                int maxProgress = recipeHandler.getMaxProgress();
                String text;

                if (maxProgress < 20) {
                    text = " / " + maxProgress + " t";
                } else {
                    // Display progress as seconds if it's greater 1 second
                    currentProgress = Math.round(currentProgress / 20.0F);
                    maxProgress = Math.round(maxProgress / 20.0F);
                    text = " / " + maxProgress + " s";
                }

                if(recipeHandler.getMaxProgress() > 0 && machine.getMachineState() == MachineState.ACTIVE) {
                    IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                    horizontalPane.text("Progress: ");
                    horizontalPane.progress(currentProgress, maxProgress, probeInfo.defaultProgressStyle()
                            .suffix(text)
                            .filledColor(0xFF4CBB17)
                            .alternateFilledColor(0xFF4CBB17)
                            .borderColor(0xFF555555));
                }

            }

        }

    }
}
