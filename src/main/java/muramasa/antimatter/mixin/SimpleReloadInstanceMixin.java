package muramasa.antimatter.mixin;

import com.mojang.datafixers.util.Unit;
import muramasa.antimatter.AntimatterDynamics;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
//Priority > 1000, to ensure we run after KubeJS. Otherwise, KubeJS doesn't init properly.
@Mixin(value = ReloadableServerResources.class, priority = 1001)
public class SimpleReloadInstanceMixin {
    
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void create(RegistryAccess.Frozen frozen, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
        AntimatterDynamics.onResourceReload(true, commandSelection != Commands.CommandSelection.INTEGRATED);
    }


    //Since tag event doesn't include recipe manager.
    @Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At(value = "TAIL"))
    public void onUpdateTags(RegistryAccess p_206869, CallbackInfo info) {
        ReloadableServerResources rs = (ReloadableServerResources) (Object) this;
        AntimatterDynamics.onRecipeCompile(true, rs.getRecipeManager());
    }
}
