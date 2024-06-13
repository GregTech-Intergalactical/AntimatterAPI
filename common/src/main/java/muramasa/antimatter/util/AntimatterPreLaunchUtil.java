package muramasa.antimatter.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import tesseract.TesseractCapUtils;

import java.util.ServiceLoader;

public interface AntimatterPreLaunchUtil {
    AntimatterPreLaunchUtil INSTANCE =  ImplLoader.load(AntimatterPreLaunchUtil.class);
    boolean isModLoaded(String modid);
}
