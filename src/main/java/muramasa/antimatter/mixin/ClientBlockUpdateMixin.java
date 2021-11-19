package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientWorld.class)
public class ClientBlockUpdateMixin {
    @Inject(at = @At("HEAD"), method = "sendBlockUpdated(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;I)V")
    private void onNotifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo info) {
        AntimatterAPI.onNotifyBlockUpdate((World) (Object) this, pos, oldState, newState, flags);
    }
}
