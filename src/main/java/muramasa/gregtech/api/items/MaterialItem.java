package muramasa.gregtech.api.items;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.Element;
import muramasa.gregtech.api.materials.GTItemStack;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.client.creativetab.GregTechTab;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class MaterialItem extends Item {

    private static LinkedHashMap<String, GTItemStack> STACK_LOOKUP = new LinkedHashMap<>();

    private String material, prefix;

    public MaterialItem(Prefix prefix, Material material) {
        setUnlocalizedName(Ref.MODID + "_item_" + prefix.getName() + "_" + material.getName());
        setRegistryName("item_" + prefix.getName() + "_" + material.getName());
        setCreativeTab(Ref.TAB_MATERIALS);
        this.material = material.getName();
        this.prefix = prefix.getName();
        STACK_LOOKUP.put(prefix.getName() + material.getName(), new GTItemStack(new ItemStack(this), prefix.isVisible()));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("materials")) {
                GTItemStack gtStack = STACK_LOOKUP.get(prefix + material);
                if (gtStack.isVisible()) {
                    items.add(gtStack.get());
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return ((MaterialItem) stack.getItem()).getMaterial().getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Element element = ((MaterialItem) stack.getItem()).getMaterial().getElement();
        if (element != null) {
            tooltip.add(element.getDisplayName());
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        String set = Materials.get(material).getSet().getName();
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":material_set/" + set, set + "=" + prefix));
    }

    public Prefix getPrefix() {
        return Prefix.get(prefix);
    }

    public Material getMaterial() {
        return Materials.get(material);
    }

    public static boolean hasPrefix(ItemStack stack, Prefix prefix) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getPrefix() == prefix;
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return stack.getItem() instanceof MaterialItem && ((MaterialItem) stack.getItem()).getMaterial() == material;
    }

    public static ItemStack get(Prefix prefix, Material material, int count) {
        ItemStack stack = STACK_LOOKUP.get(prefix.getName() + material.getName()).get().copy();
        stack.setCount(count);
        return stack;
    }

    public static Collection<GTItemStack> getAll() {
        return STACK_LOOKUP.values();
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) { //layer0
                if (stack.getItem() instanceof MaterialItem) {
                    Material material = ((MaterialItem) stack.getItem()).getMaterial();
                    if (material != null) {
                        return material.getRGB();
                    }
                }
            }
            return -1;
        }
    }
}
