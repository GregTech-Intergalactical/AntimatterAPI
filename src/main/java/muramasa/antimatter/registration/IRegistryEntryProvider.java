package muramasa.antimatter.registration;

import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public interface IRegistryEntryProvider extends IAntimatterObject {

    void onRegistryBuild(String currentDomain, @Nullable IForgeRegistry<?> registry);

}
