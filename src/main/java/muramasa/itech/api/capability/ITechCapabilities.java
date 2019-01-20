package muramasa.itech.api.capability;

import muramasa.itech.api.capability.implementations.MachineConfigHandler;
import muramasa.itech.api.capability.implementations.MachineEnergyHandler;
import muramasa.itech.api.capability.implementations.MachineCoverHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class ITechCapabilities {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    @CapabilityInject(IConfigurable.class)
    public static Capability<IConfigurable> CONFIGURABLE = null;

    @CapabilityInject(ICoverable.class)
    public static Capability<ICoverable> COVERABLE = null;

//    @CapabilityInject(IComponent.class)
//    public static Capability<IComponent> COMPONENT = null;

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
        }, () -> new MachineEnergyHandler(1000));

        CapabilityManager.INSTANCE.register(IConfigurable.class, new Capability.IStorage<IConfigurable>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IConfigurable> capability, IConfigurable instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IConfigurable> capability, IConfigurable instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new MachineConfigHandler(null));

        CapabilityManager.INSTANCE.register(ICoverable.class, new Capability.IStorage<ICoverable>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ICoverable> capability, ICoverable instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<ICoverable> capability, ICoverable instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new MachineCoverHandler(null));

//        CapabilityManager.INSTANCE.register(IComponent.class, new Capability.IStorage<IComponent>() {
//            @Nullable
//            @Override
//            public NBTBase writeNBT(Capability<IComponent> capability, IComponent instance, EnumFacing side) {
//                return null;
//            }
//
//            @Override
//            public void readNBT(Capability<IComponent> capability, IComponent instance, EnumFacing side, NBTBase nbt) {
//
//            }
//        }, Component::new);
    }
}
