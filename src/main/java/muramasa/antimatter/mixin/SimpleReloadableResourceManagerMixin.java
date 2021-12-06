package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
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
    private PackType type;

    @Inject(method = "createReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/server/packs/resources/ReloadInstance;", at = @At("HEAD"))
    private void callback(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<PackResources> p_143950, CallbackInfoReturnable<ReloadInstance> cir) {
        if (type == PackType.SERVER_DATA) {
            AntimatterDynamics.onResourceReload(true);
        }
    }//net.minecraft.server.packs.resources

    @Inject(at = @At("RETURN"), method = "clear()V")
    private void clearNamespaceInject(CallbackInfo info) {
        SimpleReloadableResourceManager manager = ((SimpleReloadableResourceManager) (Object) this);
        //if (type == ResourcePackType.SERVER_DATA)
        manager.add(new DynamicResourcePack("Antimatter - Dynamic Data", AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet())));
    }
}
