package muramasa.antimatter.forge;

import net.minecraft.world.level.block.entity.BlockEntityType;

public class AntimatterAPIImpl {
    public static void registerTransferApi(BlockEntityType<?> type){}

    public static void registerTransferApiPipe(BlockEntityType<?> type){}

    public static void registerEventBus(){
        //FMLJavaModLoadingContext.get().getModEventBus().register(AntimatterRegistration.class);
    }

    public static boolean isRegistryEntry(Object object, String domain){
        return object instanceof IForgeRegistryEntry<?>;
    }
}
