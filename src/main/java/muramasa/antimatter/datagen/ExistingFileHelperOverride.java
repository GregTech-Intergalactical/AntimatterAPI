package muramasa.antimatter.datagen;

import muramasa.antimatter.Ref;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import speiger.src.collections.objects.sets.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/** Workaround until I figure out how to reference external resources from another mod for data gen **/
public class ExistingFileHelperOverride extends ExistingFileHelper {

    public static Set<String> GLOBAL_EXCLUDED_DOMAINS = new ObjectOpenHashSet<>();

    static {
        GLOBAL_EXCLUDED_DOMAINS.add(Ref.ID);
        GLOBAL_EXCLUDED_DOMAINS.add("gti");
        GLOBAL_EXCLUDED_DOMAINS.add("minecraft");
    }

    Set<String> excludedDomains = new ObjectOpenHashSet<>();

    public ExistingFileHelperOverride() {
        super(Collections.emptyList(), true);
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
    public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
        return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || GLOBAL_EXCLUDED_DOMAINS.contains(loc.getNamespace()) || super.exists(loc, type, pathSuffix, pathPrefix);
    }

    @Override
    public boolean exists(ResourceLocation loc, ResourcePackType packType) {
        return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || GLOBAL_EXCLUDED_DOMAINS.contains(loc.getNamespace()) || super.exists(loc,packType);
    }

    @Override
    public boolean exists(ResourceLocation loc, IResourceType type) {
       return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || GLOBAL_EXCLUDED_DOMAINS.contains(loc.getNamespace()) || super.exists(loc, type);
    }
}
