package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DynamicResourcePackFinder implements RepositorySource {

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
    public void loadPacks(Consumer<Pack> packs, Pack.PackConstructor infoFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        //TODO: true or false, dunno
        packs.accept(Pack.create(id, true, () -> dynamicPack, infoFactory, Pack.Position.TOP, PackSource.BUILT_IN));
        // packs.accept(infoFactory.create(id, false, () -> dynamicPack, dynamicPack, new PackMetadataSection(new StringTextComponent("Dynamic Resources"), SharedConstants.getVersion().getPackVersion()),ResourcePackInfo.Priority.TOP, IPackNameDecorator.PLAIN));
        //ResourcePackInfo packInfo = new ResourcePackInfo(id, true, () -> dynamicPack, new StringTextComponent(name), new StringTextComponent("Dynamic Resources"), PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.TOP, false, null, hidden);
        // packs.accept(packInfo);
    }
}
