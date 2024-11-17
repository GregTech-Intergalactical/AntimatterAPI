package muramasa.antimatter.integration.jade.forge;

import mcp.mobius.waila.api.*;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.Tesseract;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    public static ResourceLocation EU = new ResourceLocation(Tesseract.API_ID, "eu");
    public static ResourceLocation HU = new ResourceLocation(Tesseract.API_ID, "hu");
    public static ResourceLocation PROGRESS = new ResourceLocation(Ref.ID, "machine_progress");
    public static ResourceLocation STRUCTURE = new ResourceLocation(Ref.ID, "structure_valid");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EUProvider.INSTANCE, BlockEntity.class);
        registration.registerBlockDataProvider(MachineProvider.INSTANCE, BlockEntityMachine.class);
        registration.addConfig(EU, true);
        registration.addConfig(HU, true);
        registration.addConfig(PROGRESS, true);
        registration.addConfig(STRUCTURE, true);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(EUProvider.INSTANCE, TooltipPosition.BODY, Block.class);
        registration.registerComponentProvider(MachineProvider.INSTANCE, TooltipPosition.BODY, Block.class);
    }
}
