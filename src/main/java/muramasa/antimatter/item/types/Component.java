package muramasa.antimatter.item.types;

import muramasa.antimatter.item.ItemComponent;
import muramasa.antimatter.machine.Tier;
import net.minecraft.item.Item;

public class Component<T extends Component<T>> extends ItemType<T> {

    public Component(String domain, String id) {
        super(domain, id);
    }

    @Override
    public Item getItem(Tier tier) {
        return new ItemComponent<>(this, tier);
    }
}
