package muramasa.antimatter.fabric;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.fabric.fluid.storage.FabricBlockFluidContainer;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterAPIPlatformHelper;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.pipe.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tesseract.api.fabric.TesseractLookups;
import tesseract.api.fabric.wrapper.ContainerItemContextWrapper;
import tesseract.api.fabric.wrapper.ExtendedContainerWrapper;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IEnergyItem;
import tesseract.api.heat.IHeatHandler;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.fabric.TesseractImpl;

public class AntimatterAPIImpl implements AntimatterAPIPlatformHelper {
    @SuppressWarnings("UnstableApiUsage")
    public void registerTransferApi(BlockEntityType<? extends BlockEntityMachine<?>> type){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.fluidHandler.side(direction).map(f -> new FabricBlockFluidContainer(f, t -> {}, be)).orElse(null), type);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.itemHandler.side(direction).map(ExtendedContainerWrapper::new).orElse(null), type);
        TesseractLookups.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> be.energyHandler.map(i -> i).orElse(null), type);
        TesseractImpl.registerTRETile((be, direction) -> be.energyHandler.side(direction).orElse(null), (be, direction) -> be.rfHandler.side(direction).orElse(null), type);
        if (AntimatterAPI.isModLoaded("modern_industrialization")) {
            TesseractImpl.registerMITile((be, direction) -> be.energyHandler.side(direction).orElse(null), type);
        }
    }

    public void registerTransferApiPipe(BlockEntityType<? extends BlockEntityPipe<?>> type){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof BlockEntityFluidPipe<?> fluidPipe)) return null;
            return (Storage<FluidVariant>) fluidPipe.getPipeCapHolder().side(direction).map(f -> new FabricBlockFluidContainer((FluidContainer) f, b -> {}, be)).orElse(null);
        }, type);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof BlockEntityItemPipe<?> itemPipe)) return null;
            return (Storage<ItemVariant>) itemPipe.getPipeCapHolder().side(direction).map(i -> new ExtendedContainerWrapper((ExtendedItemContainer) i)).orElse(null);
        }, type);
        TesseractLookups.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof BlockEntityCable<?> cable)) return null;
            return (IEnergyHandler) cable.getPipeCapHolder().side(direction).orElse(null);
        }, type);
        TesseractImpl.registerTRETile((be, direction) -> {
            if (!(be instanceof BlockEntityCable<?> cable)) return null;
            return (IEnergyHandler) cable.getPipeCapHolder().side(direction).orElse(null);
        }, (be, direction) -> null, type);
        if (AntimatterAPI.isModLoaded("modern_industrialization")) {
            TesseractImpl.registerMITile((be, direction) -> {
                if (!(be instanceof BlockEntityCable<?> cable)) return null;
                return (IEnergyHandler) cable.getPipeCapHolder().side(direction).orElse(null);
            }, type);
        }
        TesseractLookups.HEAT_HANDLER_SIDED.registerForBlockEntity((be, direction) -> {
            if (!(be instanceof BlockEntityHeatPipe<?> heatPipe)) return null;
            return (IHeatHandler) heatPipe.getPipeCapHolder().side(direction).orElse(null);
        }, type);
    }

    public static void registerItemTransferAPI(Item item){
        if (item instanceof IEnergyItem energyItem){
            TesseractImpl.registerTREItem((s, c) -> energyItem.createEnergyHandler(new ContainerItemContextWrapper(c)), item);
        }
    }


    public boolean isRegistryEntry(Object object, String domain){
        return false;
    }
}
