package muramasa.antimatter.registration;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;

public interface IRegistryEntryProvider extends IAntimatterObject {

    Collection<IForgeRegistryEntry<?>> buildRegistryEntries(String domain, IForgeRegistry<?> registry);
}
