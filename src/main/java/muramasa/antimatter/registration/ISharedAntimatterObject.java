package muramasa.antimatter.registration;

import muramasa.antimatter.Ref;

public interface ISharedAntimatterObject extends IAntimatterObject{
    default String getDomain() {
        return Ref.ID;
    }
}
