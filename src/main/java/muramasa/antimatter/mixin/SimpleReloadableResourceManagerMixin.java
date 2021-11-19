package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin {

    @Shadow
    @Final
    private ResourcePackType type;

    @Inject(method = "createReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resources/IAsyncReloader;", at = @At("HEAD"))
    private void callback(Executor backgroundExecutor, Executor gameExecutor, List<IFutureReloadListener> listeners, CompletableFuture<Unit> waitingFor, CallbackInfoReturnable<IAsyncReloader> cir) {
        if (type == ResourcePackType.SERVER_DATA) {
            AntimatterDynamics.onResourceReload(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/resources/SimpleReloadableResourceManager;clear()V")
    private void clearNamespaceInject(CallbackInfo info) {
        SimpleReloadableResourceManager manager = ((SimpleReloadableResourceManager) (Object) this);
        //if (type == ResourcePackType.SERVER_DATA)
        manager.add(new DynamicResourcePack("Antimatter - Dynamic Data", AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet())));
    }
}
