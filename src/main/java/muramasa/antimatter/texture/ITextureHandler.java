package muramasa.antimatter.texture;

import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.machines.types.Machine;

public interface ITextureHandler {

    Texture[] getBase(Machine type, Tier tier);
}
