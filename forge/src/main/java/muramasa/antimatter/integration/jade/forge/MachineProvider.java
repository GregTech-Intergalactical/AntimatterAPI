package muramasa.antimatter.integration.jade.forge;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.api.ui.IProgressStyle;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.VanillaPlugin;

public class MachineProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {
    public static MachineProvider INSTANCE = new MachineProvider();
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof BlockEntityMachine<?> machine) {
            if (config.get(JadePlugin.PROGRESS)){
                MachineRecipeHandler<?> recipeHandler = machine.recipeHandler.orElse(null);
                if (recipeHandler != null && (!accessor.isServerConnected() || accessor.getServerData().contains("jadeProgress"))) {
                    IElementHelper helper = tooltip.getElementHelper();
                    int cur, max;
                    boolean active;
                    if (accessor.isServerConnected()) {
                        cur = accessor.getServerData().getInt("jadeProgress");
                        max = accessor.getServerData().getInt("jadeMaxProgress");
                        active = accessor.getServerData().getBoolean("jadeActive");
                    } else {
                        cur = recipeHandler.getCurrentProgress();
                        max = recipeHandler.getMaxProgress();
                        active = machine.getMachineState() == MachineState.ACTIVE;
                    }
                    if (max > 0 && active){
                        String curText = ChatFormatting.WHITE + String.valueOf(max >= 20 ? Math.round(cur / 20.0) : cur) + ChatFormatting.GRAY;
                        String maxText = (max >= 20 ? Math.round(max / 20.0) : max) + " " + (max >= 20 ? "s" : "t");
                        MutableComponent text = new TranslatableComponent("jade.fe", curText, maxText).withStyle(ChatFormatting.WHITE);
                        IProgressStyle progressStyle = helper.progressStyle().color(0xFF4CBB17, 0xFF4CBB17);
                        tooltip.add(helper.progress((float) cur / max, text, progressStyle, helper.borderStyle()).tag(JadePlugin.PROGRESS));
                    }
                }
            }
            if (machine instanceof BlockEntityBasicMultiMachine<?> multiMachine && config.get(JadePlugin.STRUCTURE)){
                if (!accessor.isServerConnected() || accessor.getServerData().contains("jadeStructureValid")){
                    boolean validStructure;
                    if (accessor.isServerConnected()) {
                        validStructure = accessor.getServerData().getBoolean("jadeStructureValid");
                    } else {
                        validStructure = multiMachine.isStructureValid();
                    }
                    if (validStructure) {
                        tooltip.add(tooltip.getElementHelper().text(Utils.translatable("antimatter.tooltip.valid_structure").withStyle(ChatFormatting.GREEN)).tag(JadePlugin.STRUCTURE));
                    } else {
                        tooltip.add(tooltip.getElementHelper().text(Utils.translatable("antimatter.tooltip.invalid_structure").withStyle(ChatFormatting.RED)).tag(JadePlugin.STRUCTURE));
                    }
                }

            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        if (blockEntity instanceof BlockEntityMachine<?> machine){
            machine.recipeHandler.ifPresent(r -> {
                compoundTag.putInt("jadeProgress", r.getCurrentProgress());
                compoundTag.putInt("jadeMaxProgress", r.getMaxProgress());
                compoundTag.putBoolean("jadeActive", machine.getMachineState() == MachineState.ACTIVE);
            });
            if (machine instanceof BlockEntityBasicMultiMachine<?> multiMachine){
                compoundTag.putBoolean("jadeStructureValid", multiMachine.isStructureValid());
            }
        }
    }
}
