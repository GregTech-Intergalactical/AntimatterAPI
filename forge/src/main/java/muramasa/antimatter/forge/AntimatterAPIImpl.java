package muramasa.antimatter.forge;

import net.minecraft.world.level.block.entity.BlockEntityType;
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
