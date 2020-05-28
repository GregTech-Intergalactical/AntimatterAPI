package muramasa.antimatter.datagen.resources;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class DynamicDataPackFinder implements IPackFinder {

    protected final String id, name;

    public DynamicDataPackFinder(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
        DynamicResourcePack dynamicPack = new DynamicResourcePack(name);
        DynamicResourcePack.DOMAINS.add(Ref.ID);
        DynamicResourcePack.DOMAINS.addAll(AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet()));
        T genericPackInfo = ResourcePackInfo.createResourcePack(id, true, () -> dynamicPack, packInfoFactory, ResourcePackInfo.Priority.TOP);
        nameToPackMap.put(id, genericPackInfo);
    }

    @SubscribeEvent
    public static void addPackFinder(FMLServerAboutToStartEvent e) {
        Antimatter.LOGGER.info("Adding Antimatter's Dynamic Datapack to the server...");
        e.getServer().getResourcePacks().addPackFinder(Ref.SERVER_PACK_FINDER);
        e.getServer().getResourcePacks().getEnabledPacks().forEach(p -> Antimatter.LOGGER.info(p.getName() + " is being loaded into the server..."));
    }

}
