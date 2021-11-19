package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerBlockUpdateMixin {
    @Inject(at = @At("HEAD"), method = "sendBlockUpdated(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V")
    private void onNotifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo info) {
        AntimatterAPI.onNotifyBlockUpdate((World) (Object) this, pos, oldState, newState, flags);
    }
}
