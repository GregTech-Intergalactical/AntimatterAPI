package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
            boolean anyEmpty = this.covers.values().stream().anyMatch(ICover::isNode);
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
            boolean anyEmpty = this.covers.values().stream().anyMatch(ICover::isNode);
            this.getTile().onCoverUpdate(true, anyEmpty, side, old, stack);
        }
        return ok;
    }

    public void readFromStack(ItemStack stack){
        if (stack.getTag() != null && stack.getTag().contains("covers")){
            CompoundTag nbt = stack.getTag().getCompound("covers");
            byte sides = nbt.getByte(Ref.TAG_MACHINE_COVER_SIDE);
            for (int i = 0; i < Ref.DIRS.length; i++) {
                if ((sides & (1 << i)) > 0) {
                    ICover cover = CoverFactory.readCover(this, Direction.from3DDataValue(i), nbt);
                    buildLookup(covers.get(Ref.DIRS[i]).getFactory(), cover.getFactory(), Ref.DIRS[i]);
                    covers.put(Ref.DIRS[i], cover);
                }
            }

        }
    }

    public void writeToStack(ItemStack machine){
        CompoundTag tag = new CompoundTag();
        byte[] sides = new byte[1];
        covers.forEach((s, cover) -> {
            if (!cover.isEmpty()) { // Don't store EMPTY covers unnecessarily
                sides[0] |= (1 << s.get3DDataValue());
                CoverFactory.writeCover(tag, cover, cover.side());
            }
        });
        if (!tag.isEmpty()){
            tag.putByte(Ref.TAG_MACHINE_COVER_SIDE, sides[0]);
            machine.getOrCreateTag().put("covers", tag);
        }
    }
}
