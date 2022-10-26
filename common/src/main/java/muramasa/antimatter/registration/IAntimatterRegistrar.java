package muramasa.antimatter.registration;

import muramasa.antimatter.event.MaterialEvent;

public interface IAntimatterRegistrar extends IAntimatterObject, IAntimatterRegistrarInitializer {

    default String getDomain() {
        return getId();
    }

    default boolean isEnabled() {
        return !getId().equals("minecraft");
    }

    void onRegistrationEvent(RegistrationEvent event, Side side);

    default void onMaterialEvent(MaterialEvent event){}

    default int getPriority() {
        return 1000;
    }

}
