package muramasa.antimatter.registration;

import net.minecraftforge.fml.ModList;

public interface IGregTechRegistrar {

    String getId();

    default boolean isEnabled() {
        return ModList.get().isLoaded(getId());
    }

    void onRegistrationEvent(RegistrationEvent event);
}
