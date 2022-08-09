package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.item.ReequipAnimationItem;
import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import io.github.fabricators_of_create.porting_lib.util.ShieldBlockItem;
import io.github.feltmc.feltapi.api.item.extensions.EnchantabilityItem;
import io.github.feltmc.feltapi.api.item.extensions.ShareTagItem;
import muramasa.antimatter.tool.MaterialTool;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialTool.class, remap = false)
public class MaterialToolMixin implements DamageableItem, EnchantabilityItem, ReequipAnimationItem, ShieldBlockItem, ShareTagItem {
}
