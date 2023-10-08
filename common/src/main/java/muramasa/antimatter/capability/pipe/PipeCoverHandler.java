package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PipeCoverHandler<T extends BlockEntityPipe<?>> extends CoverHandler<T> {

    public PipeCoverHandler(T tile) {
        super(tile, tile.getValidCovers());
        // if (tag != null) deserialize(tag);
    }

    /*@Override
    public boolean set(Direction side, ICover old, ICover stack, boolean sync) {
        boolean ok = super.set(side, old, stack, sync);
        if (ok && sync) {
            boolean anyEmpty = this.covers.values().stream().anyMatch(t -> !t.isEmpty());
            this.getTile().onCoverUpdate(!old.isEmpty() && stack.isEmpty(), anyEmpty, side, old, stack);
        }
        return ok;
    }*/

    @Override
    public boolean placeCover(Player player, Direction side, ItemStack stack, ICover cover) {
        ICover old = get(side);
        boolean ok = super.placeCover(player, side, stack, cover);
        if (ok){
            boolean anyEmpty = this.covers.values().stream().anyMatch(t -> !t.isEmpty());
            this.getTile().onCoverUpdate(false, anyEmpty, side, old, cover);
        }
        return ok;
    }

    @Override
    public boolean removeCover(Player player, Direction side, boolean onlyRemove) {
        ICover old = get(side);
        boolean ok = super.removeCover(player, side, onlyRemove);
        if (ok){
            ICover stack = ICover.empty;
            boolean anyEmpty = this.covers.values().stream().anyMatch(t -> !t.isEmpty());
            this.getTile().onCoverUpdate(true, anyEmpty, side, old, stack);
        }
        return ok;
    }
}
