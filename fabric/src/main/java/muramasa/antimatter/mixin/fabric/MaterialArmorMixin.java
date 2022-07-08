package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.util.ArmorTextureItem;
import muramasa.antimatter.tool.armor.MaterialArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MaterialArmor.class)
public abstract class MaterialArmorMixin implements ArmorTextureItem {
}
