package muramasa.antimatter.fabric;

import muramasa.antimatter.tile.TileEntityMachine;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.api.fabric.TesseractCapsImpl;

public class AntimatterAPIImpl {
    @SuppressWarnings("UnstableApiUsage")
    public static void registerTransferApi(BlockEntityType<?> type){
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction).map(f -> f).orElse(null), type);
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).map(i -> i).orElse(null), type);
        TesseractCapsImpl.ENERGY_HANDLER_SIDED.registerForBlockEntity((be, direction) -> be.getCapability(TesseractCapsImpl.ENERGY_HANDLER_CAPABILITY, direction).map(i -> i).orElse(null), type);
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
