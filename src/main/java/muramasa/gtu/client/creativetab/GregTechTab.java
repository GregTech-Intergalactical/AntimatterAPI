package muramasa.gtu.client.creativetab;

import muramasa.gtu.Ref;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GregTechTab extends CreativeTabs {

    private final String tabName;
    private ItemStack tabStack;

    public GregTechTab(String label, ItemStack stack) {
        super(Ref.MODID + "." + label);
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

    public void setTabStack(ItemStack stack) {
        tabStack = stack;
    }
}
