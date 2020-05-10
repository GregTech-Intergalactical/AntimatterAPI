package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.util.Utils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;

public class AntimatterFluidTagProvider extends FluidTagsProvider {

    private String providerDomain, providerName;
    private boolean replace;

    public AntimatterFluidTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    protected void registerTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(AntimatterFluid.class).stream().filter(f -> f.getDomain().equals(domain)).forEach(f -> {
            this.getBuilder(Utils.getForgeFluidTag(f.getId())).add(f.getFluid(), f.getFlowingFluid());
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

}
