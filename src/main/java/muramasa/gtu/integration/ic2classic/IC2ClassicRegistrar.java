package muramasa.gtu.integration.ic2classic;

import muramasa.gtu.Ref;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;

public class IC2ClassicRegistrar implements IGregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MOD_IC2C;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        //TODO
    }
}
