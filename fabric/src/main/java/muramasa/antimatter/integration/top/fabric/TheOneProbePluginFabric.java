package muramasa.antimatter.integration.top.fabric;

import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbePlugin;
import muramasa.antimatter.integration.top.TheOneProbePlugin;

public class TheOneProbePluginFabric implements ITheOneProbePlugin {
    @Override
    public void onLoad(ITheOneProbe iTheOneProbe) {
        new TheOneProbePlugin().apply(iTheOneProbe);
    }
}
