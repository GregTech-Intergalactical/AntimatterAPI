package muramasa.antimatter.client.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class AntimatterItemGroup extends ItemGroup {

    private String namespace, id;
    private ItemStack icon = ItemStack.EMPTY;

    public AntimatterItemGroup(String namespace, String id, ItemStack icon) {
        super(namespace + "." + id);
        this.namespace = namespace;
        this.id = id;
        this.icon = icon;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return id;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public ItemStack createIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
}
