package muramasa.antimatter.cover;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;

public class CoverDebug extends BaseCover {

    public CoverDebug(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public boolean onTransfer(Object object, boolean inputSide, boolean simulate) {
        if (this.handler.getTile().getLevel().isClientSide) return false;
        if (!simulate) {
            String fmt = "";
            if (object instanceof FluidHolder fluidHolder) {
                fmt = String.format("Fluid: %s, amount: %d", FluidPlatformUtils.INSTANCE.getFluidId(fluidHolder.getFluid()), (fluidHolder.getFluidAmount()));
            } else {
                fmt = object.toString();
            }
            Antimatter.LOGGER.info(String.format("Transfer type: %s, data: %s, position: %s, side: %s", object.getClass().getSimpleName(), fmt, this.handler.getTile().getBlockPos().toString(), side));
        }
        return false;
    }

    public boolean ticks() {
        return true;
    }

}
