package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.item.ArmorTextureItem;
import muramasa.antimatter.tool.armor.MaterialArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MaterialArmor.class, remap = false)
public abstract class MaterialArmorMixin implements ArmorTextureItem {
}
