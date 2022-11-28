package muramasa.antimatter.integration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.top.TheOneProbePlugin;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import static muramasa.antimatter.Antimatter.LOGGER;

public class Integrations {
    public static void enqueueIMC(final InterModEnqueueEvent event) {
        if(AntimatterAPI.isModLoaded(Ref.MOD_TOP)) {
            LOGGER.info("The One Probe is loaded, enabling integration");
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbePlugin::new);
        }
    }
}
