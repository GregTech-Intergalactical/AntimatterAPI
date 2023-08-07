package muramasa.antimatter.integration.top;

import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.config.Config;
import muramasa.antimatter.Ref;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class TheOneProbePlugin implements Function<ITheOneProbe, Void> {
	@Override
	public Void apply(ITheOneProbe input) {
		Config.getTooltypeTags().putIfAbsent(new ResourceLocation(Ref.ID, "mineable/wrench"), "Wrench");
		Config.getTooltypeTags().putIfAbsent(new ResourceLocation(Ref.ID, "mineable/wire_cutter"), "Wire Cutter");
		input.registerProvider(new EnergyInfoProvider());
		input.registerProvider(new MultiblockInfoProvider());
		input.registerProvider(new RecipeInfoProvider());
		return null;
	}
}
