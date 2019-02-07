package muramasa.gregtech.api.items;

import muramasa.gregtech.api.capability.ICoverable;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.materials.GTItemStack;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.creativetab.GregTechTab;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;

public class MetaItem extends Item {

    private static LinkedHashMap<String, GTItemStack> stringToStack = new LinkedHashMap<>();
    private static Prefix[] generatedPrefixes;

    public MetaItem() {
        setRegistryName("meta_item");
        setUnlocalizedName(Ref.MODID + ".meta_item");
        setCreativeTab(Ref.TAB_MATERIALS);
        setHasSubtypes(true);
        generatedPrefixes = Prefix.values();
        for (int p = 0; p < generatedPrefixes.length; p++) {
            for (int m = 0; m < Materials.generated.length; m++) {
                if (Materials.generated[m] == null || !generatedPrefixes[p].allowGeneration(Materials.generated[m])) continue;
                ItemStack stack = new ItemStack(this, 1, (p * 1000) + Materials.generated[m].getId());
                stringToStack.put(generatedPrefixes[p].getName() + Materials.generated[m].getName(), new GTItemStack(stack, generatedPrefixes[p].isVisible()));
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("materials")) {
                for (GTItemStack stack : stringToStack.values()) {
                    if (stack.isVisible()) {
                        subItems.add(stack.get());
                    }
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Prefix prefix = getPrefix(stack);
        Material material = getMaterial(stack);
        if (prefix != null && material != null) {
            return prefix.getDisplayName(material);
        }
        return "DISPLAY NAME ERROR";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Material material = getMaterial(stack);
        if (material != null && material.getElement() != null) {
            tooltip.add(material.getElement().getDisplayName());
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            if (tile.hasCapability(ITechCapabilities.COVERABLE, null)) {
                if (hasPrefix(stack, Prefix.PLATE)) {
                    ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, facing);
                    coverHandler.setCover(facing, CoverType.BLANK);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public static ItemStack get(Prefix prefix, Material material, int amount) {
        GTItemStack stack = stringToStack.get(prefix.getName() + material.getName());
        if (stack != null) {
            ItemStack copy = stack.get().copy();
            copy.setCount(amount);
            return copy;
        }
        System.err.println("get() NULL: " + prefix.getName() + " " + material.getName());
        return null;
    }

    public static Material getMaterial(ItemStack stack) {
        return Materials.generated[stack.getMetadata() % 1000];
    }

    public static Prefix getPrefix(ItemStack stack) {
        return generatedPrefixes[stack.getMetadata() / 1000];
    }

    public static boolean hasMaterial(ItemStack stack, Material material) {
        return getMaterial(stack) == material;
    }

    public static boolean hasPrefix(ItemStack stack, Prefix prefix) {
        return getPrefix(stack) == prefix;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (GTItemStack stack : stringToStack.values()) {
            Material material = getMaterial(stack.get());
            Prefix prefix = getPrefix(stack.get());
            if (material == null || prefix == null) continue;
            ModelLoader.setCustomModelResourceLocation(this, stack.get().getMetadata(), new ModelResourceLocation(Ref.MODID + ":material_set/" + material.getSet(), material.getSet() + "=" + prefix));
        }
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (tintIndex == 0) { //layer0
                Material material = getMaterial(stack);
                return material != null ? material.getRGB() : -1;
            }
            return -1;
        }
    }
}
