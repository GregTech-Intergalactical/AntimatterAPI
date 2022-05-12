package muramasa.antimatter.registration;

public interface IRegistryEntryProvider extends IAntimatterObject {

    void onRegistryBuild(RegistryType registry);
}
