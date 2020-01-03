package muramasa.antimatter.client.itemgroup;

import muramasa.gtu.Ref;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class AntimatterItemGroup extends ItemGroup {

    private String name;
    private ItemStack icon;

    public AntimatterItemGroup(String name, ItemStack icon) {
        super(Ref.MODID + "." + name);
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
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
