package muramasa.antimatter.registration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;

public interface IAntimatterRegistrar extends IAntimatterObject {

    default String getId() {
        return ModLoadingContext.get().getActiveNamespace();
    }

    default String getDomain() { return getId(); }

    default boolean isEnabled() {
        return !getId().equals("minecraft");
    }

    void onRegistrationEvent(RegistrationEvent event, Dist side);

}
