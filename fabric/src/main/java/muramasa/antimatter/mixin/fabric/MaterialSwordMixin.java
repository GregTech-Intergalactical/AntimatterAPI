package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import muramasa.antimatter.tool.MaterialSword;
import net.feltmc.feltapi.api.enchanting.EnchantabilityItem;
import net.feltmc.feltapi.api.playeritem.SneakBypassUseItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialSword.class, remap = false)
public abstract class MaterialSwordMixin implements EnchantabilityItem, DamageableItem, SneakBypassUseItem {
}
