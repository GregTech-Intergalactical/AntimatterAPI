package muramasa.antimatter.registration;

import muramasa.antimatter.Ref;
import net.minecraft.util.ResourceLocation;

public interface IAntimatterObject {

    default String getDomain() {
        return Ref.ID;
    }

    String getId();

    default boolean shouldRegister() {
        return true;
    }

    default ResourceLocation getLoc(){
        return new ResourceLocation(getDomain(), getId());
    }
}
