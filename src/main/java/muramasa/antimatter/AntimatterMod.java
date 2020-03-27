package muramasa.antimatter;

import muramasa.antimatter.registration.RegistrationHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AntimatterMod {

    public AntimatterMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegistryEvent.Register<?> e) -> {
            RegistrationHelper.onRegistryEvent(e);
        });
    }
}
