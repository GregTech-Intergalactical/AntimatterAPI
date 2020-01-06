package muramasa.gtu.client.events;

import muramasa.antimatter.blocks.IInfoProvider;
import muramasa.gtu.Ref;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Ref.MODID, value = Dist.CLIENT)
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
        if (MC.player.isSneaking()) {
            //TODO
            e.getLeft().add("");
            e.getLeft().add(TextFormatting.AQUA + "[GregTech Debug Client]");
        }
    }
}
