package muramasa.gregtech.integration.ic2classic;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.registration.GregTechRegistrar;

public class IC2ClassicRegistrar extends GregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MOD_IC2C;
    }

    @Override
    public void onMachineRecipeRegistration() {

    }
}
