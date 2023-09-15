package muramasa.antimatter.capability;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.EnumMap;
import java.util.Map;

public class FluidHandlerSnapshot implements FluidSnapshot {
    EnumMap<FluidHandler.FluidDirection, Map<Integer, FluidHolder>> map = new EnumMap<>(FluidHandler.FluidDirection.class);
    public FluidHandlerSnapshot(FluidHandler<?> handler){
        if (handler.tanks.containsKey(FluidHandler.FluidDirection.INPUT)){
            map.put(FluidHandler.FluidDirection.INPUT, new Object2ObjectOpenHashMap<>());
            for (int i = 0; i < handler.getInputTanks().getBackingTanks().length; i++) {
                FluidHolder fluidHolder = handler.getInputTanks().getFluidInTank(i).copyHolder();
                if (!fluidHolder.isEmpty()){
                    map.get(FluidHandler.FluidDirection.INPUT).put(i, fluidHolder);
                }
            }
        }
        if (handler.tanks.containsKey(FluidHandler.FluidDirection.OUTPUT)){
            map.put(FluidHandler.FluidDirection.OUTPUT, new Object2ObjectOpenHashMap<>());
            for (int i = 0; i < handler.getOutputTanks().getBackingTanks().length; i++) {
                FluidHolder fluidHolder = handler.getOutputTanks().getFluidInTank(i).copyHolder();
                if (!fluidHolder.isEmpty()){
                    map.get(FluidHandler.FluidDirection.OUTPUT).put(i, fluidHolder);
                }
            }
        }

    }
    @Override
    public void loadSnapshot(FluidContainer container) {
        if (container instanceof FluidHandler<?> handler){
            map.forEach((fluidDirection, integerFluidHolderMap) -> {
                integerFluidHolderMap.forEach((i, fluidHolder) -> {
                    handler.tanks.get(fluidDirection).setFluid(i, fluidHolder);
                });
            });
        }
    }
}
