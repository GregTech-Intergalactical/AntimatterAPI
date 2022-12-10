package muramasa.antimatter.forge;

import muramasa.antimatter.registration.forge.AntimatterRegistration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class AntimatterAPIImpl {
    public static void registerTransferApi(BlockEntityType<?> type){}

    public static void registerTransferApiPipe(BlockEntityType<?> type){}

    public static void registerEventBus(){
        //FMLJavaModLoadingContext.get().getModEventBus().register(AntimatterRegistration.class);
    }

    public static boolean isRegistryEntry(Object object, String domain){
        return object instanceof IForgeRegistryEntry<?> r && r.getRegistryName() != null
                && r.getRegistryName().getNamespace().equals(domain);
    }
}
