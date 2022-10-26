package muramasa.antimatter.fabric;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.AntimatterDynamics;
import net.fabricmc.api.DedicatedServerModInitializer;

import static muramasa.antimatter.Antimatter.LOGGER;

public class AntimatterServerImpl implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Antimatter.LOGGER.info("server initializing");
        AntimatterDynamics.runDataProvidersDynamically();
        AntimatterAPI.getCommonDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during common setup: " + ex.getMessage());
                }
            }
        });
        AntimatterAPI.getServerDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during server setup: " + ex.getMessage());
                }
            }
        });
    }
}
