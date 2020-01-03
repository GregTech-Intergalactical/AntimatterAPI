package muramasa.antimatter.capability.impl;

import muramasa.gtu.Ref;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

public class RotatableCoverHandler extends CoverHandler {

    public RotatableCoverHandler(TileEntity tile, Cover... covers) {
        super(tile, covers);
    }

    @Override
    public void update() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(getTile(), Utils.rotateFacingAlt(Ref.DIRECTIONS[i], getTileFacing()));
        }
    }

    @Override
    public boolean set(Direction side, Cover cover) {
        return super.set(Utils.rotateFacing(side, getTileFacing()), cover);
    }

    @Override
    public Cover get(Direction side) {
        return super.get(Utils.rotateFacing(side, getTileFacing()));
    }
}
