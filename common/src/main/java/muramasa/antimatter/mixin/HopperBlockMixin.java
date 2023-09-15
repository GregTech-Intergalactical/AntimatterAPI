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
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public class HopperBlockMixin {

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
            if (side != Direction.UP){
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.FACING_HOPPER, side));
                Utils.damageStack(player.getItemInHand(hand), hand, player);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
