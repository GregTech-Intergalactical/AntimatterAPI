package muramasa.gtu.api.util;

import net.minecraft.client.resources.I18n;

public class GTLoc {

    public static String get(String toFormat) {
        return I18n.format(toFormat);
    }
}
