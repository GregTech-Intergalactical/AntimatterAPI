package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.item.ReequipAnimationItem;
import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import io.github.fabricators_of_create.porting_lib.util.ShieldBlockItem;
import muramasa.antimatter.tool.MaterialTool;
import net.feltmc.feltapi.api.enchanting.EnchantabilityItem;
import net.feltmc.feltapi.api.playeritem.SneakBypassUseItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialTool.class, remap = false)
public abstract class MaterialToolMixin implements DamageableItem, EnchantabilityItem, ReequipAnimationItem, ShieldBlockItem, SneakBypassUseItem {
}
