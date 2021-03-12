package muramasa.antimatter.mixin;

import muramasa.antimatter.Ref;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Main.class)
public class ServerDynamicPackMixin {

    @ModifyArg(/*remap = false,*/method = "Lnet/minecraft/server/Main;main([Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;func_240772_a_(Lnet/minecraft/resources/ResourcePackList;Lnet/minecraft/util/datafix/codec/DatapackCodec;Z)Lnet/minecraft/util/datafix/codec/DatapackCodec;"), index = 0)
    private static ResourcePackList p(ResourcePackList y) {
        y.addPackFinder(Ref.SERVER_PACK_FINDER);
        return y;
    }
}
