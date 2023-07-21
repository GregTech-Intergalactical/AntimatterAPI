package muramasa.antimatter.fabric;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.fabric.fluid.storage.FabricBlockFluidContainer;
import earth.terrarium.botarium.util.Updatable;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.fabric.AntimatterLookups;
import muramasa.antimatter.item.IFluidItem;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.pipe.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tesseract.api.fabric.TesseractLookups;
import tesseract.api.fabric.wrapper.ContainerItemContextWrapper;
import tesseract.api.fabric.wrapper.ExtendedContainerWrapper;
import tesseract.api.fluid.IFluidNode;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IEnergyItem;
import tesseract.api.heat.IHeatHandler;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.fabric.TesseractImpl;

public class AntimatterAPIImpl {
    @SuppressWarnings("UnstableApiUsage")
    public static void registerTransferApi(BlockEntityType<? extends TileEntityMachine<?>> type){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.fluidHandler.side(direction).map(f -> new FabricBlockFluidContainer(f, t -> {}, be)).orElse(null), type);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.itemHandler.side(direction).map(ExtendedContainerWrapper::new).orElse(null), type);
        TesseractLookups.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> be.energyHandler.map(i -> i).orElse(null), type);
        TesseractImpl.registerTRETile((be, direction) -> be.energyHandler.side(direction).orElse(null), (be, direction) -> be.rfHandler.side(direction).orElse(null), type);
        if (AntimatterAPI.isModLoaded("modern_industrialization")) {
            TesseractImpl.registerMITile((be, direction) -> be.energyHandler.side(direction).orElse(null), type);
        }
        AntimatterLookups.COVER_HANDLER_SIDED.registerForBlockEntity((be, direction) -> be.coverHandler.side(direction).orElse(null), type);
    }

    public static void registerTransferApiPipe(BlockEntityType<? extends TileEntityPipe<?>> type){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof TileEntityFluidPipe<?> fluidPipe)) return null;
            return (Storage<FluidVariant>) fluidPipe.getPipeCapHolder().side(direction).map(f -> new FabricBlockFluidContainer((FluidContainer) f, b -> {}, be)).orElse(null);
        }, type);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof TileEntityItemPipe<?> itemPipe)) return null;
            return (Storage<ItemVariant>) itemPipe.getPipeCapHolder().side(direction).map(i -> new ExtendedContainerWrapper((ExtendedItemContainer) i)).orElse(null);
        }, type);
        TesseractLookups.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof TileEntityCable<?> cable)) return null;
            return (IEnergyHandler) cable.getPipeCapHolder().side(direction).orElse(null);
        }, type);
        TesseractImpl.registerTRETile((be, direction) -> {
            if (!(be instanceof TileEntityCable<?> cable)) return null;
            return (IEnergyHandler) cable.getPipeCapHolder().side(direction).orElse(null);
        }, (be, direction) -> null, type);
        if (AntimatterAPI.isModLoaded("modern_industrialization")) {
            TesseractImpl.registerMITile((be, direction) -> {
                if (!(be instanceof TileEntityCable<?> cable)) return null;
                return (IEnergyHandler) cable.getPipeCapHolder().side(direction).orElse(null);
            }, type);
        }
        TesseractLookups.HEAT_HANDLER_SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof TileEntityHeatPipe<?> heatPipe)) return null;
            return (IHeatHandler) heatPipe.getPipeCapHolder().side(direction).orElse(null);
        }, type);
        AntimatterLookups.COVER_HANDLER_SIDED.registerForBlockEntity((be, direction) -> be.coverHandler.map(c -> c).orElse(null), type);
    }

    public static void registerItemTransferAPI(Item item){
        if (item instanceof IEnergyItem energyItem){
            TesseractImpl.registerTREItem((s, c) -> energyItem.createEnergyHandler(new ContainerItemContextWrapper(c)), item);
        }
    }

    public static void registerEventBus(){
    }

    public static boolean isRegistryEntry(Object object, String domain){
        return false;
    }
}
