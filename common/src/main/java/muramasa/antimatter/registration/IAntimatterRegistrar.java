package muramasa.antimatter.registration;

public interface IAntimatterRegistrar extends IAntimatterObject {


    default String getDomain() {
        return getId();
    }

    default boolean isEnabled() {
        return !getId().equals("minecraft");
    }

    void onRegistrationEvent(RegistrationEvent event, Side side);

    default int getPriority() {
        return 1000;
    }

}
