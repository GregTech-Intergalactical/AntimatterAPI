package muramasa.antimatter.util.forge;

import muramasa.antimatter.util.AntimatterPreLaunchUtil;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class AntimatterPreLaunchUtilImpl implements AntimatterPreLaunchUtil {
    @Override
    public boolean isModLoaded(String modid){
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modid::equals);
        }
        return ModList.get().isLoaded(modid);
    }
}
