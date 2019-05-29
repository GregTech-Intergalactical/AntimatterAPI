package muramasa.gtu.api.registration;

import muramasa.gtu.api.util.Utils;
import net.minecraftforge.fml.common.Loader;

public interface IGregTechRegistrar {

    String getId();

    default boolean isEnabled() {
        return Utils.isModLoaded(getId());
    }

    void onRegistrationEvent(RegistrationEvent event);
}
