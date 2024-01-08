package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.rf.IRFNode;

public class CoverDynamo extends BaseCover {

    public CoverDynamo(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public boolean ticks() {
        return false;
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir) {
        return getBasicModel();
    }

    @Override
    public void onPlace() {
        super.onPlace();
        ((BlockEntityMachine<?>) handler.getTile()).invalidateCap(IEnergyHandler.class);
        ((BlockEntityMachine<?>) handler.getTile()).invalidateCap(IRFNode.class);
    }
}
