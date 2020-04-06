package muramasa.antimatter.capability;

import muramasa.antimatter.capability.impl.ComponentHandler;
import muramasa.antimatter.capability.impl.CoverHandler;
import muramasa.antimatter.capability.impl.MachineConfigHandler;
import muramasa.antimatter.capability.impl.MachineEnergyHandler;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class AntimatterCaps {

    @CapabilityInject(IEnergyHandler.class)
    public static Capability<IEnergyHandler> ENERGY;

    @CapabilityInject(IConfigHandler.class)
    public static Capability<IConfigHandler> CONFIGURABLE;

    @CapabilityInject(ICoverHandler.class)
    public static Capability<ICoverHandler> COVERABLE;

    @CapabilityInject(IComponentHandler.class)
    public static Capability<IComponentHandler> COMPONENT;

    public static void register() {

        CapabilityManager.INSTANCE.register(IEnergyHandler.class, new Capability.IStorage<IEnergyHandler>() {
            @Override
            public INBT writeNBT(Capability<IEnergyHandler> capability, IEnergyHandler instance, Direction side) {
                return LongNBT.valueOf(instance.getPower());
            }

            @Override
            public void readNBT(Capability<IEnergyHandler> capability, IEnergyHandler instance, Direction side, INBT nbt) {
                if (!(instance instanceof MachineEnergyHandler)) {
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                }
                instance.insert(((LongNBT)nbt).getLong(), false);
            }
        }, () -> new MachineEnergyHandler(null, false));

        CapabilityManager.INSTANCE.register(IConfigHandler.class, new Capability.IStorage<IConfigHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IConfigHandler> capability, IConfigHandler instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IConfigHandler> capability, IConfigHandler instance, Direction side, INBT nbt) {

            }
        }, () -> new MachineConfigHandler(null));

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
