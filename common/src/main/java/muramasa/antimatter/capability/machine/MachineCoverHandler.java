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
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;


public class MachineCoverHandler<T extends TileEntityMachine<T>> extends CoverHandler<T> implements IMachineHandler, Dispatch.Sided<ICoverHandler<?>> {
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

    public boolean setOutputFacing(Player entity, Direction side) {
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
        if (ok && getTile().getLevel() != null) {
            if (!getTile().getLevel().isClientSide) {
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
    public boolean onInteract(@Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
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
    public Optional<ICoverHandler<?>> forSide(Direction side) {
        return Optional.of(this);
    }

    @Override
    public Optional<? extends ICoverHandler<?>> forNullSide() {
        return Optional.of(this);
    }
}
