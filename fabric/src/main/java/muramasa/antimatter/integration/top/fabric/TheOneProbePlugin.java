package muramasa.antimatter.integration.top.fabric;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbePlugin {
	public static void init() {
		ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new EnergyInfoProvider());
        oneProbe.registerProvider(new RecipeInfoProvider());
	}
}
