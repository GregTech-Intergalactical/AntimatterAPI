package muramasa.antimatter.mixin.fabric;

import io.github.fabricators_of_create.porting_lib.item.CustomMaxCountItem;
import muramasa.antimatter.item.ItemBattery;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemBattery.class)
public abstract class ItemBatteryMixin implements CustomMaxCountItem {
}
