package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.capability.forge.AntimatterCaps;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;

@Debug(export = true)
@Mixin(value = CapabilityManager.class, remap = false)
public class CapabilityManagerMixin {
    @Shadow @Final private IdentityHashMap<String, Capability<?>> providers;

    @Inject(method = "get(Ljava/lang/String;Z)Lnet/minecraftforge/common/capabilities/Capability;", at = @At(value = "INVOKE", target = "Ljava/util/IdentityHashMap;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;", shift = At.Shift.AFTER), remap = false)
    private void injectCaps(String realName, boolean registering, CallbackInfoReturnable<Capability<?>> info){
        try {
            AntimatterCaps.CAP_MAP.putIfAbsent(Class.forName(realName.replace("/", ".")), providers.get(realName));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
