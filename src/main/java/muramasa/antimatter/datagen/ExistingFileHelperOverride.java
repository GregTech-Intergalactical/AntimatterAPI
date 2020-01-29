package muramasa.antimatter.datagen;

import muramasa.antimatter.Ref;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import java.util.*;

/** Workaround until I figure out how to reference external resources from another mod for data gen **/
public class ExistingFileHelperOverride extends ExistingFileHelper {

    Set<String> excludedDomains = new HashSet<>();

    public ExistingFileHelperOverride() {
        super(Collections.emptyList(), true);
    }

    public ExistingFileHelperOverride(String... domains) {
        this();
        excludedDomains = new HashSet<>(Arrays.asList(domains));
    }

    @Override
    public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
        return loc.getNamespace().equals(Ref.ID) || excludedDomains.contains(loc.getNamespace()) || super.exists(loc, type, pathSuffix, pathPrefix);
    }
}
