package muramasa.antimatter.mixin.client;

import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class TESRMixin {

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void inject(E tile, CallbackInfoReturnable<BlockEntityRenderer<E>> ret) {
        if (tile instanceof BlockEntityMachine) {
            BlockEntityMachine<?> machine = (BlockEntityMachine<?>) tile;
            if (machine.getMachineType().renderAsTesr()) {
                return;
            }
            ret.setReturnValue(null);
        }
    }
}
