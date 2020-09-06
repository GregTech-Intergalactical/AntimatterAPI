package muramasa.antimatter.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class AntimatterCaps {

    @CapabilityInject(IEnergyHandler.class)
    public static Capability<IEnergyHandler> ENERGY;

    @CapabilityInject(IInteractHandler.class)
    public static Capability<IInteractHandler> INTERACTABLE;

    @CapabilityInject(ICoverHandler.class)
    public static Capability<ICoverHandler> COVERABLE;

    @CapabilityInject(IComponentHandler.class)
    public static Capability<IComponentHandler> COMPONENT;

    public static void register() {

        CapabilityManager.INSTANCE.register(IEnergyHandler.class, new Capability.IStorage<IEnergyHandler>() {
            @Override
            public INBT writeNBT(Capability<IEnergyHandler> capability, IEnergyHandler instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IEnergyHandler> capability, IEnergyHandler instance, Direction side, INBT nbt) {
            }
        }, () -> new EnergyHandler(0, 0, 0, 0, 0, 0));

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
                return null;
            }

            @Override
            public void readNBT(Capability<ICoverHandler> capability, ICoverHandler instance, Direction side, INBT nbt) {

            }
        }, () -> new CoverHandler(null));

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
