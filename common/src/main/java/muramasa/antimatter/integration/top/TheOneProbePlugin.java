package muramasa.antimatter.integration.top;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TheOneProbePlugin implements Function<ITheOneProbe, Void> {
	@Override
	public Void apply(ITheOneProbe input) {
		input.registerProvider(new EnergyInfoProvider());
		input.registerProvider(new MultiblockInfoProvider());
		input.registerProvider(new RecipeInfoProvider());
		return null;
	}
}
