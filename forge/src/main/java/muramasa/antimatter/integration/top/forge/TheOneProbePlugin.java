package muramasa.antimatter.integration.top.forge;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbePlugin {
	public static void init() {
		ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new EnergyInfoProvider());
        oneProbe.registerProvider(new RecipeInfoProvider());
	}
}
