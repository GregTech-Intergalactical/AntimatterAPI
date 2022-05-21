package muramasa.antimatter.integration.kubejs;

import muramasa.antimatter.AntimatterMod;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterBlockTagProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemTagProvider;
import muramasa.antimatter.datagen.providers.AntimatterLanguageProvider;
import muramasa.antimatter.event.forge.AntimatterProvidersEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;

public class KubeJSRegistrar extends AntimatterMod {
    public KubeJSRegistrar() {
        MinecraftForge.EVENT_BUS.addListener(this::providerEvent);
        AntimatterDynamics.clientProvider(Ref.MOD_KJS, g -> new AntimatterBlockStateProvider(Ref.MOD_KJS, "KubeJS BlockStates", g));
        AntimatterDynamics.clientProvider(Ref.MOD_KJS, g -> new AntimatterItemModelProvider(Ref.MOD_KJS, "KubeJS Item Models", g));
        AntimatterDynamics.clientProvider(Ref.MOD_KJS, g -> new AntimatterLanguageProvider(Ref.MOD_KJS, "KubeJS en_us Localization", "en_us", g));
    }

    private void providerEvent(AntimatterProvidersEvent ev) {
        if (ev.getSide() == Dist.CLIENT) {

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

    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
