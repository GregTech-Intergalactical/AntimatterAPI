package muramasa.antimatter.texture;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;

public interface ITextureHandler {
    //Can also return just one texture.
    Texture[] getBase(Machine type, Tier tier);
}
