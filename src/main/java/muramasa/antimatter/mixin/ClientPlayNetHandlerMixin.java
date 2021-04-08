package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.STagsListPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {
    //Because recipes are sent before tags we have to mixin.
    @Inject(method = "Lnet/minecraft/client/network/play/ClientPlayNetHandler;handleTags(Lnet/minecraft/network/play/server/STagsListPacket;)V", at = @At("TAIL"))
    private void clientRecipesInjection(STagsListPacket packetIn, CallbackInfo info) {
        AntimatterAPI.onRecipeCompile(((ClientPlayNetHandler)(Object)this).getRecipeManager(), Item::getTags);
    }
}
