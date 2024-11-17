package muramasa.antimatter.integration.jade.forge;

import mcp.mobius.waila.api.*;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.addon.harvest.SimpleToolHandler;
import tesseract.Tesseract;

import static muramasa.antimatter.data.AntimatterDefaultTools.WIRE_CUTTER;
import static muramasa.antimatter.data.AntimatterDefaultTools.WRENCH;

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
        HarvestToolProvider.registerHandler(new SimpleToolHandler("wrench", WRENCH.getToolType(), WRENCH.getToolItem(AntimatterMaterials.Iron), WRENCH.getToolItem(AntimatterMaterials.Diamond), WRENCH.getToolItem(AntimatterMaterials.NetherizedDiamond)));
        HarvestToolProvider.registerHandler(new SimpleToolHandler("wire_cutter", WIRE_CUTTER.getToolType(), WIRE_CUTTER.getToolItem(AntimatterMaterials.Iron), WIRE_CUTTER.getToolItem(AntimatterMaterials.Diamond), WIRE_CUTTER.getToolItem(AntimatterMaterials.NetherizedDiamond)));
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(EUProvider.INSTANCE, TooltipPosition.BODY, Block.class);
        registration.registerComponentProvider(MachineProvider.INSTANCE, TooltipPosition.BODY, Block.class);
    }
}
