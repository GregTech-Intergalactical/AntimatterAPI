package muramasa.antimatter.datagen;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.base.ExistingFileHelper;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Workaround until I figure out how to reference external resources from another mod for data gen
 **/
public class ExistingFileHelperOverride extends ExistingFileHelper {

    public static Set<String> GLOBAL_EXCLUDED_DOMAINS = new ObjectOpenHashSet<>();

    static {
        GLOBAL_EXCLUDED_DOMAINS.add(Ref.ID);
        GLOBAL_EXCLUDED_DOMAINS.add(Ref.SHARED_ID);
        GLOBAL_EXCLUDED_DOMAINS.add("minecraft");
        AntimatterAPI.all(IAntimatterRegistrar.class, r -> GLOBAL_EXCLUDED_DOMAINS.add(r.getDomain()));
    }

    Set<String> excludedDomains = new ObjectOpenHashSet<>();

    public ExistingFileHelperOverride() {
        super(Collections.emptyList(), Collections.emptySet(), true, null, null);
    }

    public ExistingFileHelperOverride(String... domains) {
        this();
        excludedDomains = new ObjectOpenHashSet<>(Arrays.asList(domains));
    }

    public ExistingFileHelperOverride addDomains(String... domains) {
        excludedDomains.addAll(Arrays.asList(domains));
        return this;
    }

    public Set<String> getExcludedDomains() {
        return excludedDomains;
    }

    @Override
    public boolean exists(ResourceLocation loc, PackType type, String pathSuffix, String pathPrefix) {
        return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || GLOBAL_EXCLUDED_DOMAINS.contains(loc.getNamespace()) || super.exists(loc, type, pathSuffix, pathPrefix);
    }

    @Override
    public boolean exists(ResourceLocation loc, PackType packType) {
        return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || GLOBAL_EXCLUDED_DOMAINS.contains(loc.getNamespace()) || super.exists(loc, packType);
    }

    @Override
    public boolean exists(ResourceLocation loc, IResourceType type) {
        return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || GLOBAL_EXCLUDED_DOMAINS.contains(loc.getNamespace()) || super.exists(loc, type);
    }
}
