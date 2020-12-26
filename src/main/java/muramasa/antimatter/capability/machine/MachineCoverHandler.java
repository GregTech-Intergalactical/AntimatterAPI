package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class MachineCoverHandler<T extends TileEntityMachine> extends CoverHandler<T> implements IMachineHandler {

    protected Direction output;

    public MachineCoverHandler(T tile) {
        super(tile, tile.getValidCovers());
        // if (tag != null) deserialize(tag);
        covers.put(getTileFacing().getOpposite(), new CoverStack<>(Data.COVEROUTPUT, getTile()));
        output = getTileFacing().getOpposite();
    }

    public Direction getOutputFacing() {
        return output;
    }

    public boolean setOutputFacing(Direction side) {
        if (side == output) return true;
        if (set(side, Data.COVEROUTPUT)) {
            if (covers.get(output).isEqual(Data.COVEROUTPUT)) set(output, Data.COVERNONE);
            output = side;
            return true;
        }
        return false;
    }

    @Override
    public boolean set(Direction side, @Nonnull Cover newCover) {
        if (getTileFacing() == side) return false;

        boolean ok = super.set(side, newCover);
        if (ok) {
            getTile().sidedSync(true);
        }
        return ok;
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nonnull AntimatterToolType type) {
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        covers.forEach((s, c) -> c.onMachineEvent(getTile(), event));
    }

    @Override
    public boolean isValid(@Nonnull Direction side, @Nonnull Cover replacement) {
        if (!validCovers.contains(replacement.getId())) return false;
        if (Utils.rotateFacing(side, getTileFacing()) == output) return false;
        return (get(side).isEmpty() && !replacement.isEmpty()) || super.isValid(side, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return getTile().getFacing();
    }

}
