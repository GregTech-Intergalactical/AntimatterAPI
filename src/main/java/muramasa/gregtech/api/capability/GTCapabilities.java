package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.capability.impl.ConfigHandler;
import muramasa.gregtech.api.capability.impl.CoverHandler;
import muramasa.gregtech.api.capability.impl.MachineEnergyHandler;
import muramasa.gregtech.api.machines.Tier;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class GTCapabilities {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY;

    @CapabilityInject(IConfigHandler.class)
    public static Capability<IConfigHandler> CONFIGURABLE;

    @CapabilityInject(ICoverHandler.class)
    public static Capability<ICoverHandler> COVERABLE;

    @CapabilityInject(IComponent.class)
    public static Capability<IComponent> COMPONENT;

    public static void register() {

        CapabilityManager.INSTANCE.register(IEnergyStorage.class, new Capability.IStorage<IEnergyStorage>() {
            @Override
            public NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side) {
                return new NBTTagLong(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side, NBTBase nbt) {
                if (!(instance instanceof MachineEnergyHandler)) {
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                }
                ((MachineEnergyHandler)instance).energy = ((NBTTagLong)nbt).getLong();
            }
        }, () -> new MachineEnergyHandler(Tier.LV));

        CapabilityManager.INSTANCE.register(IConfigHandler.class, new Capability.IStorage<IConfigHandler>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IConfigHandler> capability, IConfigHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IConfigHandler> capability, IConfigHandler instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new ConfigHandler(null));

        CapabilityManager.INSTANCE.register(ICoverHandler.class, new Capability.IStorage<ICoverHandler>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ICoverHandler> capability, ICoverHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<ICoverHandler> capability, ICoverHandler instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new CoverHandler(null));

        CapabilityManager.INSTANCE.register(IComponent.class, new Capability.IStorage<IComponent>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IComponent> capability, IComponent instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IComponent> capability, IComponent instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new ComponentHandler("null", null));
    }
}
