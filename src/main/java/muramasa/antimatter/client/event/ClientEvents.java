package muramasa.antimatter.client.event;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.block.IInfoProvider;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.MaterialTool;
import muramasa.antimatter.tool.behaviour.BehaviourAOEBreak;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

@Mod.EventBusSubscriber(modid = Ref.ID, value = Dist.CLIENT)
public class ClientEvents {

    private static Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onBlockHighlight(DrawHighlightEvent.HighlightBlock event) throws IllegalAccessException {
        PlayerEntity player = MC.player;
        if (player.isCrouching()) return;
        World world = player.getEntityWorld();
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof IAntimatterTool)) return;
        IAntimatterTool item = (IAntimatterTool) stack.getItem();
        AntimatterToolType type = item.getType();
        IBehaviour<MaterialTool> behaviour = type.getBehaviour("aoe_break");
        if (!(behaviour instanceof BehaviourAOEBreak)) return;
        BehaviourAOEBreak aoeBreakBehaviour = (BehaviourAOEBreak) behaviour;

        BlockPos currentPos = event.getTarget().getPos();
        BlockState state = world.getBlockState(currentPos);
        if (state.isAir(world, currentPos) || !Utils.isToolEffective(type, state)) return;

        Vec3d viewPosition = event.getInfo().getProjectedView();
        Entity entity = event.getInfo().getRenderViewEntity();
        IVertexBuilder builderLines = event.getBuffers().getBuffer(RenderType.LINES);
        MatrixStack matrix = event.getMatrix();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        ImmutableSet<BlockPos> positions =  Utils.getHarvestableBlocksToBreak(world, player, item, aoeBreakBehaviour.getColumn(), aoeBreakBehaviour.getRow(), aoeBreakBehaviour.getDepth());
        for (BlockPos nextPos : positions) {
            double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
            VoxelShape shape = world.getBlockState(nextPos).getShape(world, nextPos, ISelectionContext.forEntity(entity));
            Matrix4f matrix4f = matrix.getLast().getMatrix();
            matrix.push();
            shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                builderLines.pos(matrix4f, (float) (minX + modX), (float) (minY + modY), (float) (minZ + modZ)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
                builderLines.pos(matrix4f, (float) (maxX + modX), (float) (maxY + modY), (float) (maxZ + modZ)).color(0.0F, 0.0F, 0.0F, 0.4F).endVertex();
            });
            matrix.pop();
        }
        if (MC.playerController.getIsHittingBlock()) {
            for (BlockPos nextPos : positions) {
                double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
                int partialDamage = (int) (ObfuscationReflectionHelper.findField(PlayerController.class, "field_78770_f").getFloat(MC.playerController) * 10) - 1; // field_78770_f = curBlockDamageMP
                matrix.push();
                matrix.translate(modX, modY, modZ);
                if (partialDamage == -1)
                    return; // Not sure why this happens, but it certainly is an edge-case, if we made it so it returns 0 every time it hit -1, the animation will have a delay
                IVertexBuilder builderBreak = new MatrixApplyingVertexBuilder(event.getBuffers().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(partialDamage)), matrix.getLast());
                MC.getBlockRendererDispatcher().renderBlockDamage(world.getBlockState(nextPos), nextPos, world, matrix, builderBreak);
                // MC.getBlockRendererDispatcher().renderModel(world.getBlockState(nextPos), nextPos, world, matrix, builderBreak, ModelDataManager.getModelData(world, nextPos));
                matrix.pop();
            }
        }
    }

    // Needs some work, won't work in 3rd person also, needs special ItemModel properties
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            PlayerEntity player = e.player;
            if (player == null || player.getHeldItemMainhand().isEmpty()) return;
            ItemStack stack = player.getHeldItemMainhand();
            if (!(stack.getItem() instanceof IAntimatterTool)) return;
            IAntimatterTool item = (IAntimatterTool) stack.getItem();
            if (item.getType().getUseAction() != UseAction.NONE && player.isSwingInProgress) {
                item.asItem().onUsingTick(stack, player, stack.getCount());
                player.swingProgress = player.prevSwingProgress;
            }
        }
    }

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
