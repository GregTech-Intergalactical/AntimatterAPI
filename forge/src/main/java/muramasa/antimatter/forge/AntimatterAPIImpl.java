package muramasa.antimatter.forge;

import muramasa.antimatter.AntimatterAPIPlatformHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AntimatterAPIImpl implements AntimatterAPIPlatformHelper {
    public void registerTransferApi(BlockEntityType<? extends muramasa.antimatter.blockentity.BlockEntityMachine<?>> type){}

    public void registerTransferApiPipe(BlockEntityType<? extends muramasa.antimatter.blockentity.pipe.BlockEntityPipe<?>> type){}

    public boolean isRegistryEntry(Object object, String domain){
        return object instanceof IForgeRegistryEntry<?> r && r.getRegistryName() != null
                && r.getRegistryName().getNamespace().equals(domain);
    }
}
