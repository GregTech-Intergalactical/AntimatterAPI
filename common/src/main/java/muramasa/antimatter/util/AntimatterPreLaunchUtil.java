package muramasa.antimatter.util;

public interface AntimatterPreLaunchUtil {
    AntimatterPreLaunchUtil INSTANCE =  ImplLoader.load(AntimatterPreLaunchUtil.class);
    boolean isModLoaded(String modid);
}
