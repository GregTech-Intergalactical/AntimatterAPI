package muramasa.antimatter.texture;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;

public interface IOverlayTexturer {
    Texture[] getOverlays(Machine type, MachineState state, Tier tier);
}
