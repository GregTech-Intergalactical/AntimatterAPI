package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.COVERNONE;

public class MachineCoverHandler<T extends TileEntityMachine> extends CoverHandler<T> implements IMachineHandler {

    public MachineCoverHandler(T tile) {
        super(tile, tile.getValidCovers());
        Arrays.stream(Ref.DIRS).forEach(d -> {
            Direction facing = getTileFacing();
            Direction newDir = Utils.coverRotateFacing(d, facing);
            //Don't use set(), it calls onPlace which might call into Tesseract.
            CoverStack<T> cover = new CoverStack<>(tile.getMachineType().defaultCover(newDir), tile);
            covers.put(d, cover);
            buildLookup(COVERNONE, cover.getCover(), d);
        });
    }

    public Direction getOutputFacing() {
        return lookupSingle(getTile().getMachineType().getOutputCover().getClass());
    }

    public boolean setOutputFacing(PlayerEntity entity, Direction side) {
        Direction dir = getOutputFacing();
        if (dir == null) return false;
        if (side == dir) return true;
        if (getTileFacing() == side && !getTile().getMachineType().allowsFrontCovers()) return false;
        return moveCover(entity, dir, side);
    }

    @Override
    public boolean set(Direction side, CoverStack<T> old, CoverStack<T> stack) {
        if (getTileFacing() == side && !getTile().getMachineType().allowsFrontCovers()) return false;
        return super.set(side, old, stack);
    }

    @Override
    protected boolean canRemoveCover(ICover cover) {
        return !getTile().getMachineType().getOutputCover().isEqual(cover);
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
        if (!validCovers.contains(replacement.getId())) return false;
        if (side == getOutputFacing()) return false;
        return (get(side).isEmpty() && !(replacement == COVERNONE)) || super.isValid(side, replacement);
    }

    @Override
    public Direction getTileFacing() {
        return getTile().getFacing();
    }

    /**
     * Returns a list of item stacks to be dropped upon machine removal.
     * @return list.
     */
    public List<ItemStack> getDrops() {
        return this.covers.values().stream().filter(t -> !t.getCover().getDroppedStack().isEmpty()).map(t ->
            t.getCover().getDroppedStack()).collect(Collectors.toList());
    }

    /**
     * Checks whether a cover would block capability on this side.
     * @param side side to check
     * @return a boolean whether or not capability was blocked.
     */
    public <U> boolean blocksCapability(Capability<U> capability, Direction side) {
        CoverStack<?> stack = get(side);
        if (stack.isEmpty()) return false;
        return stack.getCover().blocksCapability(stack, capability, side);
    }
}
