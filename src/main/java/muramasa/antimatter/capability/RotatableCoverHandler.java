package muramasa.antimatter.capability;

import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
//TODO: Delete
public class RotatableCoverHandler<T extends TileEntity> extends CoverHandler<T> {

    public RotatableCoverHandler(T tile, Cover... covers) {
        super(tile, covers);
    }

    @Override
    public void onUpdate() {
        covers.forEach((s, c) -> c.getCover().onUpdate(c, Utils.coverRotateFacing(getTileFacing(),s)));
    }

    @Override
    public boolean set(Direction side, @Nonnull Cover newCover) {
        return super.set(Utils.coverRotateFacing(side,getTileFacing()), newCover);
    }

    @Override
    public CoverInstance<T> get(Direction side) {
        return super.get(Utils.coverRotateFacing(side,getTileFacing()));
    }
}
