package muramasa.gtu.api.registration;

import net.minecraftforge.fml.common.Loader;

public interface IGregTechRegistrar {

    String getId();

    default boolean isEnabled() {
        return Loader.isModLoaded(getId());
    }

    void onRegistrationEvent(RegistrationEvent event);
}
