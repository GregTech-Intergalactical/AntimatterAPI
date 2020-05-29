package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.util.Utils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AntimatterFluidTagProvider extends FluidTagsProvider implements IAntimatterProvider {

    private String providerDomain, providerName;
    private boolean replace;

    public AntimatterFluidTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        this.tagToBuilder.clear();
        registerTags();
        TagCollection<Fluid> tags = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
        Map<ResourceLocation, Tag.Builder<Fluid>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().getId(), Map.Entry::getValue));
        tags.registerAll(map);
        tags.getTagMap().forEach((k, v) -> DynamicResourcePack.addTag("fluids", k, v.serialize(this.registry::getKey)));
        this.setCollection(tags);
    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    protected void registerTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(AntimatterFluid.class, domain).forEach(f -> getBuilder(Utils.getForgeFluidTag(f.getId())).add(f.getFluid(), f.getFlowingFluid()));
    }

    @Override
    public String getName() {
        return providerName;
    }

}
