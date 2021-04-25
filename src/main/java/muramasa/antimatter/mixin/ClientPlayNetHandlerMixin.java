package muramasa.antimatter.mixin;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.tags.TagCollectionManager;
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
    private NetworkManager netManager;
    //Because recipes are sent before tags we have to mixin.
    @Inject(/*remap = false,*/ method = "handleTags(Lnet/minecraft/network/play/server/STagsListPacket;)V", at = @At("TAIL"))
    private void clientRecipesInjection(STagsListPacket packetIn, CallbackInfo info) {
        //Since recipe maps are static we don't have to double compile when playing a local server.
        if (!netManager.isLocalChannel()) {
            AntimatterDynamics.onResourceReload(false);
            AntimatterDynamics.onRecipeCompile(((ClientPlayNetHandler)(Object)this).getRecipeManager(), Item::getTags);
        }
    }
}
