package muramasa.antimatter.client.event.forge;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.block.IInfoProvider;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.SoundHelper;
import muramasa.antimatter.client.event.ClientEvents;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.mixin.client.LevelRendererAccessor;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.behaviour.BehaviourAOEBreak;
import muramasa.antimatter.tool.behaviour.BehaviourExtendedHighlight;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent.HighlightBlock;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Ref.ID, value = Dist.CLIENT)
public class ClientEventsForge {

    @SubscribeEvent
    public static void onBlockHighlight(HighlightBlock event) throws IllegalAccessException {
        if (ClientEvents.onBlockHighlight(event.getLevelRenderer(), event.getCamera(), event.getTarget(), event.getPartialTicks(), event.getPoseStack(), event.getMultiBufferSource()))
            event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    protected static void onTooltipAdd(final ItemTooltipEvent ev) {
        MaterialType.addTooltip(ev.getItemStack(), ev.getToolTip(), ev.getPlayer(), ev.getFlags());
        ClientEvents.onItemTooltip(ev.getFlags(), ev.getToolTip());
    }

    //TODO why is this client only?
    //Needs some work, won't work in 3rd person also, needs special ItemModel properties
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            Player player = e.player;
            if (player == null || player.getMainHandItem().isEmpty()) return;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof IAntimatterTool)) return;
            IAntimatterTool item = (IAntimatterTool) stack.getItem();
            if (item.getAntimatterToolType().getUseAction() != UseAnim.NONE && player.swinging) {
                item.getItem().onUsingTick(stack, player, stack.getCount());
                //player.swingProgress = player.prevSwingProgress;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderDebugInfo(RenderGameOverlayEvent.Text e) {
        ClientEvents.onRenderDebugInfo(e.getLeft());
    }

    @SubscribeEvent
    public static void onGuiMouseScrollPre(ScreenEvent.MouseScrollEvent.Pre e) {
        ClientEvents.onGuiMouseScrollPre(e.getScrollDelta());
    }
    /*@SubscribeEvent
    public static void onGuiMouseClickPre(ScreenEvent.MouseClickedEvent.Pre e) {
        ClientEvents.onGuiMouseClickPre(e.getButton());
    }*/

    @SubscribeEvent
    public static void onGuiMouseReleasedPre(ScreenEvent.MouseReleasedEvent.Pre e) {
        ClientEvents.onGuiMouseReleasedPre(e.getButton());
    }

    @SubscribeEvent
    public static void worldUnload(WorldEvent.Unload ev) {
        SoundHelper.worldUnload(ev.getWorld());
    }
}
