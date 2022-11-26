package muramasa.antimatter.integration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.top.fabric.TheOneProbePlugin;

import static muramasa.antimatter.Antimatter.LOGGER;

public class Integrations {
    public static void setupIntegrations() {
        if(AntimatterAPI.isModLoaded(Ref.MOD_TOP)) {
            LOGGER.info("The One Probe is loaded, enabling integration");
            TheOneProbePlugin.init();
        }
    }
}
