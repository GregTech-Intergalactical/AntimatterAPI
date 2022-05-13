package muramasa.antimatter.forge;

import muramasa.antimatter.registration.AntimatterRegistration;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class AntimatterAPIImpl {
    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static void registerEventBus(){
        FMLJavaModLoadingContext.get().getModEventBus().register(AntimatterRegistration.class);
    }

    public static boolean isRegistryEntry(Object object, String domain){
        return object instanceof IForgeRegistryEntry<?> r && r.getRegistryName() != null
                && r.getRegistryName().getNamespace().equals(domain);
    }
}
