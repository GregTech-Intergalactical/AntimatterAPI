package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import io.github.feltmc.feltapi.api.item.extensions.EnchantabilityItem;
import muramasa.antimatter.tool.MaterialSword;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MaterialSword.class)
public class MaterialSwordMixin implements EnchantabilityItem, DamageableItem {
}
