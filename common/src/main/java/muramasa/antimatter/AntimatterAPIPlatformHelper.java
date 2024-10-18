package muramasa.antimatter;

import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.util.ImplLoader;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface AntimatterAPIPlatformHelper {
    AntimatterAPIPlatformHelper INSTANCE = ImplLoader.load(AntimatterAPIPlatformHelper.class);

    boolean isRegistryEntry(Object object, String domain);


    void registerTransferApi(BlockEntityType<? extends BlockEntityMachine<?>> type);


    void registerTransferApiPipe(BlockEntityType<? extends BlockEntityPipe<?>> type);
}
