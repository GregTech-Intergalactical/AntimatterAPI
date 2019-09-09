package muramasa.gtu.api.texture;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;

public interface ITextureHandler {

    Texture[] getBase(Machine type, Tier tier);
}
