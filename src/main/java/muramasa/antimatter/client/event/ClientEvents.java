package muramasa.antimatter.client.event;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.block.IInfoProvider;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.behaviour.BehaviourAOEBreak;
import muramasa.antimatter.tool.behaviour.BehaviourExtendedHighlight;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
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
import net.minecraftforge.client.event.DrawSelectionEvent.HighlightBlock;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = Ref.ID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onBlockHighlight(HighlightBlock event) throws IllegalAccessException {
        Player player = MC.player;
        Level world = player.getCommandSenderWorld();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || (!(stack.getItem() instanceof IAntimatterTool) && !(stack.getItem() instanceof IHaveCover)))
            return;
        if (stack.getItem() instanceof IHaveCover) {
            if (player.isCrouching()) return;
            RenderHelper.onDrawHighlight(player, event, b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION);
            event.setCanceled(true);
            return;
        }
        IAntimatterTool item = (IAntimatterTool) stack.getItem();
        AntimatterToolType type = item.getAntimatterToolType();
        if (player.isCrouching() && type != Data.WRENCH && type != Data.ELECTRIC_WRENCH && type != Data.CROWBAR && type != Data.WIRE_CUTTER)
            return;
        //Perform highlight of wrench
        InteractionResult res = item.onGenericHighlight(player, event);
        if (res.shouldSwing()) {
            return;
        }
        IBehaviour<IAntimatterTool> behaviour = type.getBehaviour("aoe_break");
        if (!(behaviour instanceof BehaviourAOEBreak)) return;
        BehaviourAOEBreak aoeBreakBehaviour = (BehaviourAOEBreak) behaviour;

        BlockPos currentPos = event.getTarget().getBlockPos();
        BlockState state = world.getBlockState(currentPos);
        if (state.isAir() || !Utils.isToolEffective(item, state)) return;
        Vec3 viewPosition = event.getCamera().getPosition();
        Entity entity = event.getCamera().getEntity();
        VertexConsumer builderLines = event.getMultiBufferSource().getBuffer(RenderType.LINES);
        PoseStack matrix = event.getPoseStack();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        ImmutableSet<BlockPos> positions = Utils.getHarvestableBlocksToBreak(world, player, item, aoeBreakBehaviour.getColumn(), aoeBreakBehaviour.getRow(), aoeBreakBehaviour.getDepth());
        for (BlockPos nextPos : positions) {
            double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
            VoxelShape shape = world.getBlockState(nextPos).getShape(world, nextPos, CollisionContext.of(entity));
            Matrix4f matrix4f = matrix.last().pose();
            matrix.pushPose();
            shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
                builderLines.vertex(matrix4f, (float) (minX + modX), (float) (minY + modY), (float) (minZ + modZ)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
                builderLines.vertex(matrix4f, (float) (maxX + modX), (float) (maxY + modY), (float) (maxZ + modZ)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
            });
            matrix.popPose();
        }
        if (MC.gameMode.isDestroying()) {
            for (BlockPos nextPos : positions) {
                double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
                //TODO 1.18
                int partialDamage = 1;
                //int partialDamage = (int) (ObfuscationReflectionHelper.findField(MultiPlayerGameMode.class, "destroyProgress").getFloat(MC.gameMode) * 10) - 1; // destroyProgress = curBlockDamageMP
                matrix.pushPose();
                matrix.translate(modX, modY, modZ);
                if (partialDamage == -1)
                    return; // Not sure why this happens, but it certainly is an edge-case, if we made it so it returns 0 every time it hit -1, the animation will have a delay
                VertexConsumer builderBreak = new SheetedDecalTextureGenerator(event.getMultiBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(partialDamage)), matrix.last().pose(), matrix.last().normal());
                MC.getBlockRenderer().renderBreakingTexture(world.getBlockState(nextPos), nextPos, world, matrix, builderBreak);
                // MC.getBlockRendererDispatcher().renderModel(world.getBlockState(nextPos), nextPos, world, matrix, builderBreak, ModelDataManager.getModelData(world, nextPos));
                matrix.popPose();
            }
        }
    }

    // Needs some work, won't work in 3rd person also, needs special ItemModel properties
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
        if (!MC.options.renderDebug || MC.hitResult == null || MC.hitResult.getType() != HitResult.Type.BLOCK)
            return;
        Level world = Minecraft.getInstance().level;
        if (world == null) return;
        BlockPos pos = new BlockPos(MC.hitResult.getLocation());
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IInfoProvider) {
            e.getLeft().add("");
            e.getLeft().add(ChatFormatting.AQUA + "[Antimatter Debug Server]");
            e.getLeft().addAll(((IInfoProvider) state.getBlock()).getInfo(new ObjectArrayList<>(), world, state, pos));
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityBase) {
            e.getLeft().addAll(((TileEntityBase) tile).getInfo());
        }
        if (MC.player.isCrouching()) {
            e.getLeft().add("");
            e.getLeft().add(ChatFormatting.AQUA + "[Antimatter Debug Client]");
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent e) {
        if (e.getFlags().isAdvanced() && Ref.SHOW_ITEM_TAGS) {
            Collection<ResourceLocation> tags = ItemTags.getAllTags().getMatchingTags(e.getItemStack().getItem());
            if (!tags.isEmpty()) {
                List<Component> list = e.getToolTip();
                list.add(new TextComponent("Tags:").withStyle(ChatFormatting.DARK_GRAY));
                for (ResourceLocation loc : tags) {
                    list.add(new TextComponent(loc.toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }
    }

    public static double lastDelta;
    @SubscribeEvent
    public static void onGuiMouseScrollPre(ScreenEvent.MouseScrollEvent e) {
        lastDelta = e.getScrollDelta();
    }

    public static boolean leftDown;
    public static boolean rightDown;
    public static boolean middleDown;
    @SubscribeEvent
    public static void onGuiMouseClickPre(ScreenEvent.MouseClickedEvent e) {
        if (e.getButton() == 0) {
            leftDown = true;
        } else if (e.getButton() == 1) {
            rightDown = true;
        } else {
            middleDown = true;
        }
    }

    @SubscribeEvent
    public static void onGuiMouseReleasedPre(ScreenEvent.MouseReleasedEvent e) {
        if (e.getButton() == 0) {
            leftDown = false;
        } else if (e.getButton() == 1) {
            rightDown = false;
        } else {
            middleDown = false;
        }
    }
}
