package muramasa.gtu.api.capability.impl;

import net.minecraftforge.fluids.FluidTank;

public class GTFluidTank extends FluidTank {

    public GTFluidTank(int capacity, boolean fill, boolean drain) {
        super(capacity);
        setCanFill(fill);
        setCanDrain(drain);
    }
}
