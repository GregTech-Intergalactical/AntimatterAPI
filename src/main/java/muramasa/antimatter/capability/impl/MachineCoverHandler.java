package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class MachineCoverHandler extends RotatableCoverHandler implements IMachineHandler {

    protected int outputSide = 3;

    public MachineCoverHandler(TileEntityMachine tile) {
        super(tile, tile.getValidCovers());
        covers = new CoverInstance[]{
            Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_OUTPUT, Data.COVER_EMPTY, Data.COVER_EMPTY
        };
    }

    public Direction getOutputFacing() {
        return Utils.rotateFacingAlt(Ref.DIRECTIONS[outputSide], getTileFacing());
    }

    public boolean setOutputFacing(Direction side) {
        if (onPlace(side, Data.COVEROUTPUT)) {
            if (covers[outputSide].isEqual(Data.COVER_OUTPUT)) covers[outputSide] = Data.COVER_EMPTY;
            outputSide = Utils.rotateFacing(side, getTileFacing()).getIndex();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPlace(Direction side, @Nonnull Cover cover) {
        if (cover.isEqual(Data.COVERNONE) && Utils.rotateFacing(side, getTileFacing()).getIndex() == outputSide) {
            super.onPlace(side, Data.COVERNONE);
            return super.onPlace(side, Data.COVEROUTPUT);
        }
        return super.onPlace(side, cover);
    }

    @Override
    public boolean  onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nonnull AntimatterToolType type) {
        if (type == Data.CROWBAR && getCover(side).isEqual(Data.COVER_OUTPUT)) return false;
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        for (CoverInstance cover : covers) {
            if (cover.isEmpty()) continue;
            cover.onMachineEvent((TileEntityMachine) getTile(), event);
        }
    }

    @Override
    public boolean isValid(@Nonnull Direction side, Cover existing, @Nonnull Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        return (existing.isEmpty() && !replacement.isEmpty()) || super.isValid(side, existing, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return ((TileEntityMachine) getTile()).getFacing();
    }
}
