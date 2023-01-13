package muramasa.antimatter.mixin;

import com.google.common.collect.ImmutableList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.AntimatterDynamics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

@Debug(export = true)
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = {"method_29437", "lambda$reloadResources$18", "m_212917_"}, at = @At(value = "HEAD"))
    private void injectIntoReload(RegistryAccess.Frozen frozen, ImmutableList reloadableResources, CallbackInfoReturnable<CompletionStage<?>> ci){
        AntimatterDynamics.onResourceReload(AntimatterAPI.getSIDE().isServer());
    }
}
