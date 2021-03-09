package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientBlockUpdateMixin {
    @Inject(remap = false,at = @At("HEAD"), method = "notifyBlockUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V")
    private void onNotifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo info) {
        AntimatterAPI.onNotifyBlockUpdateClient((ClientWorld) (Object)this, pos, oldState, newState);
    }
}
