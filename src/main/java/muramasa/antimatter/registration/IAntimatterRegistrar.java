package muramasa.antimatter.registration;

import net.minecraftforge.api.distmarker.Dist;

public interface IAntimatterRegistrar extends IAntimatterObject {


    default String getDomain() { return getId(); }

    default boolean isEnabled() {
        return !getId().equals("minecraft");
    }

    void onRegistrationEvent(RegistrationEvent event, Dist side);

}
