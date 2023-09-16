package muramasa.antimatter.cover;

import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.rf.IRFNode;

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
        ((BlockEntityMachine<?>) handler.getTile()).invalidateCap(IEnergyHandler.class);
        ((BlockEntityMachine<?>) handler.getTile()).invalidateCap(IRFNode.class);
    }
}
