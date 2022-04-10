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

@Mixin(ReloadableServerResources.class)
public class SimpleReloadInstanceMixin {
    
    @Inject(method = "loadResources", at = @At(value = "HEAD"))
    private static void create(ResourceManager p_206862_, RegistryAccess.Frozen p_206863_, Commands.CommandSelection p_206864_, int p_206865_, Executor p_206866_, Executor p_206867_, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> ri) {
        AntimatterDynamics.onResourceReload(true, p_206864_ != Commands.CommandSelection.INTEGRATED);
    }


    //Since tag event doesn't include recipe manager.
    @Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At(value = "TAIL"))
    public void onUpdateTags(RegistryAccess p_206869, CallbackInfo info) {
        ReloadableServerResources rs = (ReloadableServerResources) (Object) this;
        AntimatterDynamics.onRecipeCompile(true, rs.getRecipeManager());
    }
}
