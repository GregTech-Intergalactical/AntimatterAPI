package muramasa.antimatter.fabric;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.Side;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class AntimatterPreInitializer implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        AntimatterAPI.setSIDE(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER);
    }
}
