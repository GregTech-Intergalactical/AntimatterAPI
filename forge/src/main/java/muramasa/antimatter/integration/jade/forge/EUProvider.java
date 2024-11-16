package muramasa.antimatter.integration.jade.forge;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.api.ui.IProgressStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.VanillaPlugin;
import tesseract.api.forge.TesseractCaps;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.heat.IHeatHandler;

public class EUProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    public static EUProvider INSTANCE = new EUProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity == null) return;
        if (Minecraft.getInstance().player == null) return;
        if (config.get(JadePlugin.EU)){
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
                String curText = ChatFormatting.WHITE + humanReadableNumber(cur, "EU", Minecraft.getInstance().player.isCrouching()) + ChatFormatting.GRAY;
                String maxText = humanReadableNumber(max, "EU", Minecraft.getInstance().player.isCrouching());
                MutableComponent text = new TranslatableComponent("jade.fe", curText, maxText).withStyle(ChatFormatting.WHITE);
                IProgressStyle progressStyle = helper.progressStyle().color(0xFF00FFFF, 0xFF009999);
                tooltip.add(helper.progress((float) cur / max, text, progressStyle, helper.borderStyle()).tag(JadePlugin.EU));
            }
        }
        if (config.get(JadePlugin.HU)){
            IHeatHandler storage = blockEntity.getCapability(TesseractCaps.HEAT_CAPABILITY).orElse(null);
            if (storage != null && (!accessor.isServerConnected() || accessor.getServerData().contains("jadeHU"))) {
                IElementHelper helper = tooltip.getElementHelper();
                long cur, max;
                if (accessor.isServerConnected()) {
                    cur = accessor.getServerData().getLong("jadeHU");
                    max = accessor.getServerData().getLong("jadeMaxHU");
                } else {
                    cur = storage.getHeat();
                    max = storage.getHeatCap();
                }
                String curText = ChatFormatting.WHITE + humanReadableNumber(cur, "HU", Minecraft.getInstance().player.isCrouching()) + ChatFormatting.GRAY;
                String maxText = humanReadableNumber(max, "HU", Minecraft.getInstance().player.isCrouching());
                MutableComponent text = new TranslatableComponent("jade.fe", curText, maxText).withStyle(ChatFormatting.WHITE);
                IProgressStyle progressStyle = helper.progressStyle().color(0xFFFF0000, 0xFF660000);
                tooltip.add(helper.progress((float) cur / max, text, progressStyle, helper.borderStyle()).tag(JadePlugin.HU));
            }
        }
    }

    private String humanReadableNumber(double number, String unit, boolean shift) {
        return shift ? number + unit : VanillaPlugin.getDisplayHelper().humanReadableNumber(number, unit, false);
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        IEnergyHandler handler = blockEntity.getCapability(TesseractCaps.ENERGY_HANDLER_CAPABILITY).orElse(null);
        if (handler != null) {
            compoundTag.putLong("jadeEU", handler.getEnergy());
            compoundTag.putLong("jadeMaxEU", handler.getCapacity());
        }
        IHeatHandler heatHandler = blockEntity.getCapability(TesseractCaps.HEAT_CAPABILITY).orElse(null);
        if (heatHandler != null) {
            compoundTag.putLong("jadeHU", heatHandler.getHeat());
            compoundTag.putLong("jadeMaxHU", heatHandler.getHeatCap());
        }
    }
}
