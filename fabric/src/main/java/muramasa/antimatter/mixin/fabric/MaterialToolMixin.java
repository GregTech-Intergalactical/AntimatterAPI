package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.item.ReequipAnimationItem;
import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import io.github.fabricators_of_create.porting_lib.util.ShieldBlockItem;
import io.github.feltmc.feltapi.api.enchanting.EnchantabilityItem;
import io.github.feltmc.feltapi.api.playeritem.SneakBypassUseItem;
import muramasa.antimatter.tool.MaterialTool;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialTool.class, remap = false)
public abstract class MaterialToolMixin implements DamageableItem, EnchantabilityItem, ReequipAnimationItem, ShieldBlockItem, SneakBypassUseItem {
}
