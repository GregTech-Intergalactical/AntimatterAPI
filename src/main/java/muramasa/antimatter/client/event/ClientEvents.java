package muramasa.antimatter.client.event;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
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
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = Ref.ID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onBlockHighlight(DrawHighlightEvent.HighlightBlock event) throws IllegalAccessException {
        PlayerEntity player = MC.player;
        World world = player.getCommandSenderWorld();
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
        ActionResultType res = item.onGenericHighlight(player, event);
        if (res.shouldSwing()) {
            return;
        }
        IBehaviour<IAntimatterTool> behaviour = type.getBehaviour("aoe_break");
        if (!(behaviour instanceof BehaviourAOEBreak)) return;
        BehaviourAOEBreak aoeBreakBehaviour = (BehaviourAOEBreak) behaviour;

        BlockPos currentPos = event.getTarget().getBlockPos();
        BlockState state = world.getBlockState(currentPos);
        if (state.isAir(world, currentPos) || !Utils.isToolEffective(item, state)) return;
        Vector3d viewPosition = event.getInfo().getPosition();
        Entity entity = event.getInfo().getEntity();
        IVertexBuilder builderLines = event.getBuffers().getBuffer(RenderType.LINES);
        MatrixStack matrix = event.getMatrix();
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        ImmutableSet<BlockPos> positions = Utils.getHarvestableBlocksToBreak(world, player, item, aoeBreakBehaviour.getColumn(), aoeBreakBehaviour.getRow(), aoeBreakBehaviour.getDepth());
        for (BlockPos nextPos : positions) {
            double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
            VoxelShape shape = world.getBlockState(nextPos).getShape(world, nextPos, ISelectionContext.of(entity));
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
                int partialDamage = (int) (ObfuscationReflectionHelper.findField(PlayerController.class, "destroyProgress").getFloat(MC.gameMode) * 10) - 1; // destroyProgress = curBlockDamageMP
                matrix.pushPose();
                matrix.translate(modX, modY, modZ);
                if (partialDamage == -1)
                    return; // Not sure why this happens, but it certainly is an edge-case, if we made it so it returns 0 every time it hit -1, the animation will have a delay
                IVertexBuilder builderBreak = new MatrixApplyingVertexBuilder(event.getBuffers().getBuffer(ModelBakery.DESTROY_TYPES.get(partialDamage)), matrix.last().pose(), matrix.last().normal());
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
            PlayerEntity player = e.player;
            if (player == null || player.getMainHandItem().isEmpty()) return;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof IAntimatterTool)) return;
            IAntimatterTool item = (IAntimatterTool) stack.getItem();
            if (item.getAntimatterToolType().getUseAction() != UseAction.NONE && player.swinging) {
                item.getItem().onUsingTick(stack, player, stack.getCount());
                //player.swingProgress = player.prevSwingProgress;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderDebugInfo(RenderGameOverlayEvent.Text e) {
        if (!MC.options.renderDebug || MC.hitResult == null || MC.hitResult.getType() != RayTraceResult.Type.BLOCK)
            return;
        World world = Minecraft.getInstance().level;
        if (world == null) return;
        BlockPos pos = new BlockPos(MC.hitResult.getLocation());
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IInfoProvider) {
            e.getLeft().add("");
            e.getLeft().add(TextFormatting.AQUA + "[Antimatter Debug Server]");
            e.getLeft().addAll(((IInfoProvider) state.getBlock()).getInfo(new ObjectArrayList<>(), world, state, pos));
        }
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityBase) {
            e.getLeft().addAll(((TileEntityBase) tile).getInfo());
        }
        if (MC.player.isCrouching()) {
            //TODO
            e.getLeft().add("");
            e.getLeft().add(TextFormatting.AQUA + "[Antimatter Debug Client]");
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent e) {
        if (e.getFlags().isAdvanced() && Ref.SHOW_ITEM_TAGS) {
            Collection<ResourceLocation> tags = ItemTags.getAllTags().getMatchingTags(e.getItemStack().getItem());
            if (!tags.isEmpty()) {
                List<ITextComponent> list = e.getToolTip();
                list.add(new StringTextComponent("Tags:").withStyle(TextFormatting.DARK_GRAY));
                for (ResourceLocation loc : tags) {
                    list.add(new StringTextComponent(loc.toString()).withStyle(TextFormatting.DARK_GRAY));
                }
            }
        }
    }

    public static double lastDelta;
    @SubscribeEvent
    public static void onGuiMouseScrollPre(GuiScreenEvent.MouseScrollEvent.Pre e) {
        lastDelta = e.getScrollDelta();
    }

    public static boolean leftDown;
    public static boolean rightDown;
    public static boolean middleDown;
    @SubscribeEvent
    public static void onGuiMouseClickPre(GuiScreenEvent.MouseClickedEvent.Pre e) {
        if (e.getButton() == 0) {
            leftDown = true;
        } else if (e.getButton() == 1) {
            rightDown = true;
        } else {
            middleDown = true;
        }
    }

    @SubscribeEvent
    public static void onGuiMouseReleasedPre(GuiScreenEvent.MouseReleasedEvent.Pre e) {
        if (e.getButton() == 0) {
            leftDown = false;
        } else if (e.getButton() == 1) {
            rightDown = false;
        } else {
            middleDown = false;
        }
    }
}
