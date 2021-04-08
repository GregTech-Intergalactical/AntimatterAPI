package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.STagsListPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {
    @Shadow
    private NetworkManager netManager;
    //Because recipes are sent before tags we have to mixin.
    @Inject(method = "Lnet/minecraft/client/network/play/ClientPlayNetHandler;handleTags(Lnet/minecraft/network/play/server/STagsListPacket;)V", at = @At("TAIL"))
    private void clientRecipesInjection(STagsListPacket packetIn, CallbackInfo info) {
        //Since recipe maps are static we don't have to double compile when playing a local server.
        if (!netManager.isLocalChannel())
            AntimatterAPI.onRecipeCompile(((ClientPlayNetHandler)(Object)this).getRecipeManager(), Item::getTags);
    }
}
