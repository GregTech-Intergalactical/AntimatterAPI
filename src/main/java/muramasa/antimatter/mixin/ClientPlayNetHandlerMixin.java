package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterDynamics;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.STagsListPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {
    @Final
    @Shadow
    private NetworkManager connection;

    //Because recipes are sent before tags we have to mixin.
    @Inject(method = "handleUpdateTags(Lnet/minecraft/network/play/server/STagsListPacket;)V", at = @At("TAIL"))
    private void clientRecipesInjection(STagsListPacket packetIn, CallbackInfo info) {
        //Since recipe maps are static we don't have to double compile when playing a local server.
        if (!connection.isMemoryConnection()) {
            ClientPlayNetHandler handler = ((ClientPlayNetHandler) (Object) this);
            AntimatterDynamics.onResourceReload(false);
            AntimatterDynamics.onRecipeCompile(false, handler.getRecipeManager(), handler.getTags());
        }
    }
}
