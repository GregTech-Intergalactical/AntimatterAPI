package muramasa.gtu.integration.top;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbePlugin {
	
	public static void init() {
		ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
		oneProbe.registerProvider(new EnergyStorageInfoProvider());
	}

}
