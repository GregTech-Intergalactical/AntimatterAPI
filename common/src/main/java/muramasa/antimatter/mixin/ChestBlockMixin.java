package muramasa.antimatter.mixin;

import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> {
    @Shadow
    @Final
    public static EnumProperty<ChestType> TYPE;

    protected ChestBlockMixin(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityFactory) {
        super(properties, blockEntityFactory);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void injectShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir){
        if (context instanceof EntityCollisionContext collisionContext && collisionContext.getEntity() instanceof Player player){
            if (Utils.isPlayerHolding(player, InteractionHand.MAIN_HAND, AntimatterDefaultTools.WRENCH)){
                cir.setReturnValue(Shapes.block());
            }
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injectUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        if (!level.isClientSide && Utils.isPlayerHolding(player, hand, AntimatterDefaultTools.WRENCH)){
            Direction side = Utils.getInteractSide(hit);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (side.getAxis().isHorizontal()){
                if (state.getValue(TYPE) == ChestType.SINGLE) {
                    if (side == facing.getCounterClockWise()){
                        BlockState other = level.getBlockState(pos.relative(facing.getCounterClockWise()));
                        if (other.getBlock() == this && other.hasProperty(TYPE) && other.getValue(TYPE) == ChestType.SINGLE && other.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing){
                            level.setBlockAndUpdate(pos, state.setValue(TYPE, ChestType.RIGHT));
                            level.setBlockAndUpdate(pos.relative(facing.getCounterClockWise()), other.setValue(TYPE, ChestType.LEFT));
                            Utils.damageStack(player.getItemInHand(hand), hand, player);
                            cir.setReturnValue(InteractionResult.SUCCESS);
                            return;
                        }
                    }
                    if (side == facing.getClockWise()){
                        BlockState other = level.getBlockState(pos.relative(facing.getClockWise()));
                        if (other.getBlock() == this && other.hasProperty(TYPE) && other.getValue(TYPE) == ChestType.SINGLE && other.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing){
                            level.setBlockAndUpdate(pos, state.setValue(TYPE, ChestType.LEFT));
                            level.setBlockAndUpdate(pos.relative(facing.getClockWise()), other.setValue(TYPE, ChestType.RIGHT));
                            Utils.damageStack(player.getItemInHand(hand), hand, player);
                            cir.setReturnValue(InteractionResult.SUCCESS);
                            return;
                        }
                    }
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.HORIZONTAL_FACING, side));
                    Utils.damageStack(player.getItemInHand(hand), hand, player);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else if (state.getValue(TYPE) == ChestType.LEFT) {
                    if (side == facing.getClockWise()){
                        BlockState other = level.getBlockState(pos.relative(facing.getClockWise()));
                        if (other.getBlock() == this && other.hasProperty(TYPE) && other.getValue(TYPE) == ChestType.RIGHT && other.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing){
                            level.setBlockAndUpdate(pos, state.setValue(TYPE, ChestType.SINGLE));
                            level.setBlockAndUpdate(pos.relative(facing.getClockWise()), other.setValue(TYPE, ChestType.SINGLE));
                            Utils.damageStack(player.getItemInHand(hand), hand, player);
                            cir.setReturnValue(InteractionResult.SUCCESS);
                        }
                    }
                } else if (state.getValue(TYPE) == ChestType.RIGHT) {
                    if (side == facing.getCounterClockWise()){
                        BlockState other = level.getBlockState(pos.relative(facing.getCounterClockWise()));
                        if (other.getBlock() == this && other.hasProperty(TYPE) && other.getValue(TYPE) == ChestType.LEFT && other.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing){
                            level.setBlockAndUpdate(pos, state.setValue(TYPE, ChestType.SINGLE));
                            level.setBlockAndUpdate(pos.relative(facing.getCounterClockWise()), other.setValue(TYPE, ChestType.SINGLE));
                            Utils.damageStack(player.getItemInHand(hand), hand, player);
                            cir.setReturnValue(InteractionResult.SUCCESS);
                        }
                    }
                }
            }
        }
    }
}
