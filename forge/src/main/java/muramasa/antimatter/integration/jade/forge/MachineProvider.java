package muramasa.antimatter.integration.jade.forge;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.api.ui.IProgressStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import snownee.jade.VanillaPlugin;
import tesseract.api.forge.TesseractCaps;
import tesseract.api.gt.IEnergyHandler;

public class MachineProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    public static MachineProvider INSTANCE = new MachineProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity == null) return;
        IEnergyHandler storage = blockEntity.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).orElse(null);
        if (storage != null && (!accessor.isServerConnected() || accessor.getServerData().contains("jadeEU"))) {
            IElementHelper helper = tooltip.getElementHelper();
            long cur, max;
            if (accessor.isServerConnected()) {
                cur = accessor.getServerData().getLong("jadeEU");
                max = accessor.getServerData().getLong("jadeMaxEU");
            } else {
                cur = storage.getEnergy();
                max = storage.getCapacity();
            }
            String curText = ChatFormatting.WHITE + VanillaPlugin.getDisplayHelper().humanReadableNumber(cur, "EU", false) + ChatFormatting.AQUA;
            String maxText = VanillaPlugin.getDisplayHelper().humanReadableNumber(max, "EU", false);
            MutableComponent text = new TranslatableComponent("jade.fe", curText, maxText).withStyle(ChatFormatting.GRAY);
            IProgressStyle progressStyle = helper.progressStyle().color(0xFFFF0000, 0xFF660000);
            tooltip.add(helper.progress((float) cur / max, text, progressStyle, helper.borderStyle()).tag(VanillaPlugin.FORGE_ENERGY));
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        IEnergyHandler handler = blockEntity.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).orElse(null);
        if (handler != null) {
            compoundTag.putLong("jadeEU", handler.getEnergy());
            compoundTag.putLong("jadeMaxEU", handler.getCapacity());
        }
    }
}
