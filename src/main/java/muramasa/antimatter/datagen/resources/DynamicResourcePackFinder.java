package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DynamicResourcePackFinder implements IPackFinder {

    protected final String id, name, desc;
    protected final boolean hidden;

    public DynamicResourcePackFinder(String id, String name, String desc, boolean hidden) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.hidden = hidden;
    }

    /*@Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packs, ResourcePackInfo.IFactory<T> factory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        ResourcePackInfo packInfo = new ResourcePackInfo(id, true, () -> dynamicPack, new StringTextComponent(name), new StringTextComponent("Dynamic Resources"), PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.TOP, false, null, hidden);
        packs.put(packInfo.getName(), (T) packInfo);
    }*/

    @Override
    public void loadPacks(Consumer<ResourcePackInfo> packs, ResourcePackInfo.IFactory infoFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        //TODO: true or false, dunno
        packs.accept(ResourcePackInfo.create(id, true, () -> dynamicPack, infoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILT_IN));
        // packs.accept(infoFactory.create(id, false, () -> dynamicPack, dynamicPack, new PackMetadataSection(new StringTextComponent("Dynamic Resources"), SharedConstants.getVersion().getPackVersion()),ResourcePackInfo.Priority.TOP, IPackNameDecorator.PLAIN));
        //ResourcePackInfo packInfo = new ResourcePackInfo(id, true, () -> dynamicPack, new StringTextComponent(name), new StringTextComponent("Dynamic Resources"), PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.TOP, false, null, hidden);
        // packs.accept(packInfo);
    }
}
