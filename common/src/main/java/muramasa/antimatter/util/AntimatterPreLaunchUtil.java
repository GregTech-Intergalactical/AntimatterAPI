package muramasa.antimatter.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import tesseract.TesseractCapUtils;

import java.util.ServiceLoader;

public interface AntimatterPreLaunchUtil {
    AntimatterPreLaunchUtil INSTANCE =  ServiceLoader.load(AntimatterPreLaunchUtil.class).findFirst().orElseThrow(() -> new IllegalStateException("No implementation of AntimatterPreLaunchUtil found"));
    boolean isModLoaded(String modid);
}
