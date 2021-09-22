package muramasa.antimatter.integration.kubejs;

import muramasa.antimatter.AntimatterMod;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraftforge.api.distmarker.Dist;

public class KubeJSRegistrar extends AntimatterMod {
    @Override
    public String getId() {
        return Ref.MOD_KJS;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Dist side) {

    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
