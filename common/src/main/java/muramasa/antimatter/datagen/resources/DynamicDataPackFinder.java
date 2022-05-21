package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
//@Mod.EventBusSubscriber(modid = Ref.ID)
public class DynamicDataPackFinder implements RepositorySource {

    protected final String id, name;

    public DynamicDataPackFinder(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /*
    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        T genericPackInfo = ResourcePackInfo.createResourcePack(id, true, () -> dynamicPack, packInfoFactory, ResourcePackInfo.Priority.TOP);
        nameToPackMap.put(id, genericPackInfo);
    }*/


    //@SubscribeEvent
    public static void addPackFinder(/*ServerStartingEvent e*/) {
        //Antimatter.LOGGER.info("Adding Antimatter's Dynamic Datapack to the server...");
        //e.getServer().getResourcePacks().addPackFinder(Ref.SERVER_PACK_FINDER);
        //e.getServer().getResourcePacks().getEnabledPacks().forEach(p -> Antimatter.LOGGER.info(p.getName() + " is being loaded into the server..."));
    }

    @Override
    public void loadPacks(Consumer<Pack> infoConsumer, Pack.PackConstructor infoFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name, AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        //TODO: not sure here
        Pack genericPackInfo = Pack.create(id, true, () -> dynamicPack, infoFactory, Pack.Position.TOP, PackSource.BUILT_IN);
        infoConsumer.accept(genericPackInfo);

    }
}
