package muramasa.antimatter.registration;

import muramasa.antimatter.Ref;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IAntimatterObject {

    default String getDomain() {
        return Ref.ID;
    }

    String getId();

    default boolean shouldRegister() {
        return true;
    }

    default ResourceLocation getLoc() {
        return new ResourceLocation(getDomain(), getId());
    }

    /**
     * Translates this AntimatterObject. Return null if there is no implementable translation.
     * @param lang the language to provide for.
     * @return a component to render.
     */
    default String getLang(String lang) {
        return null;
    }
}
