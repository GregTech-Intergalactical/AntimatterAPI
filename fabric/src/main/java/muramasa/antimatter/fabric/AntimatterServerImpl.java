package muramasa.antimatter.fabric;

import muramasa.antimatter.AntimatterDynamics;
import net.fabricmc.api.DedicatedServerModInitializer;

public class AntimatterServerImpl implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        AntimatterDynamics.runDataProvidersDynamically();
    }
}
