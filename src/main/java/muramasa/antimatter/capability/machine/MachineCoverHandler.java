package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class MachineCoverHandler<T extends TileEntityMachine> extends RotatableCoverHandler<T> implements IMachineHandler, ICapabilityHandler {

    protected Direction output = Direction.SOUTH;

    public MachineCoverHandler(T tile, CompoundNBT tag) {
        super(tile, tile.getValidCovers());
        covers.put(getTile().getFacing().getOpposite(), new CoverInstance<>(Data.COVEROUTPUT, getTile()));
        if (tag != null) deserialize(tag);
    }

    public Direction getOutputFacing() {
        return Utils.rotateFacingAlt(output, getTileFacing());
    }

    public boolean setOutputFacing(Direction side) {
        if (set(side, Data.COVEROUTPUT)) {
            if (covers.get(output).isEqual(Data.COVEROUTPUT)) covers.put(output, new CoverInstance<>(Data.COVERNONE));
            output = Utils.rotateFacing(side, getTileFacing());
            return true;
        }
        return false;
    }

    @Override
    public boolean set(Direction side, @Nonnull Cover newCover) {
//        if (newCover.isEqual(Data.COVERNONE) && Utils.rotateFacing(side, getTileFacing()) == output) {
//            super.set(side, Data.COVERNONE);
//            return super.set(side, Data.COVEROUTPUT);
//        }
        return super.set(side, newCover);
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

    @Override
    public CapabilityType getCapabilityType() {
        return CapabilityType.COVERABLE;
    }
}
