package muramasa.antimatter.client.event;

import muramasa.antimatter.Ref;
import muramasa.antimatter.blocks.IInfoProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = Ref.ID, value = Dist.CLIENT)
public class ClientEvents {

    private static Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderDebugInfo(RenderGameOverlayEvent.Text e) {
        if (!MC.gameSettings.showDebugInfo || MC.objectMouseOver == null || MC.objectMouseOver.getType() != RayTraceResult.Type.BLOCK) return;
        World world = ServerLifecycleHooks.getCurrentServer().getWorld(MC.world.dimension.getType());
        BlockPos pos = new BlockPos(MC.objectMouseOver.getHitVec());
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IInfoProvider) {
            e.getLeft().add("");
            e.getLeft().add(TextFormatting.AQUA + "[GregTech Debug Server]");
            e.getLeft().addAll(((IInfoProvider) state.getBlock()).getInfo(new ArrayList<>(), world, state, pos));
        }
        if (MC.player.isCrouching()) {
            //TODO
            e.getLeft().add("");
            e.getLeft().add(TextFormatting.AQUA + "[GregTech Debug Client]");
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent e) {
        if (e.getFlags().isAdvanced() && Ref.SHOW_ITEM_TAGS) {
            Collection<ResourceLocation> tags = ItemTags.getCollection().getOwningTags(e.getItemStack().getItem());
            if (!tags.isEmpty()) {
                List<ITextComponent> list = e.getToolTip();
                list.add(new StringTextComponent(""));
                list.add(new StringTextComponent("Tags:").applyTextStyle(TextFormatting.DARK_GRAY));
                for (ResourceLocation loc : tags) {
                    list.add(new StringTextComponent(loc.toString()).applyTextStyle(TextFormatting.DARK_GRAY));
                }
            }
        }
    }
}
