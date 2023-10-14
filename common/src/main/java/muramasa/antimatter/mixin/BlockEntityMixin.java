package muramasa.antimatter.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import muramasa.antimatter.AntimatterRemapping;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @WrapOperation(method = "loadStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getOptional(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"))
    private static Optional<BlockEntityType<?>> wrapBlockEntityRemap(Registry registry, ResourceLocation id, Operation<Optional<BlockEntityType<?>>> original){
        if (Registry.BLOCK_ENTITY_TYPE.getOptional(id).isPresent()) return original.call(registry, id);
        for (var function : AntimatterRemapping.getBeRemappingFunctionList()) {
            ResourceLocation newId = function.apply(id);
            if (newId != null){
                return original.call(registry, newId);
            }
        }
        return original.call(registry, id);
    }
}
