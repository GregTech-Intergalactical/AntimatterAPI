package muramasa.antimatter.integration.top;

import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.config.Config;
import muramasa.antimatter.Ref;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TheOneProbePlugin implements Function<ITheOneProbe, Void> {
    private static final List<Supplier<IProbeInfoProvider>> ADDON_PROVIDERS = new ArrayList<>();
	@Override
	public Void apply(ITheOneProbe input) {
		Config.getTooltypeTags().putIfAbsent(new ResourceLocation(Ref.ID, "mineable/wrench"), "Wrench");
		Config.getTooltypeTags().putIfAbsent(new ResourceLocation(Ref.ID, "mineable/wire_cutter"), "Wire Cutter");
		input.registerProvider(new EnergyInfoProvider());
		input.registerProvider(new MultiblockInfoProvider());
		input.registerProvider(new RecipeInfoProvider());
        ADDON_PROVIDERS.forEach(s -> {
            input.registerProvider(s.get());
        });
		return null;
	}

    public static void addProbeInfoProvider(Supplier<IProbeInfoProvider> providerSupplier){
        ADDON_PROVIDERS.add(providerSupplier);
    }
}
