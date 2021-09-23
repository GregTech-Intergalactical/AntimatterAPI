package muramasa.antimatter.integration.kubejs;

import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.AntimatterMod;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.providers.AntimatterLanguageProvider;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraftforge.api.distmarker.Dist;

public class KubeJSRegistrar extends AntimatterMod {
    public KubeJSRegistrar(){
        AntimatterDynamics.addProvider(Ref.MOD_KJS, g -> new AntimatterBlockStateProvider(Ref.MOD_KJS, "KubeJS BlockStates", g));
        AntimatterDynamics.addProvider(Ref.MOD_KJS, g -> new AntimatterItemModelProvider(Ref.MOD_KJS, "KubeJS Item Models", g));
        AntimatterDynamics.addProvider(Ref.MOD_KJS, g -> new AntimatterLanguageProvider(Ref.MOD_KJS, "KubeJS en_us Localization", "en_us", g));
    }

    @Override
    public String getId() {
        return Ref.MOD_KJS;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Dist side) {

    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
