package muramasa.antimatter.tool.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.capability.ICoverHandlerProvider;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BehaviourExtendedHighlight implements IItemHighlight<IAntimatterTool> {

    final float INDENTATION_SIDE = 0.25F;
    final double INTERACT_DISTANCE = 5;

    protected Function<Block, Boolean> validator;
    protected BiFunction<Direction, BlockEntity, Boolean> function;

    public final static BiFunction<Direction, BlockEntity, Boolean> COVER_FUNCTION = (dir, tile) -> {
        if (tile instanceof ICoverHandlerProvider<?> base) {
            return base.getCoverHandler().map(t -> !t.get(dir).isEmpty()).orElse(false);
        }
        return false;
    };

    public static final List<BiFunction<Direction, BlockEntity, Boolean>> EXTRA_PIPE_FUNCTIONS = new ArrayList<>();

    public final static BiFunction<Direction, BlockEntity, Boolean> PIPE_FUNCTION = (dir, tile) -> {
        for (var extraPipeFunction : EXTRA_PIPE_FUNCTIONS) {
            if (extraPipeFunction.apply(dir, tile)) return true;
        }
        if (tile instanceof BlockEntityPipe<?> pipe) {
            return (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCrouching()) ? pipe.canConnectVirtual(dir.get3DDataValue()) : pipe.canConnect(dir.get3DDataValue());
        }
        if (tile instanceof BlockEntityMachine<?> machine) {
            Direction direction = machine.getOutputFacing();
            if ((Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCrouching()) || machine.getMachineType().getOutputCover() == ICover.emptyFactory)
                direction = machine.getFacing();
            return direction != null && direction == dir;
        }
        if (tile instanceof HopperBlockEntity hopperBlockEntity){
            if (dir != Direction.UP){
                return hopperBlockEntity.getBlockState().getValue(BlockStateProperties.FACING_HOPPER) == dir;
            }
        }
        if(tile instanceof ChestBlockEntity chestBlockEntity){
            if (dir.getAxis().isHorizontal()){
                return chestBlockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) == dir;
            }
        }
        return false;
    };


    public BehaviourExtendedHighlight(Function<Block, Boolean> validator, BiFunction<Direction, BlockEntity, Boolean> builder) {
        this.validator = validator;
        this.function = builder;
    }

    @Override
    public String getId() {
        return "extended_highlight";
    }

    @Override
    public InteractionResult onDrawHighlight(Player player, LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        return RenderHelper.onDrawHighlight(player, levelRenderer, camera, target, partialTicks, poseStack, multiBufferSource, validator, function);
    }
}
