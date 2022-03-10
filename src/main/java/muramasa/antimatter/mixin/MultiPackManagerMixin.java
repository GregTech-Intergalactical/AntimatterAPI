package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(MultiPackResourceManager.class)
public class MultiPackManagerMixin {

    @ModifyVariable(method = "<init>", at = @At("LOAD"))
    public List<PackResources> modifyArg(List<PackResources> p_203798_) {
        List<PackResources> ret = new java.util.ArrayList<>(p_203798_);
        ret.add(new DynamicResourcePack("Antimatter - Dynamic Data", AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet())));
        return ret;
    }
}
