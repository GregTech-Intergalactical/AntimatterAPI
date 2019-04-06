package muramasa.gtu.integration.ic2classic;

import muramasa.gtu.Ref;
import muramasa.gtu.api.registration.GregTechRegistrar;

public class IC2ClassicRegistrar extends GregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MOD_IC2C;
    }

    @Override
    public void onMachineRecipeRegistration() {

    }
}
