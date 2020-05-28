package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.util.Utils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AntimatterFluidTagProvider extends FluidTagsProvider implements IAntimatterProvider {

    private String providerDomain, providerName;
    private boolean replace;
    private ResourceMethod method = ResourceMethod.PROVIDER_GEN;

    public AntimatterFluidTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run(ResourceMethod method) {
        this.method = method;
        // super.registerTags();
        registerTags();
        TagCollection<Fluid> collection = new TagCollection<>(f -> Optional.empty(), "", false, "generated");
        Map<ResourceLocation, Tag.Builder<Fluid>> map = this.tagToBuilder.entrySet().stream().collect(Collectors.toMap(m -> m.getKey().getId(), Map.Entry::getValue));
        collection.registerAll(map);
        this.setCollection(collection);
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
