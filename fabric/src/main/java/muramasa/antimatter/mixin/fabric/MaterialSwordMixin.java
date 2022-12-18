package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import io.github.feltmc.feltapi.api.item.extensions.EnchantabilityItem;
import io.github.feltmc.feltapi.api.item.extensions.SneakBypassUseItem;
import muramasa.antimatter.tool.MaterialSword;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialSword.class, remap = false)
public class MaterialSwordMixin implements EnchantabilityItem, DamageableItem, SneakBypassUseItem {
}
