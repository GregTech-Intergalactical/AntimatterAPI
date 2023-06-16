package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.core.Direction;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.rf.IRFNode;

import javax.annotation.Nullable;

public class CoverEnergy extends BaseCover {

    public CoverEnergy(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    // @Override
    // public ResourceLocation getModel(Direction dir, Direction facing) {
    // return getBasicModel();
    // }

    @Override
    public boolean ticks() {
        return false;
    }

    @Override
    public void onPlace() {
        super.onPlace();
        ((TileEntityMachine<?>) handler.getTile()).invalidateCap(IEnergyHandler.class);
        ((TileEntityMachine<?>) handler.getTile()).invalidateCap(IRFNode.class);
    }
}
