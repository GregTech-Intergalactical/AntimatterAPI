package muramasa.antimatter.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class AntimatterCaps {

    @CapabilityInject(IInteractHandler.class)
    public static final Capability<IInteractHandler> INTERACTABLE_HANDLER_CAPABILITY;

    @CapabilityInject(ICoverHandler.class)
    public static final Capability<ICoverHandler> COVERABLE_HANDLER_CAPABILITY;

    @CapabilityInject(IComponentHandler.class)
    public static final Capability<IComponentHandler> COMPONENT_HANDLER_CAPABILITY;

    /** Dummy cap **/
    public static final Capability<?> RECIPE_HANDLER_CAPABILITY;

    static {
        INTERACTABLE_HANDLER_CAPABILITY = null; // Fixme: REMOVE
        COVERABLE_HANDLER_CAPABILITY = null;
        COMPONENT_HANDLER_CAPABILITY = null; // Fixme: Optimize
        RECIPE_HANDLER_CAPABILITY = null; // Fixme: REMOVE
    }

    public static <T extends ICapabilityProvider> LazyOptional<EnergyHandler> getCustomEnergyHandler(T type) {
        return type.getCapability(CapabilityEnergy.ENERGY).filter(IEnergyHandler.class::isInstance).cast();
    }

    public static void register() {

        CapabilityManager.INSTANCE.register(IInteractHandler.class, new Capability.IStorage<IInteractHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IInteractHandler> capability, IInteractHandler instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IInteractHandler> capability, IInteractHandler instance, Direction side, INBT nbt) {

            }
        }, () -> new InteractHandler(null));

        CapabilityManager.INSTANCE.register(ICoverHandler.class, new Capability.IStorage<ICoverHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ICoverHandler> capability, ICoverHandler instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ICoverHandler> capability, ICoverHandler instance, Direction side, INBT nbt) {
                instance.deserializeNBT(nbt);
            }
        }, () -> new CoverHandler<>(null));

        CapabilityManager.INSTANCE.register(IComponentHandler.class, new Capability.IStorage<IComponentHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IComponentHandler> capability, IComponentHandler instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IComponentHandler> capability, IComponentHandler instance, Direction side, INBT nbt) {

            }
        }, () -> new ComponentHandler("null", null));
    }
}
