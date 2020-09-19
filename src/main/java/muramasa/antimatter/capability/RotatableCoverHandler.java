package muramasa.antimatter.capability;

import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.Map;

public class RotatableCoverHandler<T extends TileEntity> extends CoverHandler<T> {

    public RotatableCoverHandler(T tile, Cover... covers) {
        super(tile, covers);
    }

    @Override
    public void onUpdate() {
        for (Map.Entry<Direction, CoverInstance<T>> e : covers.entrySet()) {
            e.getValue().onUpdate(Utils.rotateFacingAlt(e.getKey(), getTileFacing()));
        }
    }

    @Override
    public boolean set(Direction side, @Nonnull Cover newCover) {
        return super.set(Utils.rotateFacing(side, getTileFacing()), newCover);
    }

    @Override
    public CoverInstance<T> get(Direction side) {
        return super.get(Utils.rotateFacing(side, getTileFacing()));
    }
}
