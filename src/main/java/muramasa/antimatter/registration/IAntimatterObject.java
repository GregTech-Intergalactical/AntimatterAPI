package muramasa.antimatter.registration;

import muramasa.antimatter.Ref;

public interface IAntimatterObject {

    default String getDomain() {
        return Ref.ID;
    }

    String getId();
}
