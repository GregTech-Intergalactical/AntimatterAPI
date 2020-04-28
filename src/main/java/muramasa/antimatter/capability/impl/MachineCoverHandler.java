package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class MachineCoverHandler extends RotatableCoverHandler implements IMachineHandler {

    protected int outputSide = 3;

    public MachineCoverHandler(TileEntityMachine tile) {
        super(tile, tile.getValidCovers());
        covers = new Cover[] {
            Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_OUTPUT, Data.COVER_NONE, Data.COVER_NONE
        };
    }

    public Direction getOutputFacing() {
        return Utils.rotateFacingAlt(Ref.DIRECTIONS[outputSide], getTileFacing());
    }

    public boolean setOutputFacing(Direction side) {
        if (onPlace(side, Data.COVER_OUTPUT)) {
            if (covers[outputSide].isEqual(Data.COVER_OUTPUT)) covers[outputSide] = Data.COVER_NONE;
            outputSide = Utils.rotateFacing(side, getTileFacing()).getIndex();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPlace(Direction side, Cover cover) {
        if (cover.isEqual(Data.COVER_NONE) && Utils.rotateFacing(side, getTileFacing()).getIndex() == outputSide) {
            super.onPlace(side, Data.COVER_NONE);
            return super.onPlace(side, Data.COVER_OUTPUT);
        }
        return super.onPlace(side, cover);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type) {
        if (type == Data.CROWBAR && getCover(side).isEqual(Data.COVER_OUTPUT)) return false;
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onMachineEvent((TileEntityMachine) getTile(), event);
        }
    }

    @Override
    public boolean isValid(Direction side, Cover existing, Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        return (existing.isEqual(Data.COVER_OUTPUT) && !replacement.isEqual(Data.COVER_NONE)) || super.isValid(side, existing, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return ((TileEntityMachine) getTile()).getFacing();
    }
}
