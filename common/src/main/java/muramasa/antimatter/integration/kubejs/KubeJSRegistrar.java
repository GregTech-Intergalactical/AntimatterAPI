package muramasa.antimatter.integration.kubejs;

import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.AntimatterMod;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.util.AntimatterPlatformUtils;

;

public class KubeJSRegistrar extends AntimatterMod {
    public KubeJSRegistrar() {
        if (AntimatterPlatformUtils.isFabric()){
            onRegistrarInit();
        }
    }

    @Override
    public void onRegistrarInit() {
        super.onRegistrarInit();
        AntimatterDynamics.clientProvider(Ref.MOD_KJS, g -> new AntimatterBlockStateProvider(Ref.MOD_KJS, "KubeJS BlockStates", g));
        AntimatterDynamics.clientProvider(Ref.MOD_KJS, g -> new AntimatterItemModelProvider(Ref.MOD_KJS, "KubeJS Item Models", g));
        AntimatterDynamics.clientProvider(Ref.MOD_KJS, g -> new AntimatterLanguageProvider(Ref.MOD_KJS, "KubeJS en_us Localization", "en_us", g));
    }

    public static void providerEvent(ProvidersEvent ev) {
        if (ev.getSide() == Side.CLIENT) {

        } else {
            final AntimatterBlockTagProvider[] p = new AntimatterBlockTagProvider[1];
            ev.addProvider(Ref.MOD_KJS, g -> {
                p[0] = new AntimatterBlockTagProvider(Ref.MOD_KJS, "KubeJS Block Tags", false, g);
                return p[0];
            });
            ev.addProvider(Ref.MOD_KJS, g ->
                    new AntimatterItemTagProvider(Ref.MOD_KJS, "KubeJS Item Tags", false, g, p[0]));
            ev.addProvider(Ref.MOD_KJS, g -> new AntimatterBlockLootProvider(Ref.MOD_KJS, "KubeJS Loot generator", g));
        }
    }

    @Override
    public String getId() {
        return Ref.MOD_KJS;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Side side) {
        if (event == RegistrationEvent.DATA_INIT){
            AntimatterKubeJS.loadStartup();
        }
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
