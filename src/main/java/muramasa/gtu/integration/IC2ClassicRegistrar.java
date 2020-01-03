package muramasa.gtu.integration;

import muramasa.gtu.Ref;
import muramasa.antimatter.registration.IGregTechRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;

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
