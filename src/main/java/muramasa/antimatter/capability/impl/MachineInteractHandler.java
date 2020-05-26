package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static muramasa.antimatter.Data.ELECTRIC_WRENCH;
import static muramasa.antimatter.Data.WRENCH;
import static muramasa.antimatter.Data.HAMMER;

public class MachineInteractHandler extends InteractHandler {

    public MachineInteractHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nonnull Direction parsedSide, AntimatterToolType type) {
        TileEntityMachine tile = (TileEntityMachine) getTile();
        //TODO: this is lefti n BlockMachine.java for higher priority
        /*
        LazyOptional<ICoverHandler> coverable = tile.getCapability(AntimatterCaps.COVERABLE, side);
        LazyOptional<Object> consume = coverable.map(i -> {
            boolean ok = i.onInteract(player,hand, parsedSide,Utils.getToolType(player));
            return ok ? ok : null;
        });
        if (consume.isPresent()) {
            return true;
        }*/
        if (type == WRENCH || type == ELECTRIC_WRENCH) {
            return player.isCrouching() ? tile.setFacing(side) : tile.setOutputFacing(side);
        } else if (type == HAMMER) {
            tile.toggleMachine();
            player.sendMessage(new StringTextComponent("Machine was " + (tile.getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
            return true;
        }
        return false;
    }
}
