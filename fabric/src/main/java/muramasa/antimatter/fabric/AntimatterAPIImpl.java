package muramasa.antimatter.fabric;

import muramasa.antimatter.tile.TileEntityMachine;
import net.fabricatedforgeapi.fluid.IFluidHandlerStorage;
import net.fabricatedforgeapi.item.IItemHandlerStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AntimatterAPIImpl {
    @SuppressWarnings("UnstableApiUsage")
    public static void registerTransferApi(BlockEntityType<?> type){
        FluidStorage.SIDED.registerForBlockEntities((be, direction) -> ((TileEntityMachine<?>)be).fluidHandler.side(direction).map(f -> {
            if (f instanceof IFluidHandlerStorage storage){
                return storage;
            }
            return TransferApiImpl.EMPTY_STORAGE;
        }).orElse(TransferApiImpl.EMPTY_STORAGE), type);
        ItemStorage.SIDED.registerForBlockEntities((be, direction) -> ((TileEntityMachine<?>)be).itemHandler.side(direction).map(i -> {
            if (i instanceof IItemHandlerStorage storage){
                return storage;
            }
            return TransferApiImpl.EMPTY_STORAGE;
        }).orElse(TransferApiImpl.EMPTY_STORAGE), type);
    }

    public static boolean isModLoaded(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }

    public static void registerEventBus(){
    }

    public static boolean isRegistryEntry(Object object, String domain){
        return false;
    }
}
