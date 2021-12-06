package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterDynamics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetHandlerMixin {
    @Final
    @Shadow
    private Connection connection;

    //Because recipes are sent before tags we have to mixin.
    @Inject(method = "handleTagQueryPacket(Lnet/minecraft/network/protocol/game/ClientboundTagQueryPacket;)V", at = @At("TAIL"))
    private void clientRecipesInjection(ClientboundTagQueryPacket p_105120_, CallbackInfo ci) {
        //Since recipe maps are static we don't have to double compile when playing a local server.
        if (!connection.isMemoryConnection()) {
            ClientPacketListener handler = ((ClientPacketListener) (Object) this);
            AntimatterDynamics.onResourceReload(false);
            AntimatterDynamics.onRecipeCompile(false, handler.getRecipeManager(), handler.getTags());
        }
    }
}
