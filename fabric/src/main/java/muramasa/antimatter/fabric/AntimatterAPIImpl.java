package muramasa.antimatter.fabric;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.tile.TileEntityMachine;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tesseract.api.fabric.TesseractCapsImpl;
import tesseract.api.fabric.wrapper.ContainerItemContextWrapper;
import tesseract.api.gt.IEnergyItem;
import tesseract.fabric.TesseractImpl;

public class AntimatterAPIImpl {
    @SuppressWarnings("UnstableApiUsage")
    public static void registerTransferApi(BlockEntityType<? extends TileEntityMachine<?>> type){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.fluidHandler.side(direction).map(f -> f).orElse(null), type);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.itemHandler.side(direction).map(i -> i).orElse(null), type);
        TesseractCapsImpl.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> be.energyHandler.map(i -> i).orElse(null), type);
        TesseractImpl.registerTRETile((be, direction) -> be.energyHandler.side(direction).map(i -> i).orElse(null), type);
        if (AntimatterAPI.isModLoaded("modern_industrialization")) {
            TesseractImpl.registerMITile((be, direction) -> be.energyHandler.side(direction).map(i -> i).orElse(null), type);
        }
    }

    public static void registerItemTransferAPI(Item item){
        if (item instanceof IEnergyItem energyItem){
            TesseractCapsImpl.ENERGY_HANDLER_ITEM.registerForItems((s, c) -> energyItem.createEnergyHandler(new ContainerItemContextWrapper(c)), item);
        }
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
