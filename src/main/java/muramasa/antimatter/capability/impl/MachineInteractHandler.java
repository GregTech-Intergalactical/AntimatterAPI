package muramasa.antimatter.capability.impl;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

import static muramasa.antimatter.Data.ELECTRIC_WRENCH;
import static muramasa.antimatter.Data.WRENCH;
import static muramasa.antimatter.Data.HAMMER;

public class MachineInteractHandler extends InteractHandler {

    public MachineInteractHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, AntimatterToolType type) {
        TileEntityMachine tile = (TileEntityMachine) getTile();
        if (type == WRENCH || type == ELECTRIC_WRENCH) {
            return player.isCrouching() ? tile.setFacing(side) : tile.coverHandler.map(h -> h.setOutputFacing(side)).orElse(false);
        } else if (type == HAMMER) {
            tile.toggleMachine();
            player.sendMessage(new StringTextComponent("Machine was " + (tile.getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
            return true;
        }
        return false;
    }
}
