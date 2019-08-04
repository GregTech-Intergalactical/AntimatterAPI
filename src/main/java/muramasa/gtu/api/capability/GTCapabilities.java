package muramasa.gtu.api.capability;

import muramasa.gtu.api.capability.impl.ComponentHandler;
import muramasa.gtu.api.capability.impl.CoverHandler;
import muramasa.gtu.api.capability.impl.MachineConfigHandler;
import muramasa.gtu.api.capability.impl.MachineEnergyHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class GTCapabilities {

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
            public NBTBase writeNBT(Capability<IEnergyHandler> capability, IEnergyHandler instance, EnumFacing side) {
                return new NBTTagLong(instance.getPower());
            }

            @Override
            public void readNBT(Capability<IEnergyHandler> capability, IEnergyHandler instance, EnumFacing side, NBTBase nbt) {
                if (!(instance instanceof MachineEnergyHandler)) {
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                }
                instance.insert(((NBTTagLong)nbt).getLong(), false);
            }
        }, () -> new MachineEnergyHandler(null));

        CapabilityManager.INSTANCE.register(IConfigHandler.class, new Capability.IStorage<IConfigHandler>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IConfigHandler> capability, IConfigHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IConfigHandler> capability, IConfigHandler instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new MachineConfigHandler(null));

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

        CapabilityManager.INSTANCE.register(IComponentHandler.class, new Capability.IStorage<IComponentHandler>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IComponentHandler> capability, IComponentHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IComponentHandler> capability, IComponentHandler instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new ComponentHandler("null", null));
    }
}
