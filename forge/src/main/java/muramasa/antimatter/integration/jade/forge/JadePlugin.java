package muramasa.antimatter.integration.jade.forge;

import mcp.mobius.waila.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.Tesseract;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    public static ResourceLocation EU = new ResourceLocation(Tesseract.API_ID, "eu");
    public static ResourceLocation HU = new ResourceLocation(Tesseract.API_ID, "hu");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EUProvider.INSTANCE, BlockEntity.class);
        registration.addConfig(EU, true);
        registration.addConfig(HU, true);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(EUProvider.INSTANCE, TooltipPosition.BODY, Block.class);
    }
}
