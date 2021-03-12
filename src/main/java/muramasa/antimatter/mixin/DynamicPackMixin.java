package muramasa.antimatter.mixin;

import muramasa.antimatter.Ref;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@OnlyIn(Dist.CLIENT)
@Mixin(Minecraft.class)
public class DynamicPackMixin  {
    @ModifyArg(/*remap = false,*/method = "reloadDatapacks(Lnet/minecraft/util/registry/DynamicRegistries$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/world/storage/SaveFormat$LevelSave;)Lnet/minecraft/client/Minecraft$PackManager;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;func_240772_a_(Lnet/minecraft/resources/ResourcePackList;Lnet/minecraft/util/datafix/codec/DatapackCodec;Z)Lnet/minecraft/util/datafix/codec/DatapackCodec;"), index = 0)
    private ResourcePackList reloadDataPacks(ResourcePackList y) {
        y.addPackFinder(Ref.SERVER_PACK_FINDER);
        return y;
    }
}
