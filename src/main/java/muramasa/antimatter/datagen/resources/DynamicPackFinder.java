package muramasa.antimatter.datagen.resources;

import com.google.common.collect.Sets;
import muramasa.antimatter.Antimatter;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;
import java.util.Set;

public class DynamicPackFinder implements IPackFinder {

    protected final String id, name, desc;
    protected final boolean hidden;
    protected final Set<String> domains;

    public DynamicPackFinder(String id, String name, String desc, boolean hidden, String... domains) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.hidden = hidden;
        this.domains = Sets.newHashSet(domains);
    }

    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packs, ResourcePackInfo.IFactory<T> factory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name);
        DynamicResourcePack.DOMAINS.addAll(domains);
        ClientResourcePackInfo packInfo = new ClientResourcePackInfo(
            id,
            true,
            () -> dynamicPack,
            new StringTextComponent(name),
            new StringTextComponent("Dynamic Resources"),
            PackCompatibility.COMPATIBLE,
            ResourcePackInfo.Priority.TOP,
            false,
            null,
            hidden
        );
        packs.put(packInfo.getName(), (T) packInfo);
    }
}
