package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;


public class MachineCoverHandler<T extends TileEntityMachine<T>> extends CoverHandler<T> implements IMachineHandler, Dispatch.Sided<ICoverHandler> {
    public MachineCoverHandler(T tile) {
        super(tile, tile.getValidCovers());
        Arrays.stream(Ref.DIRS).forEach(d -> {
            Direction facing = getTileFacing();
            Direction newDir = Utils.coverRotateFacing(d, facing);
            covers.put(d, tile.getMachineType().defaultCover(newDir).get().get(this, null, d, tile.getMachineType().defaultCover(newDir)));
            buildLookup(ICover.emptyFactory, tile.getMachineType().defaultCover(newDir), d);
        });
    }

    public Direction getOutputFacing() {
        return lookupSingle(getTile().getMachineType().getOutputCover());
    }

    public ICover getOutputCover() {
        return get(lookupSingle(getTile().getMachineType().getOutputCover()));
    }

    public boolean setOutputFacing(PlayerEntity entity, Direction side) {
        Direction dir = getOutputFacing();
        if (dir == null) return false;
        if (side == dir) return false;
        if (getTileFacing() == side && !getTile().getMachineType().allowsFrontCovers()) return false;
        boolean ok = moveCover(entity, dir, side);
        if (ok) {
            getTile().invalidateCaps();
        }
        return ok;
    }

    @Override
    public boolean set(Direction side, ICover old, ICover stack, boolean sync) {
        if (getTileFacing() == side && !getTile().getMachineType().allowsFrontCovers()) return false;
        boolean ok = super.set(side, old, stack, sync);
        if (ok && getTile().getWorld() != null) {
            if (!getTile().getWorld().isRemote) {
                getTile().invalidateCaps(side);
            } else {
                if (coverTexturer != null) getTexturer(side).invalidate();
            }
        }
        return ok;
    }

    @Override
    protected boolean canRemoveCover(ICover cover) {
        return getTile().getMachineType().getOutputCover() != cover.getFactory();
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
        return super.onInteract(player, hand, side, type);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        covers.forEach((s, c) -> c.onMachineEvent(getTile(), event));
    }

    @Override
    public boolean isValid(@Nonnull Direction side, @Nonnull ICover replacement) {
        if (!validCovers.contains(replacement.getLoc())) return false;
        if (side == getOutputFacing()) return false;
        return (get(side).isEmpty() && !replacement.isEmpty()) || super.isValid(side, replacement);
    }

    public Direction getTileFacing() {
        return getTile().getFacing();
    }

    @Override
    public LazyOptional<ICoverHandler> forSide(Direction side) {
        return LazyOptional.of(() -> this);
    }

    @Override
    public LazyOptional<? extends ICoverHandler> forNullSide() {
        return LazyOptional.of(() -> this);
    }

    @Override
    public void refresh() {

    }
}
