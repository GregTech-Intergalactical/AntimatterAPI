package muramasa.antimatter.registration;

import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IAntimatterObject {

    default String getDomain() {
        if (this instanceof IForgeRegistryEntry) return ((IForgeRegistryEntry<?>) this).getRegistryName().getNamespace();
        return "null";
    }

    default String getId() {
        if (this instanceof IForgeRegistryEntry) return ((IForgeRegistryEntry) this).getRegistryName().getPath();
        return "null";
    }
}
