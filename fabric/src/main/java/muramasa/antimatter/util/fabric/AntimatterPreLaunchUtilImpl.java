package muramasa.antimatter.util.fabric;

import muramasa.antimatter.util.AntimatterPreLaunchUtil;
import net.fabricmc.loader.api.FabricLoader;

public class AntimatterPreLaunchUtilImpl implements AntimatterPreLaunchUtil {
    @Override
    public boolean isModLoaded(String modid){
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
