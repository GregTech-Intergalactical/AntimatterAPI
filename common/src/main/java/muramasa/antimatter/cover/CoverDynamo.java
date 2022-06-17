package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.core.Direction;
import tesseract.api.TesseractCaps;

import javax.annotation.Nullable;

public class CoverDynamo extends BaseCover {

    public CoverDynamo(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public boolean ticks() {
        return false;
    }

    @Override
    public void onPlace() {
        super.onPlace();
        ((TileEntityMachine<?>) handler.getTile()).invalidateCap(TesseractCaps.getENERGY_HANDLER_CAPABILITY());
    }
}