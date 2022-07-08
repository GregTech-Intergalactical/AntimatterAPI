package muramasa.antimatter.mixin.fabric;

import io.github.feltmc.feltapi.api.item.extensions.ShareTagItem;
import muramasa.antimatter.item.ItemBattery;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemBattery.class)
public class ItemBatteryMixin implements ShareTagItem {
}
