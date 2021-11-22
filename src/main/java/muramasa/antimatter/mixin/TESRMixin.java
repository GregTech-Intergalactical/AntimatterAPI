package muramasa.antimatter.mixin;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityRendererDispatcher.class)
public class TESRMixin {

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <E extends TileEntity> void inject(E tile, CallbackInfoReturnable<TileEntityRenderer<E>> ret) {
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine<?> machine = (TileEntityMachine<?>) tile;
            if (machine.getMachineType().renderAsTesr()) {
                return;
            }
            ret.setReturnValue(null);
        }
    }
}
