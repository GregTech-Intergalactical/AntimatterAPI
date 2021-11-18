package muramasa.antimatter.client.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class AntimatterItemGroup extends ItemGroup {

    protected String domain, id;
    protected Supplier<ItemStack> iconSupplier;


    public AntimatterItemGroup(String domain, String id, Supplier<ItemStack> iconSupplier) {
        super(domain + "." + id);
        this.domain = domain;
        this.id = id;
        this.iconSupplier = iconSupplier;
    }

    public String getDomain() {
        return domain;
    }

    public String getGroupId() {
        return id;
    }

    @Override
    public ItemStack makeIcon() {
        return iconSupplier.get();
    }
}
