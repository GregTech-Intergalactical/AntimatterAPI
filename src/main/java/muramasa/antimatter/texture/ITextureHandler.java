package muramasa.antimatter.texture;

import muramasa.antimatter.tier.VoltageTier;
import muramasa.antimatter.machine.types.Machine;

public interface ITextureHandler {

    Texture[] getBase(Machine type, VoltageTier tier);
}
