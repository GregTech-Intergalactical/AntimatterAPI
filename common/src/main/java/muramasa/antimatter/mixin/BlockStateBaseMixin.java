package muramasa.antimatter.mixin;

import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.pipe.BlockPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/SupportType;)Z", at = @At("HEAD"), cancellable = true)
    private void injectIsFaceSturdy(BlockGetter level, BlockPos pos, Direction face, SupportType supportType, CallbackInfoReturnable<Boolean> cir){
        if (this.getBlock() instanceof BlockPipe<?> && supportType == SupportType.FULL){
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BlockEntityPipe<?> pipe){
                if (pipe.coverHandler.map(c -> !c.get(face).isEmpty()).orElse(false)){
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
