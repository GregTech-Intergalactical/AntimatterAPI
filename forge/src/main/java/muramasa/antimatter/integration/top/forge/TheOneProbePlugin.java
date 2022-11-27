package muramasa.antimatter.integration.top.forge;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.function.Function;

import static muramasa.antimatter.Antimatter.LOGGER;

public class TheOneProbePlugin implements Function<ITheOneProbe, Void> {
	public static void enqueueIMC(final InterModEnqueueEvent event) {
		InterModComms.sendTo("theoneprobe", "getTheOneProbe", TheOneProbePlugin::new);
	}

	@Override
	public Void apply(ITheOneProbe input) {
		input.registerProvider(new EnergyInfoProvider());
		input.registerProvider(new MultiblockInfoProvider());
		input.registerProvider(new RecipeInfoProvider());
		return null;
	}
}
