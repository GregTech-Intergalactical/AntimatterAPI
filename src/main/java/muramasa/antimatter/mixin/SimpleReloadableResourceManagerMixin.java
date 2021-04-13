package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Collectors;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin {
    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/resources/ResourcePackType;)V")
    private void ctorInject(ResourcePackType type, CallbackInfo info) {
        SimpleReloadableResourceManager manager = ((SimpleReloadableResourceManager)(Object)this);
        if (type == ResourcePackType.SERVER_DATA) {
            manager.addReloadListener(CommonHandler.getListener());
        }
    }

    @Inject(/*remap = false,*/ at = @At("RETURN"), method = "Lnet/minecraft/resources/SimpleReloadableResourceManager;clearResourceNamespaces()V")
    private void clearNamespaceInject(CallbackInfo info) {
        SimpleReloadableResourceManager manager = ((SimpleReloadableResourceManager)(Object)this);
        manager.addResourcePack(new DynamicResourcePack("Antimatter - Dynamic Data", AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet())));
    }
}
