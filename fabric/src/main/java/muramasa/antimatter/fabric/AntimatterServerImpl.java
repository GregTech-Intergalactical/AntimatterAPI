package muramasa.antimatter.fabric;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.datagen.AntimatterDynamics;
import net.fabricmc.api.DedicatedServerModInitializer;

public class AntimatterServerImpl implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Antimatter.LOGGER.info("server initializing");
        //AntimatterDynamics.runDataProvidersDynamically();
    }
}
