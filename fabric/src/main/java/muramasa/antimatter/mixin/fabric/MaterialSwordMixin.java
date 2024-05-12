package muramasa.antimatter.mixin.fabric;


import io.github.fabricators_of_create.porting_lib.item.DamageableItem;
import muramasa.antimatter.tool.MaterialSword;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialSword.class, remap = false)
public abstract class MaterialSwordMixin implements DamageableItem {
}
