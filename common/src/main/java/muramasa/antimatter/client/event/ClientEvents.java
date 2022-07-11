package muramasa.antimatter.client.event;

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
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.mixin.client.LevelRendererAccessor;
import muramasa.antimatter.mixin.client.MultiPlayerGameModeAccessor;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.behaviour.BehaviourAOEBreak;
import muramasa.antimatter.tool.behaviour.BehaviourExtendedHighlight;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Environment(EnvType.CLIENT)
public class ClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();



    public static boolean onBlockHighlight(LevelRenderer levelRenderer, Camera camera, BlockHitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) throws IllegalAccessException {
        Player player = MC.player;
        Level world = player.getCommandSenderWorld();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || (!(stack.getItem() instanceof IAntimatterTool) && !(stack.getItem() instanceof IHaveCover)))
            return false;
        if (stack.getItem() instanceof IHaveCover) {
            if (player.isCrouching()) return false;
            RenderHelper.onDrawHighlight(player, levelRenderer, camera, target, partialTick, poseStack, bufferSource, b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION);
            return true;
        }
        IAntimatterTool item = (IAntimatterTool) stack.getItem();
        AntimatterToolType type = item.getAntimatterToolType();
        if (player.isCrouching() && type != Data.WRENCH && type != Data.ELECTRIC_WRENCH && type != Data.CROWBAR && type != Data.WIRE_CUTTER)
            return false;
        //Perform highlight of wrench
        InteractionResult res = item.onGenericHighlight(player, levelRenderer, camera, target, partialTick, poseStack, bufferSource);
        if (res == InteractionResult.FAIL) {
            return true;
        }
        if (res.shouldSwing()) {
            return false;
        }
        IBehaviour<IAntimatterTool> behaviour = type.getBehaviour("aoe_break");
        if (!(behaviour instanceof BehaviourAOEBreak)) return false;
        BehaviourAOEBreak aoeBreakBehaviour = (BehaviourAOEBreak) behaviour;

        BlockPos currentPos = target.getBlockPos();
        BlockState state = world.getBlockState(currentPos);
        if (state.isAir() || !Utils.isToolEffective(item, state)) return false;
        Vec3 viewPosition = camera.getPosition();
        Entity entity = camera.getEntity();
        VertexConsumer builderLines = bufferSource.getBuffer(RenderType.LINES);
        double viewX = viewPosition.x, viewY = viewPosition.y, viewZ = viewPosition.z;
        ImmutableSet<BlockPos> positions = Utils.getHarvestableBlocksToBreak(world, player, item, aoeBreakBehaviour.getColumn(), aoeBreakBehaviour.getRow(), aoeBreakBehaviour.getDepth());
        for (BlockPos nextPos : positions) {
            double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
            VoxelShape shape = world.getBlockState(nextPos).getShape(world, nextPos, CollisionContext.of(entity));
            poseStack.pushPose();
            LevelRendererAccessor.renderShape(poseStack, builderLines, shape, modX, modY, modZ, 0,0,0,0.4f);
            poseStack.popPose();
        }
        if (MC.gameMode.isDestroying()) {
            for (BlockPos nextPos : positions) {
                double modX = nextPos.getX() - viewX, modY = nextPos.getY() - viewY, modZ = nextPos.getZ() - viewZ;
                //TODO 1.18
                //int partialDamage = 1;
                int partialDamage = (int) (((MultiPlayerGameModeAccessor)MC.gameMode).getDestroyProgress() * 10) - 1; // destroyProgress = curBlockDamageMP
                poseStack.pushPose();
                poseStack.translate(modX, modY, modZ);
                if (partialDamage == -1)
                    return false; // Not sure why this happens, but it certainly is an edge-case, if we made it so it returns 0 every time it hit -1, the animation will have a delay
                VertexConsumer builderBreak = new SheetedDecalTextureGenerator(bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(partialDamage)), poseStack.last().pose(), poseStack.last().normal());
                MC.getBlockRenderer().renderBreakingTexture(world.getBlockState(nextPos), nextPos, world, poseStack, builderBreak);
                // MC.getBlockRendererDispatcher().renderModel(world.getBlockState(nextPos), nextPos, world, matrix, builderBreak, ModelDataManager.getModelData(world, nextPos));
                poseStack.popPose();
            }
        }
        return false;
    }

    // Needs some work, won't work in 3rd person also, needs special ItemModel properties
    public static void onPlayerTickEnd(Player player) {
        if (player == null || player.getMainHandItem().isEmpty()) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IAntimatterTool)) return;
        IAntimatterTool item = (IAntimatterTool) stack.getItem();
        if (item.getAntimatterToolType().getUseAction() != UseAnim.NONE && player.swinging) {
            //todo abstract this
            //item.getItem().onUsingTick(stack, player, stack.getCount());
            //player.swingProgress = player.prevSwingProgress;
        }
    }

    public static void onRenderDebugInfo(ArrayList<String> left) {
        if (!MC.options.renderDebug || MC.hitResult == null || MC.hitResult.getType() != HitResult.Type.BLOCK)
            return;
        Level world = Minecraft.getInstance().level;
        if (world == null) return;
        BlockPos pos = new BlockPos(MC.hitResult.getLocation());
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IInfoProvider info) {
            left.add("");
            left.add(ChatFormatting.AQUA + "[Antimatter Debug Server]");
            left.addAll(info.getInfo(new ObjectArrayList<>(), world, state, pos));
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityBase<?> b) {
            left.addAll(b.getInfo());
        }
        if (MC.player.isCrouching()) {
            left.add("");
            left.add(ChatFormatting.AQUA + "[Antimatter Debug Client]");
        }
    }

    //TODO still needed?
    public static void onItemTooltip(TooltipFlag flags, List<Component> tooltip) {
        if (flags.isAdvanced() && Ref.SHOW_ITEM_TAGS) {
            Collection<ResourceLocation> tags = Collections.emptyList(); //ItemTags.getAllTags().getMatchingTags(e.getItemStack().getItem());
            if (!tags.isEmpty()) {
                tooltip.add(new TextComponent("Tags:").withStyle(ChatFormatting.DARK_GRAY));
                for (ResourceLocation loc : tags) {
                    tooltip.add(new TextComponent(loc.toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }
    }

    public static double lastDelta;
    public static void onGuiMouseScrollPre(double lastScrollDelta) {
        lastDelta = lastScrollDelta;
    }

    public static boolean leftDown;
    public static boolean rightDown;
    public static boolean middleDown;
    public static void onGuiMouseClickPre(int button) {
        switch (button){
            case 0 -> leftDown = true;
            case 1 -> rightDown = true;
            case 2 -> middleDown = true;
        }
    }

    public static void onGuiMouseReleasedPre(int button) {
        switch (button){
            case 0 -> leftDown = false;
            case 1 -> rightDown = false;
            case 2 -> middleDown = false;
        }
    }
}
