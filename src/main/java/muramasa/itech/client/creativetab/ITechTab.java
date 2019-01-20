package muramasa.itech.client.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ITechTab extends CreativeTabs {

    private final String tabName;
    private final ItemStack tabStack;

    public ITechTab(String label, ItemStack stack) {
        super("itech" + label);
        tabName = label;
        tabStack = stack;
    }

    public String getTabName() {
        return tabName;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getTabIconItem() {
        return tabStack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void displayAllRelevantItems(final NonNullList<ItemStack> items) {
        super.displayAllRelevantItems(items);
    }
}
