package muramasa.gtu.client.creativetab;

import muramasa.gtu.Ref;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GregTechTab extends CreativeTabs {

    private final String name;
    private ItemStack stack = new ItemStack(Items.IRON_INGOT);

    public GregTechTab(String name) {
        super(Ref.MODID + "." + name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getTabIconItem() {
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void displayAllRelevantItems(final NonNullList<ItemStack> items) {
        super.displayAllRelevantItems(items);
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
